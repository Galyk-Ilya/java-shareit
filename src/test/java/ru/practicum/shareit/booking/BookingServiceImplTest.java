package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEnterDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusErrorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    @Autowired
    private final BookingServiceImpl bookingService;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void createBookingTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        BookingEnterDto bookingDto = BookingEnterDto.builder()
                .status(Status.WAITING)
                .itemId(1L)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto ans = bookingService.createBooking(2L, bookingDto);
        assertThat(ans, is(notNullValue()));
        assertThat(ans.getId(), is(1L));
        assertThat(ans.getStatus(), is(Status.WAITING));
    }

    @Test
    void dropExceptionCreateBookingItemNotExistOrUserNotExist() {
        BookingEnterDto bookingDto = BookingEnterDto.builder()
                .status(Status.WAITING)
                .itemId(1L)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        NotFoundException itemNotExistError;
        itemNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingDto));
        assertThat(itemNotExistError.getMessage(), is("Item with id " + bookingDto.getItemId() + " was not found."));

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(Item.builder().build()));
        NotFoundException userNotExistError;
        userNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(2L, bookingDto));
        assertThat(userNotExistError.getMessage(), is("You don't have enough rights."));
    }

    @Test
    void dropExceptionCreateBookingOwnerCreateBooking() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        BookingEnterDto bookingDto = BookingEnterDto.builder()
                .status(Status.WAITING)
                .itemId(1L)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(owner));

        NotFoundException ownerCreateBookerError;
        ownerCreateBookerError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDto));
        assertThat(ownerCreateBookerError.getMessage(), is("The owner of the item cannot apply for its reservation."));
    }

    @Test
    void dropExceptionBookingCreateIfItemNotAvailable() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        User user = User.builder()
                .id(2L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        BookingEnterDto bookingDto = BookingEnterDto.builder()
                .status(Status.WAITING)
                .itemId(1L)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(false)
                .owner(owner)
                .build();

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        IncorrectDateError itemNotAvailableError;

        itemNotAvailableError = Assertions.assertThrows(IncorrectDateError.class,
                () -> bookingService.createBooking(2L, bookingDto));
        assertThat(itemNotAvailableError.getMessage(), is("Item is not available for reservation"));
    }

    @Test
    void dropExceptionCreateBookingWrongTime() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        User user = User.builder()
                .id(2L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        BookingEnterDto bookingDto = BookingEnterDto.builder()
                .status(Status.WAITING)
                .itemId(1L)
                .start(LocalDateTime.now().minusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        IncorrectDateError wrongTimeError;
        wrongTimeError = Assertions.assertThrows(IncorrectDateError.class,
                () -> bookingService.createBooking(2L, bookingDto));
        assertThat(wrongTimeError.getMessage(), is("The time is not set correctly."));
    }

    @Test
    void dropExceptionApprovedBooking() {
        NotFoundException notOwnerError;
        notOwnerError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approvedBooking(1L, 1L, false));
        assertThat(notOwnerError.getMessage(), is("You don't have enough rights."));

        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        NotFoundException bookingNotExistError;
        bookingNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approvedBooking(1L, 1L, false));
        assertThat(bookingNotExistError.getMessage(), is("Reservation with ID " + 1L + " not found."));

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        NotFoundException itemNotExistError;
        itemNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approvedBooking(1L, 1L, false));
        assertThat(itemNotExistError.getMessage(), is("Thing with id " + 1L + " not found."));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        booking.setStatus(Status.APPROVED);

        IncorrectDateError doubleStateError;
        doubleStateError = Assertions.assertThrows(IncorrectDateError.class,
                () -> bookingService.approvedBooking(1L, 1L, true));
        assertThat(doubleStateError.getMessage(), is("Reservation with id =" + booking.getId() + " already confirmed."));
    }

    @Test
    void approvedBookingToApproveTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(bookingRepository.getById(1L))
                .thenReturn(booking);
        Mockito.doNothing().when(bookingRepository).approvedBooking(Status.APPROVED, 1L);
        BookingDto ans = bookingService.approvedBooking(1L, 1L, true);
        assertThat(ans.getStatus(), is(Status.APPROVED));
    }

    @Test
    void approvedBookingToRejectedTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(bookingRepository.getById(1L))
                .thenReturn(booking);

        Mockito.doNothing().when(bookingRepository).approvedBooking(Status.REJECTED, 1L);
        BookingDto ans = bookingService.approvedBooking(1L, 1L, false);
        assertThat(ans.getStatus(), is(Status.REJECTED));
    }

    @Test
    void dropExceptionApproveBookingNoOwnerTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        NotFoundException notOwnerError;
        notOwnerError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approvedBooking(1L, 2L, true));
        assertThat(notOwnerError.getMessage(), is("You don't have enough rights."));
    }

    @Test
    void dropExceptionBookingNotExistFindBookingByIdTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        NotFoundException bookingNotExistError;
        bookingNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(1L, 1L));
        assertThat(bookingNotExistError.getMessage(), is("Booking with id: " + 1L + " does not exist."));
    }

    @Test
    void dropExceptionNoOwnerFindBookingByIdTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
        when(userRepository.existsById(any()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotFoundException notOwnerError;
        notOwnerError = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(2L, 1L));
        assertThat(notOwnerError.getMessage(), is("You don't have enough rights."));
    }

    @Test
    void findAllBookingsByIdUserTest() {
        Pageable pageable = Pageable.unpaged();
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));

        List<BookingDto> ans = bookingService.findAllBookingsByIdUser(1L, "ALL", Pageable.unpaged());
        assertThat(ans, is(notNullValue()));

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, Status.WAITING, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansWaiting = bookingService.findAllBookingsByIdUser(1L, "WAITING", Pageable.unpaged());
        assertThat(ansWaiting, is(notNullValue()));

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, Status.REJECTED, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansRejected = bookingService.findAllBookingsByIdUser(1L, "REJECTED", Pageable.unpaged());
        assertThat(ansRejected, is(notNullValue()));

        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(),
                any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansCurrent = bookingService.findAllBookingsByIdUser(1L, "CURRENT", Pageable.unpaged());
        assertThat(ansCurrent, is(notNullValue()));

        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansPast = bookingService.findAllBookingsByIdUser(1L, "PAST", Pageable.unpaged());
        assertThat(ansPast, is(notNullValue()));

        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansFuture = bookingService.findAllBookingsByIdUser(1L, "FUTURE", Pageable.unpaged());
        assertThat(ansFuture, is(notNullValue()));
    }

    @Test
    void findAllBookingsByIdOwnerTest() {
        Pageable pageable = Pageable.unpaged();
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.WAITING)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansAll = bookingService.findAllBookingsByIdOwner(1L, "ALL", pageable);
        assertThat(ansAll, is(notNullValue()));

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansWaiting = bookingService.findAllBookingsByIdOwner(1L, "WAITING", pageable);
        assertThat(ansWaiting, is(notNullValue()));

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansRejected = bookingService.findAllBookingsByIdOwner(1L, "REJECTED", pageable);
        assertThat(ansRejected, is(notNullValue()));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansCurrent = bookingService.findAllBookingsByIdOwner(1L, "CURRENT", pageable);
        assertThat(ansCurrent, is(notNullValue()));

        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansPast = bookingService.findAllBookingsByIdOwner(1L, "PAST", pageable);
        assertThat(ansPast, is(notNullValue()));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(booking)));
        List<BookingDto> ansFuture = bookingService.findAllBookingsByIdOwner(1L, "FUTURE", pageable);
        assertThat(ansFuture, is(notNullValue()));
    }

    @Test
    void wrongState() {
        StatusErrorException statusErrorException;
        statusErrorException = Assertions.assertThrows(StatusErrorException.class,
                () -> bookingService.findAllBookingsByIdUser(1L, "WrongState", Pageable.unpaged()));
        assertThat(statusErrorException.getMessage(), is("Unknown state: WrongState"));
    }
}