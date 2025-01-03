package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;

import java.util.List;

public interface BookingService {

    List<ConfirmedBookingDTO> searchBookings(final BookingInquiryDTO bookingInquiryDTO);

    ConfirmedBookingDTO bookRoom(final BookingRequestDTO bookingRequestDTO);
}
