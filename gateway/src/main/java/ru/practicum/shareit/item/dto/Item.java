package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank(message = "The name cannot be empty")
    private String name;

    @NotBlank(message = "Description must be filled in")
    private String description;

    @NotNull(message = "The item availability field is not checked")
    private Boolean available;

    private Long owner;

    private Long requestId;
}