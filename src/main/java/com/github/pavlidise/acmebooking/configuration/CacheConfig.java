package com.github.pavlidise.acmebooking.configuration;

import com.github.pavlidise.acmebooking.integration.repository.RoomRepository;
import com.github.pavlidise.acmebooking.model.entity.RoomEntity;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ApplicationRunner;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Configuration
public class CacheConfig {

    private final RoomRepository roomRepository;
    private final CacheManager cacheManager;

    public CacheConfig(RoomRepository roomRepository, CacheManager cacheManager) {
        this.roomRepository = roomRepository;
        this.cacheManager = cacheManager;
    }

    @Bean
    public ApplicationRunner preloadRooms() {
        return args -> {
            List<RoomEntity> rooms = roomRepository.getAllRooms();
            Cache cache = cacheManager.getCache("rooms");
            rooms.forEach(room -> cache.put(room.getRoomName(), room));  // Cache each room by its name
            log.info("Preloaded {} rooms into cache.", rooms.size());
        };
    }
}
