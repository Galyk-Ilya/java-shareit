package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.service.MyConstants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID) long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        return bookingMapper.toBookingDto(bookingService.createBooking(bookingMapper.toBooking(bookingDto), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader(USER_ID) long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        return bookingMapper.toBookingDto(bookingService.approvedBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID) long userId,
                                     @PathVariable long bookingId) {
        return bookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader(USER_ID) long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByBookerIdAndState(userId, state).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItemsOwnerId(@RequestHeader(USER_ID) long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByOwnerIdAndState(userId, state).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}