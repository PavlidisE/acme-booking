package com.github.pavlidise.acmebooking.service

import com.github.pavlidise.acmebooking.integration.repository.RoomRepository
import com.github.pavlidise.acmebooking.model.entity.RoomEntity
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import spock.lang.Specification
import spock.lang.Subject

class RoomCacheServiceSpec extends Specification {

    private final Cache cache = Mock()
    private final CacheManager cacheManager = Mock()
    private final RoomRepository roomRepository = Mock()
    @Subject
    private final RoomCacheService roomCacheService = new RoomCacheServiceImpl(cacheManager, roomRepository)

    private static String targetRoom
    private static RoomEntity roomEntity

    def "setupSpec"(){
        targetRoom = "ConferenceRoom"
        roomEntity = new RoomEntity(roomName: targetRoom)
    }

    def "setup"(){
        cacheManager.getCache("rooms") >> cache
    }

    def "getRoomByName returns room from cache"() {
        when:
        Optional<RoomEntity> result = roomCacheService.getRoomByName(targetRoom)

        then:
        1 * cache.get(targetRoom, RoomEntity.class) >> roomEntity
        0 * roomRepository.findByRoomName(_)
        result.isPresent()
        result.get() == roomEntity
    }

    def "getRoomByName returns room from database and caches it"() {
        when:
        Optional<RoomEntity> result = roomCacheService.getRoomByName(targetRoom)

        then:
        1 * cache.get(targetRoom, RoomEntity.class) >> null
        1 * roomRepository.findByRoomName(targetRoom) >> Optional.of(roomEntity)
        1 * cache.put(targetRoom, roomEntity)
        result.isPresent()
        result.get() == roomEntity
    }

    def "getRoomByName returns optional.empty when room is not found anywhere"() {
        when:
        Optional<RoomEntity> result = roomCacheService.getRoomByName(targetRoom)

        then:
        1 * cache.get(targetRoom, RoomEntity.class) >> null
        1 * roomRepository.findByRoomName(targetRoom) >> Optional.empty()
        0 * cache.put(_, _)
        result.isEmpty()
    }
}
