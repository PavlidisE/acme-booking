package com.github.pavlidise.acmebooking.integration.rest;

import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import com.github.pavlidise.acmebooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Search for bookings by room name and/or booking date.
     *
     * @param bookingInquiryDTO DTO consisting of room name and booking date to filter bookings
     * @return a list of ConfirmedBookingDTO matching the criteria
     */
    @GetMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<ConfirmedBookingDTO>> searchBookings(@Valid @RequestBody BookingInquiryDTO bookingInquiryDTO) {
        List<ConfirmedBookingDTO> confirmedBookingDTOPage = bookingService.searchBookings(bookingInquiryDTO);
        return ResponseEntity.ok(confirmedBookingDTOPage);
    }

    /**
     * Book a room based on the BookingReservationDTO.
     *
     * @param bookingRequestDTO the booking reservation details
     * @return the confirmed booking details
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ConfirmedBookingDTO> bookRoom(@Valid @RequestBody BookingRequestDTO bookingRequestDTO) {
        ConfirmedBookingDTO confirmedBookingDTO = bookingService.bookRoom(bookingRequestDTO);
        return ResponseEntity.ok(confirmedBookingDTO);
    }
}
