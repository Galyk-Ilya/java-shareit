package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemShortDto item;
    User booker;
    Status status;
}