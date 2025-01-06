package com.github.pavlidise.acmebooking.integration;

import com.github.pavlidise.acmebooking.exception.BookingNotFoundException;
import com.github.pavlidise.acmebooking.exception.OverlappingBookingException;
import com.github.pavlidise.acmebooking.exception.PastBookingDeletionException;
import com.github.pavlidise.acmebooking.exception.RoomNotFoundException;
import com.github.pavlidise.acmebooking.exception.UserNotFoundException;
import com.github.pavlidise.acmebooking.integration.rest.BookingController;
import com.github.pavlidise.acmebooking.service.BookingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private AutoCloseable openMocks;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void testHandleBookingNotFoundException() throws Exception {
        UUID uuid = UUID.randomUUID();
        Mockito.doThrow(new BookingNotFoundException("Booking not found")).when(bookingService).deleteBooking(uuid);

        mockMvc.perform(delete("/api/v1/bookings")
                        .param("uuid", uuid.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking not found"));
    }

    @Test
    void testHandleOverlappingBookingException() throws Exception {
        Mockito.doThrow(new OverlappingBookingException("Overlapping booking")).when(bookingService).createBooking(Mockito.any());

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"user@example.com\",\"roomName\":\"Conference Room\",\"bookingStartDateTime\":\"" + LocalDateTime.now().plusDays(1) + "\",\"numberOfHours\":2}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Overlapping booking"));
    }

    @Test
    void testHandleRoomNotFoundException() throws Exception {
        Mockito.doThrow(new RoomNotFoundException("Room not found")).when(bookingService).createBooking(Mockito.any());

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"user@example.com\",\"roomName\":\"NonExistentRoom\",\"bookingStartDateTime\":\"" + LocalDateTime.now().plusDays(1) + "\",\"numberOfHours\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Room not found"));
    }

    @Test
    void testHandleUserNotFoundException() throws Exception {
        Mockito.doThrow(new UserNotFoundException("User not found")).when(bookingService).createBooking(Mockito.any());

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"nonexistentuser@example.com\",\"roomName\":\"Conference Room\",\"bookingStartDateTime\":\"" + LocalDateTime.now().plusDays(1) + "\",\"numberOfHours\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testHandlePastBookingDeletionException() throws Exception {
        UUID uuid = UUID.randomUUID();
        Mockito.doThrow(new PastBookingDeletionException("Cannot delete past booking")).when(bookingService).deleteBooking(uuid);

        mockMvc.perform(delete("/api/v1/bookings")
                        .param("uuid", uuid.toString()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Cannot delete past booking"));
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings")
                        .param("uuid", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Method parameter 'uuid': Failed to convert value of type 'java.lang.String' to required type 'java.util.UUID'; Invalid UUID string: invalid-uuid"));
    }

    @Test
    void testHandleHttpMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"invalid-json\""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("JSON parse error: Cannot construct instance of `com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('invalid-json')"));
    }

    @Test
    void testHandleConstraintViolationException() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("deleteBooking.uuid: must not be null"));
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"email@acme.com\",\"roomName\":\"Conference Room\",\"bookingStartDateTime\":\"" + LocalDateTime.now().plusDays(1) + "\",\"numberOfHours\": -1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"numberOfHours\":\"must be greater than or equal to 1\"}"));
    }

    @Test
    void testCatchAllException() throws Exception {
        UUID uuid = UUID.randomUUID();
        doThrow(new RuntimeException("Unexpected error")).when(bookingService).deleteBooking(uuid);

        mockMvc.perform(delete("/api/v1/bookings")
                        .param("uuid", uuid.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred"));
    }
}
