package ru.practicum.shareit.item.controller;
import static ru.practicum.shareit.service.MyConstants.USER_ID;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithComments;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID) long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemService.createItem(itemMapper.toItem(itemDto), userId));
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithComments getItemById(@RequestHeader(USER_ID) long userId,
                                           @PathVariable long itemId) {
        return itemMapper.toItemDtoWithComments(itemService.getItemById(itemId, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        return itemMapper.toItemDto(itemService.updateItem(itemMapper.toItem(itemDto), userId, itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return itemService.getItemsByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemDtoWithComments> getAllItemsByUserId(@RequestHeader(USER_ID) long userId) {
        return itemService.getAllItemsByUserId(userId).stream()
                .map(itemMapper::toItemDtoWithComments)
                .collect(Collectors.toList());
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody Comment comment) {
        return commentMapper.toCommentDto(itemService.createComment(userId, itemId, comment));
    }
}