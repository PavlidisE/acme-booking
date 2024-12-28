package com.github.pavlidise.acmebooking.mapper;

import com.github.pavlidise.acmebooking.model.dto.ConfirmedBookingDTO;
import com.github.pavlidise.acmebooking.model.entity.BookingEntity;
import com.github.pavlidise.acmebooking.model.entity.RoomEntity;
import com.github.pavlidise.acmebooking.model.entity.AcmeUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper( BookingMapper.class );

    @Mapping(source = "room", target = "roomName", qualifiedByName = "getNameFromRoom")
    @Mapping(source = "acmeUser", target = "userEmail", qualifiedByName = "getEmailFromUser")
    @Mapping(source = "bookingStartTime", target = "bookingStartTime")
    @Mapping(source = "bookingEndTime", target = "bookingEndTime")
    ConfirmedBookingDTO mapConfirmedBookingFromBooking(BookingEntity bookingEntity);

    @Named("getNameFromRoom")
    static String getNameFromRoom(RoomEntity room){
        return room.getRoomName();
    }

    @Named("getEmailFromUser")
    static String getEmailFromUser(AcmeUserEntity user){
        return user.getUserEmail();
    }
}
