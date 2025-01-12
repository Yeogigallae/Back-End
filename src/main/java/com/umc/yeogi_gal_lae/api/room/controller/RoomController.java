package com.umc.yeogi_gal_lae.api.room.controller;

import com.umc.yeogi_gal_lae.api.room.dto.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 생성")
    @PostMapping
    public void createRoom(@RequestBody CreateRoomRequest request) {
        roomService.createRoom(request);
    }
}