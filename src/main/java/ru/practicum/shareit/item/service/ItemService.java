package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Long idUser);

    ItemPatchDto updateItem(ItemPatchDto itemPatchDto, Long idItem, Long idOwner);

    ItemOwnerDto findItemById(Long idOwner, Long id);

    List<ItemOwnerDto> findItemsByIdOwner(Long idOwner, Pageable pageable);

    List<ItemDto> findItemsByText(String text, Pageable pageable);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}