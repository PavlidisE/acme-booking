package com.github.pavlidise.acmebooking.integration.rest;

import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import com.github.pavlidise.acmebooking.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping(consumes = "application/json", produces = "application/json")
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
    @PostMapping(consumes = "application/json", produces = "application/json")
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
    @DeleteMapping
    public ResponseEntity<String> deleteBooking(@PathParam(value = "uuid") @Valid @NotNull UUID uuid) {
        log.info("Deleting booking with UUID: {}", uuid);
        bookingService.deleteBooking(uuid);
        log.info("Booking with UUID: {} deleted successfully", uuid);
        return ResponseEntity.ok("Booking deleted successfully!");
    }
}
