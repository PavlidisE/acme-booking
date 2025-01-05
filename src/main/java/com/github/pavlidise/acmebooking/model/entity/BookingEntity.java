package com.github.pavlidise.acmebooking.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", insertable = false, unique = true)
    @Generated
    private UUID uuid;

    /**
     * Each Booking has one room associated with it, but each room can be associated with multiple bookings.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    /**
     * Each Booking has one user associated with it, but each user can be associated with multiple bookings.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AcmeUserEntity acmeUser;

    @Future
    @Column(name = "booking_start_time", nullable = false)
    private LocalDateTime bookingStartTime;

    @Column(name = "booking_end_time", nullable = false)
    private LocalDateTime bookingEndTime;
}
