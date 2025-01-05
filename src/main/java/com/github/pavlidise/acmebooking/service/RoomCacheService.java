package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.model.entity.RoomEntity;

import java.util.Optional;

public interface RoomCacheService {

    Optional<RoomEntity> getRoomByName(final String roomName);
}
