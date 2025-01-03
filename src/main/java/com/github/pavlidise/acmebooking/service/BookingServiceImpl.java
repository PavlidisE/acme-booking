package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.exception.OverlappingBookingException;
import com.github.pavlidise.acmebooking.exception.RoomNotFoundException;
import com.github.pavlidise.acmebooking.exception.UserNotFoundException;
import com.github.pavlidise.acmebooking.integration.repository.AcmeUserRepository;
import com.github.pavlidise.acmebooking.integration.repository.BookingRepository;
import com.github.pavlidise.acmebooking.mapper.BookingMapper;
import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import com.github.pavlidise.acmebooking.model.entity.AcmeUserEntity;
import com.github.pavlidise.acmebooking.model.entity.BookingEntity;
import com.github.pavlidise.acmebooking.model.entity.RoomEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@EnableCaching
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final RoomCacheService roomCacheService;

    private final AcmeUserRepository acmeUserRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomCacheService roomCacheService, AcmeUserRepository acmeUserRepository) {
        this.bookingRepository = bookingRepository;
        this.roomCacheService = roomCacheService;
        this.acmeUserRepository = acmeUserRepository;
    }

    @Override
    public List<ConfirmedBookingDTO> searchBookings(final BookingInquiryDTO bookingInquiryDTO) {
        RoomEntity room = findRoomByName(bookingInquiryDTO.roomName());
        List<BookingEntity> bookingEntities = performBookingSearch(room.getId(), bookingInquiryDTO.date());
        return bookingEntities.stream().map(BookingMapper.INSTANCE::mapConfirmedBookingFromBooking).toList();
    }

    private RoomEntity findRoomByName(final String roomName){
        Optional<RoomEntity> roomByName = roomCacheService.getRoomByName(roomName);
        if(roomByName.isEmpty()){
            final String errorMsg = String.format("Room with name: %s not found", roomByName);
            log.warn(errorMsg);
            throw new RoomNotFoundException(errorMsg);
        }
        return roomByName.get();
    }

    private List<BookingEntity> performBookingSearch(final Long roomId, final LocalDate date) {
        return bookingRepository.searchBookingsByRoomAndDateOrderByBookingStartTimeAsc(roomId, date);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public ConfirmedBookingDTO bookRoom(final BookingRequestDTO bookingRequestDTO) {
        RoomEntity room = findRoomByName(bookingRequestDTO.roomName());

        LocalDateTime bookingStartDateTime = bookingRequestDTO.bookingStartDateTime();
        LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(bookingRequestDTO.numberOfHours());

//        validate room availability
        validateRoomAvailability(room, bookingStartDateTime, bookingEndDateTime);

//        getUser
        // in a real scenario we would get this information from JWT or similar auth information
        AcmeUserEntity user = findUserByEmail(bookingRequestDTO.userEmail());

//        make booking
        return createBooking(room, user, bookingStartDateTime, bookingEndDateTime);
    }

    private void validateRoomAvailability(final RoomEntity room, final LocalDateTime bookingStartDateTime, final LocalDateTime bookingEndDateTime) {
        boolean existsOverlappingBooking = bookingRepository.existsOverlappingBooking(room.getId(), bookingStartDateTime, bookingEndDateTime);

        if (existsOverlappingBooking) {
            final String errorMsg = String.format("Room: '%s' is already booked during the requested period", room.getRoomName());
            log.warn(errorMsg);
            throw new OverlappingBookingException(errorMsg);
        }
    }

    private AcmeUserEntity findUserByEmail(final String userEmail) {
        Optional<AcmeUserEntity> optionalAcmeUser = acmeUserRepository.findByUserEmail(userEmail);
        if (optionalAcmeUser.isEmpty()) {
            final String errorMsg = String.format("User with email: %s does not exist", userEmail);
            log.warn(errorMsg);
            throw new UserNotFoundException(errorMsg);
        }

        return optionalAcmeUser.get();
    }

    public ConfirmedBookingDTO createBooking(final RoomEntity room, final AcmeUserEntity user,
                                             final LocalDateTime bookingStartDateTime, final LocalDateTime bookingEndDateTime) {
        BookingEntity booking = BookingEntity.builder()
                .room(room)
                .acmeUser(user)
                .bookingStartTime(bookingStartDateTime)
                .bookingEndTime(bookingEndDateTime)
                .build();
        return BookingMapper.INSTANCE.mapConfirmedBookingFromBooking(bookingRepository.saveAndFlush(booking));
    }
}
