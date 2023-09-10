package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.service.MyConstants.USER_ID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto item,
                              @RequestHeader(value = USER_ID) Long id) {
        return itemService.createItem(item, id);
    }

    @PatchMapping("/{id}")
    public ItemPatchDto updateItem(@PathVariable(name = "id") Long idItem,
                                   @Valid @RequestBody ItemPatchDto itemPatchDto,
                                   @RequestHeader(value = USER_ID) Long idOwner) {
        return itemService.updateItem(itemPatchDto, idItem, idOwner);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto findItemById(@RequestHeader(value = USER_ID) Long idOwner,
                                     @PathVariable(name = "id") Long id) {
        return itemService.findItemById(idOwner, id);
    }

    @GetMapping
    public List<ItemOwnerDto> findItemsByIdOwner(@RequestHeader(value = USER_ID) Long idOwner,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                 @Positive Integer index,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                 @Positive Integer size) {
        return itemService.findItemsByIdOwner(idOwner, PageRequest.of(index, size));
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@RequestParam String text,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         @Positive Integer index,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         @Positive Integer size) {
        return itemService.findItemsByText(text, PageRequest.of(index, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = USER_ID) Long userId,
                                 @PathVariable(name = "itemId") Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}