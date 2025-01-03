package com.github.pavlidise.acmebooking.integration.repository;

import com.github.pavlidise.acmebooking.model.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    /**
     * Booking Table
     * id | room_id | user_id | booking_start_time | booking_end_time
     *
         SELECT r.room_name,
         u.user_email,
         b.booking_start_time AS start_date_time,
         b.booking_end_time   AS end_date_time
         FROM booking b
         JOIN
         room r ON b.room_id = r.id
         JOIN
         "user" u ON b.user_id = u.id
         WHERE b.room_id = :target_room AND (DATE(b.booking_start_time) <= :target_date AND DATE(b.booking_end_time) >= :target_date);
     *
     */

    @Query(value =
            "SELECT * FROM booking b " +
            "WHERE b.room_id = :targetRoom AND (DATE(b.booking_start_time) <= :targetDate and DATE(b.booking_end_time) >= :targetDate) " +
            "ORDER BY b.booking_start_time ASC", nativeQuery = true)
    List<BookingEntity> searchBookingsByRoomAndDateOrderByBookingStartTimeAsc(@Param("targetRoom") Long targetRoom, @Param("targetDate") LocalDate targetDate);


}
