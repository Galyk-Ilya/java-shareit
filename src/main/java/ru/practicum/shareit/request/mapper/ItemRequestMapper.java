package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .items(new ArrayList<>())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requestor) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requestor(requestor)
                .created(requestDto.getCreated())
                .build();
    }
}