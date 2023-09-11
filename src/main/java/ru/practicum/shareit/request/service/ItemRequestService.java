package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDto requestDto, Long idRequestor, LocalDateTime time);

    List<ItemRequestDto> findAllRequests(Long idRequestor);

    List<ItemRequestDto> findAllForeignRequests(Long idUser, Pageable pageable);

    ItemRequestDto findRequestById(Long idRequest, Long idUser);

}
