package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto item,
                              @RequestHeader(value = userIdHeader) Long id) {
        return itemService.createItem(item, id);
    }

    @PatchMapping("/{id}")
    public ItemPatchDto updateItem(@PathVariable(name = "id") Long idItem,
                                   @RequestBody ItemPatchDto itemPatchDto,
                                   @RequestHeader(value = userIdHeader) Long idOwner) {
        return itemService.updateItem(itemPatchDto, idItem, idOwner);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto findItemById(@RequestHeader(value = userIdHeader) Long idOwner,
                                     @PathVariable(name = "id") Long id) {
        return itemService.findItemById(idOwner, id);
    }

    @GetMapping
    public List<ItemOwnerDto> findItemsByIdOwner(@RequestHeader(value = userIdHeader) Long idOwner,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                 Integer index,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                 Integer size) {
        return itemService.findItemsByIdOwner(idOwner, PageRequest.of(index, size));
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@RequestParam String text,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         Integer index,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         Integer size) {
        return itemService.findItemsByText(text, PageRequest.of(index, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = userIdHeader) Long userId,
                                 @PathVariable(name = "itemId") Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}