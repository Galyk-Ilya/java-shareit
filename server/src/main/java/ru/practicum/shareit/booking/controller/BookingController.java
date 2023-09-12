package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEnterDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectDateError;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(value = userIdHeader) Long idBooker,
                                    @RequestBody BookingEnterDto bookingEnterDto) {
        return bookingService.createBooking(idBooker, bookingEnterDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeApproved(@RequestHeader(value = userIdHeader) Long idOwner,
                                     @PathVariable("bookingId") Long bookingId,
                                     @RequestParam("approved") Boolean approved) {
        return bookingService.approvedBooking(bookingId, idOwner, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByIdOwner(@RequestHeader(value = userIdHeader) Long idOwner,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(name = "from", defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.findAllBookingsByIdOwner(idOwner, state, PageRequest.of(page, size, Sort.by("start").descending()));
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByIdUser(@RequestHeader(value = userIdHeader) Long idUser,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") Integer page,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (page < 0) {
            throw new IncorrectDateError("");
        }
        page = page / size;
        return bookingService.findAllBookingsByIdUser(idUser, state, PageRequest.of(page, size, Sort.by("start").descending()));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(value = userIdHeader) Long idUser,
                                      @PathVariable Long bookingId) {
        return bookingService.findBookingById(idUser, bookingId);
    }
}