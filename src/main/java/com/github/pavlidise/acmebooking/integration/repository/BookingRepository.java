package com.github.pavlidise.acmebooking.integration.repository;

import com.github.pavlidise.acmebooking.model.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    /**
     * Booking Table
     * id | room_id | user_id | booking_start_time | booking_end_time
     */

    @Query(value =
            "SELECT * FROM booking b " +
            "WHERE b.room_id = :targetRoom AND (DATE(b.booking_start_time) <= :targetDate and DATE(b.booking_end_time) >= :targetDate) " +
            "ORDER BY b.booking_start_time ASC", nativeQuery = true)
    List<BookingEntity> searchBookingsByRoomAndDateOrderByBookingStartTimeAsc(@Param("targetRoom") Long targetRoom, @Param("targetDate") LocalDate targetDate);

    @Query(value =
            "SELECT COUNT(b) > 0 FROM booking b WHERE b.room_id = :roomId " +
            "AND b.booking_end_time >= :startDate AND b.booking_start_time <= :endDate", nativeQuery = true)
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    Optional<BookingEntity> findBookingEntityByUuid(UUID uuid);
}
