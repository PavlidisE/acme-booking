package com.github.pavlidise.acmebooking.service

import com.github.pavlidise.acmebooking.exception.BookingNotFoundException
import com.github.pavlidise.acmebooking.exception.OverlappingBookingException
import com.github.pavlidise.acmebooking.exception.PastBookingDeletionException
import com.github.pavlidise.acmebooking.exception.RoomNotFoundException
import com.github.pavlidise.acmebooking.exception.UserNotFoundException
import com.github.pavlidise.acmebooking.integration.repository.AcmeUserRepository
import com.github.pavlidise.acmebooking.integration.repository.BookingRepository
import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO
import com.github.pavlidise.acmebooking.model.entity.AcmeUserEntity
import com.github.pavlidise.acmebooking.model.entity.BookingEntity
import com.github.pavlidise.acmebooking.model.entity.RoomEntity
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime

class BookingServiceSpec extends Specification {

    private BookingRepository bookingRepository = Mock()
    private RoomCacheService roomCacheService = Mock()
    private AcmeUserRepository acmeUserRepository = Mock()
    @Subject
    private BookingService bookingService = new BookingServiceImpl(bookingRepository, roomCacheService, acmeUserRepository)

    private static RoomEntity roomEntity
    private static AcmeUserEntity acmeUserEntity

    def "setupSpec"() {
        roomEntity = new RoomEntity(id: 1L, roomName: "Conference Room")
        acmeUserEntity = new AcmeUserEntity(userEmail: "user@example.com")
    }

    def "searchBookings completes successfully"() {
        given:
        BookingInquiryDTO inquiryDTO = new BookingInquiryDTO("Conference Room", LocalDate.now())
        List<BookingEntity> bookingEntities = [
                new BookingEntity(uuid: UUID.randomUUID(), room: roomEntity, acmeUser: acmeUserEntity, bookingStartTime: LocalDateTime.now(), bookingEndTime: LocalDateTime.now().plusHours(2))
        ]

        when:
        List<ConfirmedBookingDTO> result = bookingService.searchBookings(inquiryDTO)

        then:
        1 * roomCacheService.getRoomByName("Conference Room") >> Optional.of(roomEntity)
        1 * bookingRepository.searchBookingsByRoomAndDateOrderByBookingStartTimeAsc(1L, inquiryDTO.date()) >> bookingEntities
        result.size() == 1
        result[0].roomName() == "Conference Room"
    }

    def "searchBookings throws RoomNotFoundException when room is not found"() {
        given:
        BookingInquiryDTO inquiryDTO = new BookingInquiryDTO("Conference Room", LocalDate.now())

        when:
        bookingService.searchBookings(inquiryDTO)

        then:
        1 * roomCacheService.getRoomByName("Conference Room") >> Optional.empty()
        thrown(RoomNotFoundException)
    }

    def "createBooking completes successfully"() {
        given:
        BookingRequestDTO requestDTO = new BookingRequestDTO("user@example.com", "Conference Room", LocalDateTime.now().plusDays(1), 2)
        BookingEntity bookingEntity = new BookingEntity(uuid: UUID.randomUUID(), room: roomEntity, acmeUser: acmeUserEntity, bookingStartTime: requestDTO.bookingStartDateTime(), bookingEndTime: requestDTO.bookingStartDateTime().plusHours(2))

        when:
        ConfirmedBookingDTO result = bookingService.createBooking(requestDTO)

        then:
        1 * roomCacheService.getRoomByName("Conference Room") >> Optional.of(roomEntity)
        1 * bookingRepository.existsOverlappingBooking(1L, requestDTO.bookingStartDateTime(), requestDTO.bookingStartDateTime().plusHours(2)) >> false
        1 * acmeUserRepository.findByUserEmail("user@example.com") >> Optional.of(acmeUserEntity)
        1 * bookingRepository.saveAndFlush(_) >> bookingEntity
        result.roomName() == "Conference Room"
    }

    def "createBooking throws RoomNotFoundException when room is not found"() {
        given:
        BookingRequestDTO requestDTO = new BookingRequestDTO("user@example.com", "Conference Room", LocalDateTime.now().plusDays(1), 2)

        when:
        bookingService.createBooking(requestDTO)

        then:
        1 * roomCacheService.getRoomByName("Conference Room") >> Optional.empty()
        thrown(RoomNotFoundException)
    }

    def "createBooking throws OverlappingBookingException when there is an overlapping booking"() {
        given:
        BookingRequestDTO requestDTO = new BookingRequestDTO("user@example.com", "Conference Room", LocalDateTime.now().plusDays(1), 2)

        when:
        bookingService.createBooking(requestDTO)

        then:
        1 * roomCacheService.getRoomByName("Conference Room") >> Optional.of(roomEntity)
        1 * bookingRepository.existsOverlappingBooking(1L, requestDTO.bookingStartDateTime(), requestDTO.bookingStartDateTime().plusHours(2)) >> true
        thrown(OverlappingBookingException)
    }

    def "createBooking should throw UserNotFoundException if user is not found"() {
        given:
        BookingRequestDTO requestDTO = new BookingRequestDTO("user@example.com", "Conference Room", LocalDateTime.now().plusDays(1), 2)

        when:
        bookingService.createBooking(requestDTO)

        then:
        1 * roomCacheService.getRoomByName("Conference Room") >> Optional.of(roomEntity)
        1 * bookingRepository.existsOverlappingBooking(1L, requestDTO.bookingStartDateTime(), requestDTO.bookingStartDateTime().plusHours(2)) >> false
        1 * acmeUserRepository.findByUserEmail("user@example.com") >> Optional.empty()
        thrown(UserNotFoundException)
    }

    def "deleteBooking completes successfully"() {
        given:
        UUID uuid = UUID.randomUUID()
        BookingEntity bookingEntity = new BookingEntity(uuid: uuid, bookingStartTime: LocalDateTime.now().plusDays(1), bookingEndTime: LocalDateTime.now().plusDays(1).plusHours(2))

        when:
        bookingService.deleteBooking(uuid)

        then:
        1 * bookingRepository.findBookingEntityByUuid(uuid) >> Optional.of(bookingEntity)
        1 * bookingRepository.delete(bookingEntity)
    }

    def "deleteBooking throws BookingNotFoundException when booking is not found"() {
        given:
        UUID uuid = UUID.randomUUID()

        when:
        bookingService.deleteBooking(uuid)

        then:
        1 * bookingRepository.findBookingEntityByUuid(uuid) >> Optional.empty()
        thrown(BookingNotFoundException)
    }

    def "deleteBooking should throw PastBookingDeletionException if booking is in the past"() {
        given:
        UUID uuid = UUID.randomUUID()
        BookingEntity pastBookingEntity = new BookingEntity(uuid: uuid, bookingStartTime: LocalDateTime.now().minusDays(1), bookingEndTime: LocalDateTime.now().minusDays(1).plusHours(2))

        when:
        bookingService.deleteBooking(uuid)

        then:
        1 * bookingRepository.findBookingEntityByUuid(uuid) >> Optional.of(pastBookingEntity)
        thrown(PastBookingDeletionException)
    }
}
