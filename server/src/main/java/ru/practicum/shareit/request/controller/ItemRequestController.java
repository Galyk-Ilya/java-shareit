package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";


    @PostMapping
    ItemRequestDto addRequest(@RequestHeader(value = userIdHeader) Long idRequestor,
                              @RequestBody ItemRequestDto requestDto) {
        return itemRequestService.addRequest(requestDto, idRequestor, LocalDateTime.now());
    }

    @GetMapping
    List<ItemRequestDto> findAllRequests(@RequestHeader(value = userIdHeader) Long idRequestor) {
        return itemRequestService.findAllRequests(idRequestor);
    }

    @GetMapping("/all")
    List<ItemRequestDto> findAllForeignRequests(@RequestHeader(value = userIdHeader) Long idUser,
                                                @RequestParam(name = "from", defaultValue = "0")
                                                Integer index,
                                                @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {

        return itemRequestService.findAllForeignRequests(idUser, PageRequest.of(index, size,
                Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    ItemRequestDto findRequestById(@RequestHeader(value = userIdHeader) Long idUser,
                                   @PathVariable(name = "requestId") Long requestId) {
        return itemRequestService.findRequestById(requestId, idUser);
    }
}