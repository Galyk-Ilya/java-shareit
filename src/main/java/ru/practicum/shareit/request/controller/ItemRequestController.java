package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userId = "X-Sharer-User-Id";

    @PostMapping
    ItemRequestDto addRequest(@RequestHeader(value = userId) Long idRequestor,
                              @RequestBody @Valid ItemRequestDto requestDto) {
        return itemRequestService.addRequest(requestDto, idRequestor, LocalDateTime.now());
    }

    @GetMapping
    List<ItemRequestDto> findAllRequests(@RequestHeader(value = userId) Long idRequestor) {
        return itemRequestService.findAllRequests(idRequestor);
    }

    @GetMapping("/all")
    List<ItemRequestDto> findAllForeignRequests(@RequestHeader(value = userId) Long idUser,
                                                @RequestParam(name = "from", defaultValue = "0")
                                                @Positive Integer index,
                                                @RequestParam(name = "size", defaultValue = "10")
                                                @Positive Integer size) {

        return itemRequestService.findAllForeignRequests(idUser, PageRequest.of(index, size,
                Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    ItemRequestDto findRequestById(@RequestHeader(value = userId) Long idUser,
                                   @PathVariable(name = "requestId") Long requestId) {
        return itemRequestService.findRequestById(requestId, idUser);
    }
}