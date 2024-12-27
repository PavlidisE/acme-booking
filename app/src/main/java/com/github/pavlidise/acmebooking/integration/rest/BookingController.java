package com.github.pavlidise.acmebooking.integration.rest;

import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {

    /**
     * Search for bookings by room name and/or booking date.
     * <p>
     * Using RequestParams instead of PathParam since,
     * path parameters are typically used to identify or retrieve a specific resource;
     * while Query(Request) parameters are more suitable for sorting/filtering/paginating the request data.
     * </p>
     * @param roomName optional room name to filter bookings
     * @param date     optional booking date to filter bookings
     * @param pageable pageable info, enabling pagination of response
     * @return a list of ConfirmedBookingDTO matching the criteria
     */
    @GetMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Page<ConfirmedBookingDTO>> searchBookings(
            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(null);
    }

    /**
     * Book a room based on the BookingReservationDTO.
     *
     * @param bookingRequestDTO the booking reservation details
     * @return the confirmed booking details
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ConfirmedBookingDTO> bookRoom(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(null);
    }
}
