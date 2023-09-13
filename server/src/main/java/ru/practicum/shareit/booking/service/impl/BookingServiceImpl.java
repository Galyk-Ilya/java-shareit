package ru.practicum.shareit.booking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEnterDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusErrorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(Long id, BookingEnterDto bookingEnterDto) {
        bookingEnterDto.setBookerId(id);
        bookingEnterDto.setStatus(Status.WAITING);

        Item item = itemRepository.findById(bookingEnterDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + bookingEnterDto.getItemId() + " was not found."));

        User booker = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("You don't have enough rights."));

        if (id.equals(item.getOwner().getId())) {
            throw new NotFoundException("The owner of the item cannot apply for its reservation.");
        }

        if (!item.getAvailable()) {
            throw new IncorrectDateError("Item is not available for reservation");
        }

        if (bookingEnterDto.getEnd() == null || bookingEnterDto.getStart() == null ||
                bookingEnterDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingEnterDto.getEnd().isBefore(bookingEnterDto.getStart()) ||
                bookingEnterDto.getEnd().isEqual(bookingEnterDto.getStart())) {
            throw new IncorrectDateError("The time is not set correctly.");
        }

        Booking savedBooker = bookingRepository.save(bookingMapper.toBookingForCreate(item, booker, bookingEnterDto));
        return bookingMapper.toBookingDto(savedBooker);
    }

    @Override
    @Transactional
    public BookingDto approvedBooking(Long idBooking, Long idOwner, Boolean status) {
        User user = userRepository.findById(idOwner)
                .orElseThrow(() -> new NotFoundException("You don't have enough rights."));

        Booking booking = bookingRepository.findById(idBooking)
                .orElseThrow(() -> new NotFoundException("Reservation with ID " + idBooking + " not found."));

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Thing with id " + booking.getItem().getId() + " not found."));

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new IncorrectDateError("Reservation with id =" + booking.getId() + " already confirmed.");
        }

        if (item.getOwner().getId().equals(idOwner)) {
            if (!status) {
                bookingRepository.approvedBooking(Status.REJECTED, idBooking);
                booking.setStatus(Status.REJECTED);
                return bookingMapper.toBookingDto(bookingRepository.getById(idBooking));
            }
            bookingRepository.approvedBooking(Status.APPROVED, idBooking);
            booking.setStatus(Status.APPROVED);
            return bookingMapper.toBookingDto(bookingRepository.getById(idBooking));
        }
        throw new NotFoundException("You don't have enough rights.");
    }


    @Override
    @Transactional
    public BookingDto findBookingById(Long idUser, Long idBooking) {
        userValidateExist(idUser);

        Booking booking = bookingRepository.findById(idBooking)
                .orElseThrow(() -> new NotFoundException("Booking with id: " + idBooking + " does not exist."));

        Optional<Item> item = itemRepository.findById(booking.getItem().getId());
        if (item.isPresent() && (booking.getBooker().getId().equals(idUser) || item.get().getOwner().getId().equals(idUser))) {
            return bookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("You don't have enough rights.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByIdUser(Long idUser, String stringState, Pageable pageable) {
        BookingState state = validationState(stringState);
        userValidateExist(idUser);

        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findByBookerIdOrderByStartDesc(idUser, pageable).getContent());
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(idUser, Status.WAITING, pageable).getContent());
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(idUser, Status.REJECTED, pageable).getContent());
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        idUser, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        idUser, LocalDateTime.now(), pageable).getContent());
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        idUser, LocalDateTime.now(), pageable).getContent());
                break;
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByIdOwner(Long idOwner, String stringState, Pageable pageable) {
        BookingState state = validationState(stringState);
        userValidateExist(idOwner);

        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(idOwner, pageable).getContent());
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(idOwner, Status.WAITING, pageable).getContent());
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(idOwner, Status.REJECTED, pageable).getContent());
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(idOwner, LocalDateTime.now(), LocalDateTime.now(), pageable).getContent());
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(idOwner, LocalDateTime.now(), pageable).getContent());
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(idOwner, LocalDateTime.now(), pageable).getContent());
                break;
        }
        return bookings.stream()
                .map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private BookingState validationState(String stringState) {
        try {
            return BookingState.valueOf(stringState);
        } catch (RuntimeException e) {
            throw new StatusErrorException("Unknown state: " + stringState);
        }
    }

    private void userValidateExist(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new NotFoundException("There is no user with id: " + idUser);
        }
    }
}