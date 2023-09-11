package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.bookingRepository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking createBooking(Booking booking, long userId) {
        booking.setBooker(userService.getUserIfExistOrThrow(userId));
        booking.setItem(itemService.getItemIfExistOrThrow(booking.getItem().getId()));
        booking.setStatus(BookingStatus.WAITING);
        if (booking.getItem().getOwner() == userId) {
            throw new NotFoundException("The owner cannot book his item");
        }
        if (booking.getItem().getAvailable().equals(false)) {
            throw new BadRequestException("Item not available for order");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new BadRequestException("Booking end date cannot be earlier than booking start date or early");
        }
        booking.setStatus(BookingStatus.WAITING);
        log.info("User with id: {} added an item to the booking with id: {}", userId, booking.getItem().getId());
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(long bookingId, long userId) {
        userService.getUserIfExistOrThrow(userId);
        Booking booking = getBookingIfExistOrThrow(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new NotFoundException("You are not the owner of the booking or item");
        }
        return booking;
    }

    public Booking approvedBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId, userId);
        if (booking.getItem().getOwner() != userId) {
            throw new NotFoundException("You don't have an item with id" + booking.getItem().getId());
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved) {
            throw new BadRequestException("Booking already confirmed");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.info("Change of booking status");
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookingsByBookerIdAndState(long userId, String state) {
        userService.getUserIfExistOrThrow(userId);
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        switch (bookingState) {
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByBookerIdAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerIdAndState(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerIdAndState(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> getAllBookingsByOwnerIdAndState(long userId, String state) {
        userService.getUserIfExistOrThrow(userId);
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        switch (bookingState) {
            case ALL:
                return bookingRepository.findByItemOwnerOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findByItemOwnerAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByItemOwnerAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemOwnerAndState(userId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByItemOwnerAndState(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    private Booking getBookingIfExistOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking with id " + bookingId + " does not exist in the system");
        });
    }
}