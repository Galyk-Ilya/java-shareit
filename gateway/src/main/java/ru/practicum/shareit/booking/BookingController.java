package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.exception.StatusErrorException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String userIdHeader = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> createBooking(@Positive @RequestHeader(value = userIdHeader) Long idBooker,
                                                @RequestBody @Valid BookItemRequestDto bookItemRequestDto) {
        return bookingClient.createBooking(idBooker, bookItemRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@Positive @RequestHeader(value = userIdHeader) Long idOwner,
                                                  @Positive @PathVariable("bookingId") Long bookingId,
                                                  @RequestParam(required = false) Boolean approved) {
        return bookingClient.approvedBooking(bookingId, idOwner, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllBookingsByIdOwner(@Positive @RequestHeader(value = userIdHeader) Long idOwner,
                                                           @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                           @PositiveOrZero @RequestParam(name = "from",
                                                                   defaultValue = "0") Integer from,
                                                           @PositiveOrZero @RequestParam(name = "size",
                                                                   defaultValue = "10") Integer size) {
        BookingState stateValid = BookingState.from(state)
                .orElseThrow(() -> new StatusErrorException("Unknown state: " + state));
        return bookingClient.findAllBookingsByIdOwner(idOwner, stateValid, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookingsByIdUser(@Positive @RequestHeader(value = userIdHeader) Long idUser,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState stateValid = BookingState.from(state)
                .orElseThrow(() -> new StatusErrorException("Unknown state: " + state));
        return bookingClient.findAllBookingsByIdUser(idUser, stateValid, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@Positive @RequestHeader(value = userIdHeader) Long idUser,
                                                  @Positive @PathVariable Long bookingId) {
        return bookingClient.findBookingById(idUser, bookingId);
    }
}