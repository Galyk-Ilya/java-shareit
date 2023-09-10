package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class BookingEnterDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    Status status;
}