package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {
    @Autowired
    private final ItemRequestServiceImpl requestService;

    @MockBean
    private final ItemRequestRepository requestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void dropExceptionAddRequestDescriptionNull() {
        IncorrectDateError descriptionIsNull;
        descriptionIsNull = Assertions.assertThrows(IncorrectDateError.class,
                () -> requestService.addRequest(ItemRequestDto.builder().build(), 1L, LocalDateTime.now()));
        assertThat(descriptionIsNull.getMessage(), is("The description cannot be left blank"));
    }

    @Test
    void dropExceptionAddRequestDescriptionEmpty() {
        IncorrectDateError descriptionIsEmpty;
        descriptionIsEmpty = Assertions.assertThrows(IncorrectDateError.class,
                () -> requestService.addRequest(ItemRequestDto.builder().description("").build(), 1L, LocalDateTime.now()));
        assertThat(descriptionIsEmpty.getMessage(), is("The description cannot be left blank"));
    }

    @Test
    void dropExceptionAddRequestDescriptionBlank() {
        IncorrectDateError descriptionIsBlank;
        descriptionIsBlank = Assertions.assertThrows(IncorrectDateError.class,
                () -> requestService.addRequest(ItemRequestDto.builder().description(" ").build(), 1L, LocalDateTime.now()));
        assertThat(descriptionIsBlank.getMessage(), is("The description cannot be left blank"));
    }

    @Test
    void dropExceptionAddRequestRequestorNotExist() {
        NotFoundException requestorNotExistError;
        requestorNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.addRequest(ItemRequestDto.builder().description("Description").build(), 1L, LocalDateTime.now()));
        assertThat(requestorNotExistError.getMessage(), is("Incorrect user ID information."));
    }

    @Test
    void addRequestTest() {
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();
        ItemRequestDto itemRequest = ItemRequestDto.builder()
                .created(LocalDateTime.now())
                .description("Description")
                .requestor(requestor)
                .build();

        when((userRepository.findById(any())))
                .thenReturn(Optional.of(requestor));

        when(requestRepository.save(any()))
                .thenReturn(ItemRequest.builder().id(1L).description("Description").build());
        ItemRequestDto ans = requestService.addRequest(itemRequest, 1L, LocalDateTime.now());
        assertThat(ans, is(notNullValue()));
        assertThat(ans.getDescription(), is("Description"));
    }

    @Test
    void dropExceptionFindAllRequestsUserNotExist() {
        NotFoundException userIsNotExistError;
        userIsNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.findAllRequests(1L));
        assertThat(userIsNotExistError.getMessage(), is("Incorrect user ID information."));
    }

    @Test
    void findAllRequests() {
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Description")
                .requestor(requestor)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("Description2")
                .requestor(requestor)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .request(itemRequest)
                .owner(requestor)
                .build();

        when((userRepository.existsById(any())))
                .thenReturn(true);
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(Arrays.asList(itemRequest, itemRequest2));
        when(itemRepository.findByRequestIdIn(any()))
                .thenReturn(Arrays.asList(item));

        List<ItemRequestDto> ans = requestService.findAllRequests(1L);
        assertThat(ans.size(), is(2));
    }

    @Test
    void dropExceptionFindAllForeignRequestsUserNoExist() {
        NotFoundException userIsNotExistError;
        userIsNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.findAllForeignRequests(1L, Pageable.unpaged()));
        assertThat(userIsNotExistError.getMessage(), is("Incorrect user ID information."));
    }

    @Test
    void findAllForeignRequestTest() {
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Description")
                .requestor(requestor)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("Description2")
                .requestor(requestor)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .request(itemRequest)
                .owner(requestor)
                .build();

        when((userRepository.existsById(any())))
                .thenReturn(true);
        when(requestRepository.findByRequestorIdNot(any(), any()))
                .thenReturn(Arrays.asList(itemRequest, itemRequest2));
        when(itemRepository.findByRequestIdIn(any()))
                .thenReturn(Arrays.asList(item));
        List<ItemRequestDto> ans = requestService.findAllForeignRequests(1L, Pageable.unpaged());
        assertThat(ans.size(), is(2));
    }

    @Test
    void dropExceptionFindRequestByIdUserNotExist() {
        NotFoundException userIsNotExistError;
        userIsNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.findRequestById(1L, 1L));
        assertThat(userIsNotExistError.getMessage(), is("Incorrect user ID information."));
    }

    @Test
    void dropExceptionFindRequestByIdItemNotExist() {
        when((userRepository.existsById(any())))
                .thenReturn(true);

        NotFoundException itemIsNotExistError;
        itemIsNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.findRequestById(1L, 1L));
        assertThat(itemIsNotExistError.getMessage(), is("The request with id " + 1L + " does not exist."));
    }

    @Test
    void findRequestByIdTest() {
        User requestor = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Description")
                .requestor(requestor)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(requestor)
                .build();

        when((userRepository.existsById(any())))
                .thenReturn(true);

        when(requestRepository.findById(any()))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.findByRequestId(any()))
                .thenReturn(Arrays.asList(item));

        ItemRequestDto ans = requestService.findRequestById(1L, 1L);
        assertThat(ans.getId(), is(1L));
        assertThat(ans, is(notNullValue()));
    }
}