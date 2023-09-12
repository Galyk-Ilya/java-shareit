package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto addRequest(ItemRequestDto requestDto, Long idRequestor, LocalDateTime time) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()
                || requestDto.getDescription().isBlank()) {
            throw new IncorrectDateError("The description cannot be left blank");
        }
        User requester = userRepository.findById(idRequestor)
                .orElseThrow(() -> new NotFoundException("Incorrect user ID information."));
        ItemRequest addedRequest = itemRequestMapper.toItemRequest(requestDto, requester);
        addedRequest.setCreated(time);
        addedRequest = requestRepository.save(addedRequest);
        return itemRequestMapper.toItemRequestDto(addedRequest);
    }

    @Override
    public List<ItemRequestDto> findAllRequests(Long idRequestor) {
        if (!userRepository.existsById(idRequestor)) {
            throw new NotFoundException("Incorrect user ID information.");
        }
        List<ItemRequest> itemRequests = new ArrayList<>();
        List<Long> idsItems = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        List<ItemDto> itemsForAns = new ArrayList<>();
        itemRequests.addAll(requestRepository.findByRequestorIdOrderByCreatedDesc(idRequestor));
        List<ItemRequestDto> ans = itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        idsItems.addAll(ans.stream()
                .map(ItemRequestDto::getId).collect(Collectors.toList()));

        items.addAll(itemRepository.findByRequestIdIn(idsItems));
        itemsForAns.addAll(items.stream().map(itemMapper::toItemDto).collect(Collectors.toList()));

        for (ItemRequestDto request : ans) {
            for (ItemDto item : itemsForAns) {
                if (request.getId().equals(item.getRequestId())) {
                    request.getItems().add(item);
                }
            }
        }
        return ans;
    }

    @Override
    public List<ItemRequestDto> findAllForeignRequests(Long idUser, Pageable pageable) {
        if (!userRepository.existsById(idUser)) {
            throw new NotFoundException("Incorrect user ID information.");
        }
        List<ItemRequestDto> ans = requestRepository.findByRequestorIdNot(idUser, pageable).stream()
                .map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        List<Long> idsItems = new ArrayList<>();
        idsItems.addAll(ans.stream().map(ItemRequestDto::getId).collect(Collectors.toList()));

        List<Item> items = itemRepository.findByRequestIdIn(idsItems);

        List<ItemDto> itemsForAns = items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());

        for (ItemRequestDto request : ans) {
            for (ItemDto item : itemsForAns) {
                if (request.getId().equals(item.getRequestId())) {
                    request.getItems().add(item);
                }
            }
        }
        return ans;
    }

    @Override
    public ItemRequestDto findRequestById(Long idRequest, Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new NotFoundException("Incorrect user ID information.");
        }
        ItemRequest itemRequest = requestRepository.findById(idRequest)
                .orElseThrow(() -> new NotFoundException("The request with id " + idRequest + " does not exist."));

        List<Item> items = itemRepository.findByRequestId(idRequest);
        List<ItemDto> itemsDto = items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());

        ItemRequestDto ans = itemRequestMapper.toItemRequestDto(itemRequest);
        ans.setItems(itemsDto);
        return ans;
    }
}