package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createBooking(long idBooker, BookItemRequestDto bookItemRequestDto) {
        return post("", idBooker, bookItemRequestDto);
    }

    public ResponseEntity<Object> approvedBooking(Long bookingId, Long idOwner, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        String path = "/" + bookingId + "?approved={approved}";
        return patch(path, idOwner, parameters, null);
    }

    public ResponseEntity<Object> findAllBookingsByIdOwner(Long idOwner, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", state.name(),
                "from", from,
                "size", size);
        return get("/owner?state={state}&from={from}&size={size}", idOwner, parameters);
    }

    public ResponseEntity<Object> findAllBookingsByIdUser(Long idUser, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("state", state.name(),
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", idUser, parameters);
    }

    public ResponseEntity<Object> findBookingById(Long idUser, Long bookingId) {
        return get("/" + bookingId, idUser);
    }
}