package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
public class ItemRequestDto {

    @Positive
    private Long id;

    private String description;

    private User requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}