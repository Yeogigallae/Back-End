package com.umc.yeogi_gal_lae.api.place.service;

import com.umc.yeogi_gal_lae.api.place.converter.PlaceConverter;
import com.umc.yeogi_gal_lae.api.place.domain.Place;
import com.umc.yeogi_gal_lae.api.place.dto.request.PlaceRequest;
import com.umc.yeogi_gal_lae.api.place.repository.PlaceRepository;
import com.umc.yeogi_gal_lae.api.room.domain.Room;
import com.umc.yeogi_gal_lae.api.room.repository.RoomRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Place addPlace(Long roomId, PlaceRequest placeRequest) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));
        Place place = PlaceConverter.toPlaceEntity(room, placeRequest);
        placeRepository.save(place);

        return place;
    }

    public List<Place> getAllPlacesByRoomId(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new BusinessException(ErrorCode.ROOM_NOT_FOUND);
        }
        List<Place> places = placeRepository.findAllByRoomId(roomId);

        return places;
    }

    public Place getPlaceById(Long roomId, Long placeId) {
        if (!roomRepository.existsById(roomId)) {
            throw new BusinessException(ErrorCode.ROOM_NOT_FOUND);
        }
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
        return place;
    }

    @Transactional
    public void deletePlaceById(Long roomId, Long placeId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROOM_NOT_FOUND));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        if (!place.getRoom().getId().equals(room.getId())) {
            throw new BusinessException(ErrorCode.INVALID_PLACE_FOR_ROOM);
        }

        placeRepository.deleteById(placeId);
    }
}
