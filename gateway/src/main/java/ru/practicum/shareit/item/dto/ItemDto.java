package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "The title cannot be empty")
    @Size(min = 1, max = 30)
    private String name;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 1, max = 30)
    private String description;

    @NotNull(message = "The available field cannot be empty")
    private Boolean available;

    private Long ownerId;

    private Long requestId;
}