package com.github.pavlidise.acmebooking.integration.repository;

import com.github.pavlidise.acmebooking.model.entity.RoomEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Cacheable("rooms")
    @Query(value = "SELECT r FROM RoomEntity r")
    List<RoomEntity> getAllRooms();

    Optional<RoomEntity> findByRoomName(String roomName);
}
