package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.Optional;

@Component
public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null))
                .build();
    }

    public static ItemPatchDto toItemPatchDto(Item item) {
        return ItemPatchDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
    }

    public static Item toUp(Item updateItem, ItemPatchDto itemDto) {
        if (itemDto.getName() != null) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            updateItem.setDescription(itemDto.getDescription());
        }
        return updateItem;
    }

    public static ItemShortDto toShortItem(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static ItemOwnerDto toItemOwnerDto(Item item) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .request(item.getRequest())
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();
    }
}