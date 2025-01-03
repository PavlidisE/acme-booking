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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
    public List<ConfirmedBookingDTO> searchBookings(final BookingInquiryDTO bookingInquiryDTO) {
        RoomEntity room = findRoomByName(bookingInquiryDTO.roomName());
        List<BookingEntity> bookingEntities = performBookingSearch(room.getId(), bookingInquiryDTO.date());
        return bookingEntities.stream().map(BookingMapper.INSTANCE::mapConfirmedBookingFromBooking).toList();
    }

    private RoomEntity findRoomByName(final String roomName){
        Optional<RoomEntity> roomByName = roomCacheService.getRoomByName(roomName);
        if(roomByName.isEmpty()){
            throw new RoomNotFoundException(roomName);
        }
        return roomByName.get();
    }

    private List<BookingEntity> performBookingSearch(final Long roomId, final LocalDate date) {
        return bookingRepository.searchBookingsByRoomAndDateOrderByBookingStartTimeAsc(roomId, date);
    }

    @Override
    public ConfirmedBookingDTO bookRoom(final BookingRequestDTO bookingRequestDTO) {
        return null;
    }

}
