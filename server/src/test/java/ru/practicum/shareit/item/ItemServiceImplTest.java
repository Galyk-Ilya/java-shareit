package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessErrorException;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ItemServiceImplTest {

    @Autowired
    private final ItemServiceImpl itemServiceImpl;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    @MockBean
    private final ItemRequestRepository requestRepository;

    @Test
    void createItemTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        ItemDto itemDto = ItemDto.builder()
                .name("itemDto")
                .description("newItemDto")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.save(any()))
                .thenReturn(item);

        itemDto = itemServiceImpl.createItem(itemDto, 1L);
        assertThat(itemDto, is(notNullValue()));
        assertThat(itemDto.getId(), is(1L));
        assertThat(itemDto.getName(), is("item"));
        assertThat(itemDto.getDescription(), is("newItem"));
    }

    @Test
    void dropNotFoundExceptionTryCreateItemOwnerNotExist() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ItemDto itemDto = ItemDto.builder()
                .name("itemDto")
                .description("newItemDto")
                .available(true)
                .build();

        NotFoundException userNotFound;

        userNotFound = Assertions.assertThrows(NotFoundException.class,
                () -> itemServiceImpl.createItem(itemDto, 1L));
        assertThat(userNotFound.getMessage(), is("The user with id = " + 1L + " does not exist."));
    }

    @Test
    void dropInCorrectDataExceptionTryCreateItemRequestNotExist() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .name("itemDto")
                .description("newItemDto")
                .available(true)
                .requestId(1L)
                .build();

        when((userRepository.findById(1L)))
                .thenReturn(Optional.of(owner));

        when((itemRepository.findById(1L)))
                .thenReturn(Optional.of(item));

        when(requestRepository.findById(1L))
                .thenReturn(Optional.empty());

        IncorrectDateError requestNotFound;

        requestNotFound = Assertions.assertThrows(IncorrectDateError.class,
                () -> itemServiceImpl.createItem(itemDto, 1L));
        assertThat(requestNotFound.getMessage(), is("Request with id = " + 1L + " does not exist"));
    }

    @Test
    void updateItemTest() {
        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        ItemPatchDto itemPatchDto = ItemPatchDto.builder()
                .id(1L)
                .name("itemUpdate")
                .description("newItemUpdate")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        itemPatchDto = itemServiceImpl.updateItem(itemPatchDto, 1L, 1L);
        assertThat(itemPatchDto, is(notNullValue()));
        assertThat(itemPatchDto.getId(), is(1L));
        assertThat(itemPatchDto.getName(), is("itemUpdate"));
        assertThat(itemPatchDto.getDescription(), is("newItemUpdate"));
    }

    @Test
    void dropNotFoundExceptionUserNotExistTryUpdateItem() {

        User owner = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        ItemPatchDto itemDto = ItemPatchDto.builder()
                .id(1L)
                .name("itemDto")
                .description("newItemDto")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when((itemRepository.findById(1L)))
                .thenReturn(Optional.of(item));

        when(userRepository.existsById(1L))
                .thenReturn(false);

        NotFoundException userNotFound;

        userNotFound = Assertions.assertThrows(NotFoundException.class,
                () -> itemServiceImpl.updateItem(itemDto, 1L, 1L));
        assertThat(userNotFound.getMessage(), is("The user with id = " + 1L + " does not exist."));
    }

    @Test
    void dropNotFoundExceptionItemNotExistTryUpdateItem() {
        ItemPatchDto itemDto = ItemPatchDto.builder()
                .id(1L)
                .name("itemDto")
                .description("newItemDto")
                .available(true)
                .build();

        when((itemRepository.findById(1L)))
                .thenReturn(Optional.empty());

        when(userRepository.existsById(1L))
                .thenReturn(false);

        NotFoundException userNotFound;

        userNotFound = Assertions.assertThrows(NotFoundException.class,
                () -> itemServiceImpl.updateItem(itemDto, 1L, 1L));
        assertThat(userNotFound.getMessage(), is("Things with id " + 1L + " do not exist."));
    }

    @Test
    void dropAccessErrorExceptionTryUpdateItem() {
        User owner = User.builder()
                .id(2L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        ItemPatchDto itemDto = ItemPatchDto.builder()
                .id(1L)
                .owner(1L)
                .name("itemDto")
                .description("newItemDto")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when((itemRepository.findById(1L)))
                .thenReturn(Optional.of(item));

        when(userRepository.existsById(1L))
                .thenReturn(true);

        AccessErrorException accessErrorException;

        accessErrorException = Assertions.assertThrows(AccessErrorException.class,
                () -> itemServiceImpl.updateItem(itemDto, 1L, 1L));
        assertThat(accessErrorException.getMessage(), is("You don't have permission to edit"));
    }

    @Test
    void dropNotFoundExceptionTryFindItemsByIdOwner() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NotFoundException userNotFound;

        userNotFound = Assertions.assertThrows(NotFoundException.class,
                () -> itemServiceImpl.findItemsByIdOwner(1L, Pageable.unpaged()));
        assertThat(userNotFound.getMessage(), is("The user with id = " + 1L + " does not exist."));
    }

    @Test
    void findItemByIdTestIfUserNotOwner() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(commentRepository.findByItemId(1L))
                .thenReturn(new ArrayList<>());

        ItemOwnerDto itemDto = itemServiceImpl.findItemById(2L, 1L);
        assertThat(itemDto, is(notNullValue()));
        assertThat(itemDto.getId(), is(1L));
        assertThat(itemDto.getName(), is("item"));
        assertThat(itemDto.getDescription(), is("newItem"));
        assertThat(itemDto.getComments(), is(new ArrayList<>()));
        Object o = null;
        assertThat(itemDto.getLastBooking(), is(o));
        assertThat(itemDto.getNextBooking(), is(o));
    }

    @Test
    void findItemByIdTestIfUserIsOwner() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@test.ru")
                .build();

        User author = User.builder()
                .id(2L)
                .name("author")
                .email("author@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .created(LocalDateTime.now())
                .id(1L)
                .text("Good item")
                .author(author)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.APPROVED)
                .booker(author)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        LastBookingDto lastBooking = LastBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(commentRepository.findByItemId(1L))
                .thenReturn(Arrays.asList(comment));

        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.of(booking));

        ItemOwnerDto itemDto = itemServiceImpl.findItemById(1L, 1L);
        assertThat(itemDto, is(notNullValue()));
        assertThat(itemDto.getId(), is(1L));
        assertThat(itemDto.getName(), is("item"));
        assertThat(itemDto.getDescription(), is("newItem"));
        assertThat(itemDto.getComments().get(0).getText(), is("Good item"));
        assertThat(itemDto.getLastBooking().getId(), is(lastBooking.getId()));
        assertThat(itemDto.getNextBooking().getId(), is(lastBooking.getId()));
    }

    @Test
    void findItemsByIdOwnerTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .created(LocalDateTime.now())
                .id(1L)
                .text("Good item")
                .author(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.APPROVED)
                .booker(owner)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when((userRepository.findById(1L)))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findByOwner(owner, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(item)));

        when(commentRepository.findByItemId(1L))
                .thenReturn(Arrays.asList(comment));

        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.of(booking));

        List<ItemOwnerDto> itemsDto = itemServiceImpl.findItemsByIdOwner(1L, Pageable.unpaged());
        assertThat(itemsDto, is(notNullValue()));
        assertThat(itemsDto.size(), is(1));
        assertThat(itemsDto.get(0).getComments().get(0).getText(), is("Good item"));
        assertThat(itemsDto.get(0).getLastBooking().getId(), is(1L));
        assertThat(itemsDto.get(0).getNextBooking().getId(), is(1L));
    }

    @Test
    void findItemsByIdOwnerTestIfLastNextBookingNull() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@test.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .owner(owner)
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .created(LocalDateTime.now())
                .id(1L)
                .text("Good item")
                .author(owner)
                .build();

        Booking booking = null;

        when((userRepository.findById(1L)))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findByOwner(owner, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(item)));

        when(commentRepository.findByItemId(1L))
                .thenReturn(Arrays.asList(comment));

        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(Optional.empty());

        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.empty());

        List<ItemOwnerDto> itemsDto = itemServiceImpl.findItemsByIdOwner(1L, Pageable.unpaged());
        assertThat(itemsDto, is(notNullValue()));
        assertThat(itemsDto.size(), is(1));
        assertThat(itemsDto.get(0).getComments().get(0).getText(), is("Good item"));
        assertThat(itemsDto.get(0).getLastBooking(), is(booking));
        assertThat(itemsDto.get(0).getNextBooking(), is(booking));
    }

    @Test
    void findItemByTextTestIfTextEmpty() {
        List<ItemDto> ans = itemServiceImpl.findItemsByText("", Pageable.unpaged());
        assertThat(ans, is(notNullValue()));
        assertThat(ans.size(), is(0));
    }

    @Test
    void findItemByTextTest() {
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("newItem")
                .available(true)
                .build();

        when(itemRepository.findItemsByText("item", Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(item)));

        List<ItemDto> ans = itemServiceImpl.findItemsByText("item", Pageable.unpaged());
        assertThat(ans, is(notNullValue()));
        assertThat(ans.size(), is(1));
    }

    @Test
    void dropExceptionAddCommentNoTextTest() {
        IncorrectDateError commentEmptyError;

        commentEmptyError = Assertions.assertThrows(IncorrectDateError.class,
                () -> itemServiceImpl.addComment(1L, 1L, CommentDto.builder().build()));
        assertThat(commentEmptyError.getMessage(), is("The comment must not be empty"));
    }

    @Test
    void dropExceptionAddCommentTest() {
        CommentDto comment = CommentDto.builder()
                .created(LocalDateTime.now())
                .id(1L)
                .text("Good item")
                .build();

        NotFoundException itemNotExistError;

        itemNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> itemServiceImpl.addComment(1L, 1L, comment));
        assertThat(itemNotExistError.getMessage(), is("Things with id = " + 1L + " do not exist."));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(Item.builder().build()));

        NotFoundException userNotExistError;
        userNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> itemServiceImpl.addComment(1L, 1L, comment));
        assertThat(userNotExistError.getMessage(), is("The user with id = " + 1L + " does not exist."));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().build()));

        IncorrectDateError userNotBookerError;
        userNotBookerError = Assertions.assertThrows(IncorrectDateError.class,
                () -> itemServiceImpl.addComment(1L, 1L, comment));
        assertThat(userNotBookerError.getMessage(), is("You didn't book this item."));
    }

    @Test
    void addCommentTest() {
        CommentDto comment = CommentDto.builder()
                .created(LocalDateTime.now())
                .id(1L)
                .text("Good item")
                .build();

        Comment commentCreat = Comment.builder()
                .item(Item.builder().build())
                .created(LocalDateTime.now())
                .author(User.builder().build())
                .id(1L)
                .text("Good item")
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .item(Item.builder().build())
                .status(Status.APPROVED)
                .booker(User.builder().build())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(Item.builder().build()));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().build()));

        when(bookingRepository.findTop1BookingByItemIdAndBookerIdAndEndIsBeforeAndStatusOrderByStartDesc(
                any(), any(), any(), any()))
                .thenReturn(booking);

        when(commentRepository.save(commentCreat))
                .thenReturn(commentCreat);

        CommentDto commentDto = itemServiceImpl.addComment(1L, 1L, comment);
        assertThat(commentDto.getText(), is(comment.getText()));
    }
}