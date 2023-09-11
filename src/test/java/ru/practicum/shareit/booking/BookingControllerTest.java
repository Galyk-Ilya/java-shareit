package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingEnterDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.service.MyConstants.USER_ID;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    private BookingEnterDto bookingEnterDto;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private ItemShortDto itemShortDto;

    private User booker;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1L);

        end = LocalDateTime.now().plusDays(2L);

        itemShortDto = ItemShortDto.builder()
                .name("item")
                .id(1L)
                .build();

        booker = User.builder()
                .id(1L)
                .name("user")
                .email("user@test.com")
                .build();

        bookingEnterDto = BookingEnterDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .build();

        bookingDto = BookingDto.builder()
                .booker(booker)
                .id(1L)
                .start(start)
                .end(end)
                .item(itemShortDto)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void tearDown() {
        start = null;
        end = null;
        bookingDto = null;
        item = null;
        booker = null;
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void changeApproved() throws Exception {
        bookingDto.setStatus(Status.APPROVED);

        when(bookingService.approvedBooking(any(), any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L)
                        .queryParam("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findAllBookingsByIdOwnerTest() throws Exception {
        when(bookingService.findAllBookingsByIdOwner(any(), any(), any()))
                .thenReturn(Arrays.asList(bookingDto));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .content(mapper.writeValueAsString(Arrays.asList(bookingDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findAllBookingsByIdUserTest() throws Exception {
        when(bookingService.findAllBookingsByIdUser(any(), any(), any()))
                .thenReturn(Arrays.asList(bookingDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(Arrays.asList(bookingDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void findBookingByIdTest() throws Exception {
        when(bookingService.findBookingById(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(Arrays.asList(bookingDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }
}
