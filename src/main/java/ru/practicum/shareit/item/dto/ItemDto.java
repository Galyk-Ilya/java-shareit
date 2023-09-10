package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    Long id;

    @NotBlank
    @Size(min = 1, max = 30)
    String name;

    @NotBlank
    @Size(min = 1, max = 30)
    String description;

    @NotNull
    Boolean available;
    Long requestId;
}