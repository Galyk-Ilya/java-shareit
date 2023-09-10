package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEnterDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long id, BookingEnterDto bookingEnterDto);

    BookingDto approvedBooking(Long idBooking, Long idOwner, Boolean status);

    BookingDto findBookingById(Long idUser, Long idBooking);

    List<BookingDto> findAllBookingsByIdUser(Long idUser, String state, Pageable pageable);

    List<BookingDto> findAllBookingsByIdOwner(Long idOwner, String state, Pageable pageable);
}