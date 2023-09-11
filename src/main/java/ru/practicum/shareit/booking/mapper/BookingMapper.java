package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEnterDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(ItemMapper.toShortItem(booking.getItem()))
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static Booking toBookingForCreate(Item item, User booker, BookingEnterDto bookingEnterDto) {
        return new Booking(item, booker, bookingEnterDto.getStart(),
                bookingEnterDto.getEnd(), bookingEnterDto.getStatus());
    }
}