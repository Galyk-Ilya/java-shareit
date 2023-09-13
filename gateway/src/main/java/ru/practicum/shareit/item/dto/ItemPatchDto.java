package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPatchDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private Long owner;
}