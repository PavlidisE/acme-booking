package com.github.pavlidise.acmebooking.integration.rest;

import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import com.github.pavlidise.acmebooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/bookings")
@Tag(name = "ACME Booking REST API")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Search for bookings by room name and booking date.
     *
     * @param bookingInquiryDTO DTO consisting of room name and date to filter bookings
     * @return a list of ConfirmedBookingDTO(Bookings) matching the criteria
     */
    @Operation(summary = "Receives a booking inquiry request, containing room and date filters, in JSON format.",
            description = """
                    Validates incoming request and proceeds with the search of bookings based on the provided filters.
                    If at any point during that process an error occurs, an appropriate message is returned.
                    Else it returns any found bookings.
                    """)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ConfirmedBookingDTO>> searchBookings(@Valid @RequestBody BookingInquiryDTO bookingInquiryDTO) {
        log.info("Searching for bookings with criteria: {}", bookingInquiryDTO);
        List<ConfirmedBookingDTO> confirmedBookingDTOList = bookingService.searchBookings(bookingInquiryDTO);
        log.info("Found {} bookings matching the criteria", confirmedBookingDTOList.size());
        return ResponseEntity.ok(confirmedBookingDTOList);
    }

    /**
     * Book a room based on the BookingRequestDTO.
     *
     * @param bookingRequestDTO the booking reservation details
     * @return the confirmed booking details
     */
    @Operation(summary = "Receives a booking creation request, containing room, date time and user info, in JSON format.",
            description = """
                    Validates incoming request and proceeds with the creation of the booking based on provided info.
                    If at any point during that process an error occurs, an appropriate message is returned.
                    Else it returns the newly created booking.
                    """)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConfirmedBookingDTO> createBooking(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        log.info("Creating a booking with details: {}", bookingRequestDTO);
        ConfirmedBookingDTO confirmedBookingDTO = bookingService.createBooking(bookingRequestDTO);
        log.info("Booking created successfully for room: {}, from: {}, to: {}, by: {}", confirmedBookingDTO.roomName(),
                confirmedBookingDTO.bookingStartTime(), confirmedBookingDTO.bookingEndTime(), confirmedBookingDTO.userEmail());
        return ResponseEntity.ok(confirmedBookingDTO);
    }

    /**
     * Delete a booking by its UUID.
     * This operation should be performed only by authorized personnel. (future endeavors)
     *
     * @param uuid the UUID of the booking to be deleted
     * @return a response message indicating the result of the operation
     */
    @Operation(summary = "Receives a request for booking deletion, based on booking UUID.",
            description = """
                    Validates the path parameter input as a UUID and proceeds with the deletion of the matching booking, if exists.
                    If at any point during that process an error occurs, an appropriate message is returned.
                    Else it returns a positive message.
                    """)
    @DeleteMapping
    public ResponseEntity<String> deleteBooking(@PathParam(value = "uuid") @Valid @NotNull UUID uuid) {
        log.info("Deleting booking with UUID: {}", uuid);
        bookingService.deleteBooking(uuid);
        log.info("Booking with UUID: {} deleted successfully", uuid);
        return ResponseEntity.ok("Booking deleted successfully!");
    }
}
