package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    Page<ConfirmedBookingDTO> searchBookings(final BookingInquiryDTO bookingInquiryDTO, Pageable pageable);

    ConfirmedBookingDTO bookRoom(final BookingRequestDTO bookingRequestDTO);
}
