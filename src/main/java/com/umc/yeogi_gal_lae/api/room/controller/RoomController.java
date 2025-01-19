package com.umc.yeogi_gal_lae.api.room.controller;

import com.umc.yeogi_gal_lae.api.room.dto.request.AddRoomMemberRequest;
import com.umc.yeogi_gal_lae.api.room.dto.request.CreateRoomRequest;
import com.umc.yeogi_gal_lae.api.room.service.RoomMemberService;
import com.umc.yeogi_gal_lae.api.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMemberService roomMemberService;

    @Operation(summary = "방 생성")
    @PostMapping
    public void createRoom(@RequestBody CreateRoomRequest request) {
        roomService.createRoom(request);
    }

    @Operation(summary = "방 멤버 추가")
    @PostMapping
    public void addMember(@RequestBody AddRoomMemberRequest request){
        roomMemberService.addMembers(request);
    }

}