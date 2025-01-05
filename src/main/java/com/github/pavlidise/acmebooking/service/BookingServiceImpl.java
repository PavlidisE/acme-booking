package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.exception.BookingNotFoundException;
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
import java.util.UUID;

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

    /**
     * Search for bookings by room name and booking date.
     *
     * @param bookingInquiryDTO DTO consisting of room name and booking date to filter bookings
     * @return a list of ConfirmedBookingDTO matching the criteria
     */
    @Override
    public List<ConfirmedBookingDTO> searchBookings(final BookingInquiryDTO bookingInquiryDTO) {
        RoomEntity room = findRoomByName(bookingInquiryDTO.roomName());
        List<BookingEntity> bookingEntities = performBookingSearch(room.getId(), bookingInquiryDTO.date());
        return bookingEntities.stream().map(BookingMapper.INSTANCE::mapConfirmedBookingFromBooking).toList();
    }

    /**
     * Find a room by its name.
     *
     * @param roomName the name of the room
     * @return the RoomEntity found
     * @throws RoomNotFoundException if the room is not found
     */
    private RoomEntity findRoomByName(final String roomName){
        log.info("Searching room with name: {}", roomName);
        Optional<RoomEntity> roomByName = roomCacheService.getRoomByName(roomName);
        if(roomByName.isEmpty()){
            final String errorMsg = String.format("Room with name: %s not found", roomName);
            log.warn(errorMsg);
            throw new RoomNotFoundException(errorMsg);
        }
        return roomByName.get();
    }

    /**
     * Perform the search for bookings by room ID and date,
     * by invoking the relevant bookingRepository method
     *
     * @param roomId the ID of the room
     * @param date   the date of the booking
     * @return a list of BookingEntity matching the criteria
     */
    private List<BookingEntity> performBookingSearch(final Long roomId, final LocalDate date) {
        return bookingRepository.searchBookingsByRoomAndDateOrderByBookingStartTimeAsc(roomId, date);
    }

    /**
     * Create a new booking based on the BookingRequestDTO.
     *
     * @param bookingRequestDTO the booking request details
     * @return the confirmed booking details
     * @throws OverlappingBookingException if there is an overlapping booking
     * @throws RoomNotFoundException if the room is not found
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public ConfirmedBookingDTO createBooking(final BookingRequestDTO bookingRequestDTO) {
        RoomEntity room = findRoomByName(bookingRequestDTO.roomName());

        LocalDateTime bookingStartDateTime = bookingRequestDTO.bookingStartDateTime();
        LocalDateTime bookingEndDateTime = bookingStartDateTime.plusHours(bookingRequestDTO.numberOfHours());
        validateRoomAvailability(room, bookingStartDateTime, bookingEndDateTime);

        // in a real scenario we would get this information from JWT or similar auth information
        AcmeUserEntity user = findUserByEmail(bookingRequestDTO.userEmail());
        return createBooking(room, user, bookingStartDateTime, bookingEndDateTime);
    }

    /**
     * Validate the availability of the room for the given time period.
     *
     * @param room the room entity
     * @param bookingStartDateTime the start time of the booking
     * @param bookingEndDateTime the end time of the booking
     * @throws OverlappingBookingException if there is an overlapping booking
     */
    private void validateRoomAvailability(final RoomEntity room, final LocalDateTime bookingStartDateTime, final LocalDateTime bookingEndDateTime) {
        log.info("Validating Room availability");
        boolean existsOverlappingBooking = bookingRepository.existsOverlappingBooking(room.getId(), bookingStartDateTime, bookingEndDateTime);

        if (existsOverlappingBooking) {
            final String errorMsg = String.format("Room: '%s' is already booked during the requested period", room.getRoomName());
            log.warn(errorMsg);
            throw new OverlappingBookingException(errorMsg);
        }
    }

    /**
     * Find a user by their email address.
     *
     * @param userEmail the email address of the user
     * @return the AcmeUserEntity found
     * @throws UserNotFoundException if the user is not found
     */
    private AcmeUserEntity findUserByEmail(final String userEmail) {
        log.info("Searching for user with email: {}", userEmail);
        Optional<AcmeUserEntity> optionalAcmeUser = acmeUserRepository.findByUserEmail(userEmail);
        if (optionalAcmeUser.isEmpty()) {
            final String errorMsg = String.format("User with email: %s not found", userEmail);
            log.warn(errorMsg);
            throw new UserNotFoundException(errorMsg);
        }

        return optionalAcmeUser.get();
    }

    /**
     * Create a new booking entity and save it to the repository.
     *
     * @param room the room entity
     * @param user the user entity
     * @param bookingStartDateTime the start time of the booking
     * @param bookingEndDateTime the end time of the booking
     * @return the confirmed booking details
     */
    public ConfirmedBookingDTO createBooking(final RoomEntity room,
                                             final AcmeUserEntity user,
                                             final LocalDateTime bookingStartDateTime,
                                             final LocalDateTime bookingEndDateTime) {
        BookingEntity newBooking = BookingEntity.builder()
                .room(room)
                .acmeUser(user)
                .bookingStartTime(bookingStartDateTime)
                .bookingEndTime(bookingEndDateTime)
                .build();

        return BookingMapper.INSTANCE.mapConfirmedBookingFromBooking(bookingRepository.saveAndFlush(newBooking));
    }

    /**
     * Delete a booking by its UUID.
     *
     * @param uuid the UUID of the booking to be deleted
     * @throws BookingNotFoundException if the booking is not found
     */
    @Override
    public void deleteBooking(final UUID uuid) {
        Optional<BookingEntity> optionalBookingEntity = bookingRepository.findBookingEntityByUuid(uuid);
        if(optionalBookingEntity.isEmpty()){
            final String errorMsg = String.format("No Booking found with UUID: %s", uuid);
            log.warn(errorMsg);
            throw new BookingNotFoundException(errorMsg);
        }

        bookingRepository.delete(optionalBookingEntity.get());
    }
}
