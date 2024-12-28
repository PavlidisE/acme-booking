package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.exception.RoomNotFoundException;
import com.github.pavlidise.acmebooking.integration.repository.BookingRepository;
import com.github.pavlidise.acmebooking.mapper.BookingMapper;
import com.github.pavlidise.acmebooking.model.dto.BookingInquiryDTO;
import com.github.pavlidise.acmebooking.model.dto.BookingRequestDTO;
import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import com.github.pavlidise.acmebooking.model.entity.BookingEntity;
import com.github.pavlidise.acmebooking.model.entity.RoomEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@EnableCaching
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final RoomCacheService roomCacheService;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomCacheService roomCacheService) {
        this.bookingRepository = bookingRepository;
        this.roomCacheService = roomCacheService;
    }

    @Override
    public Page<ConfirmedBookingDTO> searchBookings(final BookingInquiryDTO bookingInquiryDTO, final Pageable pageable) {
        RoomEntity room = findRoomByName(bookingInquiryDTO.roomName());
        Page<BookingEntity> bookingEntities = performBookingSearch(room.getId(), bookingInquiryDTO.date(), pageable);
        return bookingEntities.map(BookingMapper.INSTANCE::mapConfirmedBookingFromBooking);
    }

    private RoomEntity findRoomByName(final String roomName){
        Optional<RoomEntity> roomByName = roomCacheService.getRoomByName(roomName);
        if(roomByName.isEmpty()){
            throw new RoomNotFoundException(roomName);
        }

        return roomByName.get();
    }

    private Page<BookingEntity> performBookingSearch(final Long roomId, final LocalDate date, final Pageable pageable) {
        // TODO investigate Pageable settings
        return bookingRepository.searchBookingsByRoomAndDate(roomId, date, pageable);
    }

    @Override
    public ConfirmedBookingDTO bookRoom(final BookingRequestDTO bookingRequestDTO) {
        return null;
    }
}
