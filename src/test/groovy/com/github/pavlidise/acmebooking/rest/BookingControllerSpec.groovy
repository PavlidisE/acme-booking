package com.github.pavlidise.acmebooking.rest

import com.github.pavlidise.acmebooking.integration.rest.BookingController
import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO
import com.github.pavlidise.acmebooking.service.BookingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime

class BookingControllerSpec extends Specification {

    private BookingService bookingService = Mock()
    @Subject
    private BookingController bookingController = new BookingController(bookingService)

    def "searchBookings completes successfully"() {
        given:
        BookingInquiryDTO inquiryDTO = new BookingInquiryDTO("Conference Room", LocalDate.now())
        List<ConfirmedBookingDTO> confirmedBookings = [
                new ConfirmedBookingDTO(UUID.randomUUID(),
                        "Conference Room",
                        "user@example.com",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(2))]

        when:
        ResponseEntity<List<ConfirmedBookingDTO>> response = bookingController.searchBookings(inquiryDTO)

        then:
        1 * bookingService.searchBookings(inquiryDTO) >> confirmedBookings
        response.statusCode == HttpStatus.OK
        response.body == confirmedBookings
    }

    def "createBooking completes successfully"() {
        given:
        BookingRequestDTO requestDTO = new BookingRequestDTO("user@example.com", "Conference Room", LocalDateTime.now().plusDays(1), 2)
        ConfirmedBookingDTO confirmedBooking = new ConfirmedBookingDTO(UUID.randomUUID(), "Conference Room", "user@example.com", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2))

        when:
        ResponseEntity<ConfirmedBookingDTO> response = bookingController.createBooking(requestDTO)

        then:
        1 * bookingService.createBooking(requestDTO) >> confirmedBooking
        response.statusCode == HttpStatus.OK
        response.body == confirmedBooking
    }

    def "deleteBooking completes successfully"() {
        given:
        UUID uuid = UUID.randomUUID()

        when:
        ResponseEntity<String> response = bookingController.deleteBooking(uuid)

        then:
        1 * bookingService.deleteBooking(uuid)
        response.statusCode == HttpStatus.OK
        response.body == "Booking deleted successfully!"
    }
}
