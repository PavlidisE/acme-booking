package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BookingService {
    Page<ConfirmedBookingDTO> searchBookings(final String roomName, final LocalDate date, Pageable pageable);

    ConfirmedBookingDTO bookRoom(final BookingRequestDTO bookingRequestDTO);
}
