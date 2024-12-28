package com.github.pavlidise.acmebooking.service;

import com.github.pavlidise.acmebooking.integration.repository.RoomRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import com.github.pavlidise.acmebooking.model.entity.RoomEntity;

import java.util.Optional;

@Service
public class RoomCacheServiceImpl implements RoomCacheService {

    private final CacheManager cacheManager;

    private final RoomRepository roomRepository;

    public RoomCacheServiceImpl(CacheManager cacheManager, RoomRepository roomRepository) {
        this.cacheManager = cacheManager;
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<RoomEntity> getRoomByName(String roomName) {
        Cache cache = cacheManager.getCache("rooms");
        RoomEntity roomEntity = cache.get(roomName, RoomEntity.class);

        // If not found in the cache, fetch from the database and cache the result
        if (roomEntity == null) {
            Optional<RoomEntity> optionalRoomFromDB = roomRepository.findByRoomName(roomName);
            if (optionalRoomFromDB.isPresent()) {
                cache.put(roomName, optionalRoomFromDB.get());  // Cache the room if found in DB
                return optionalRoomFromDB;
            }
        }
        return Optional.of(roomEntity);
    }
}
