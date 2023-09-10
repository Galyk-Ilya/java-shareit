package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@Builder
public class ItemPatchDto {
    Long id;
    String name;
    String description;
    Boolean available;
    ItemRequest request;
    Long owner;
}