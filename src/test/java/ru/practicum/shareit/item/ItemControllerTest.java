package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final String USER_ID = "X-Sharer-User-Id";

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .description("description")
            .name("itemDto")
            .available(true)
            .build();

    ItemPatchDto itemPatchDto = ItemPatchDto.builder()
            .id(1L)
            .description("description")
            .name("itemDto")
            .available(true)
            .build();

    ItemOwnerDto itemOwnerDto = ItemOwnerDto.builder()
            .id(1L)
            .description("description")
            .name("itemOwnerDto")
            .available(true)
            .build();

    CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("comment")
            .authorName("author")
            .created(LocalDateTime.now()).build();

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemPatchDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemPatchDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemPatchDto.getName())))
                .andExpect(jsonPath("$.description", is(itemPatchDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemPatchDto.getAvailable())));
    }

    @Test
    void findItemByIdTest() throws Exception {
        when(itemService.findItemById(any(), any()))
                .thenReturn(itemOwnerDto);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOwnerDto.getAvailable())));
    }

    @Test
    void findItemsByIdOwnerTest() throws Exception {
        when(itemService.findItemsByIdOwner(any(), any()))
                .thenReturn(Arrays.asList(itemOwnerDto));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemOwnerDto.getAvailable())));
    }

    @Test
    void findItemsByTextTest() throws Exception {
        when(itemService.findItemsByText(any(), any()))
                .thenReturn(Arrays.asList(itemDto));

        mvc.perform(get("/items/search?text=description")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}