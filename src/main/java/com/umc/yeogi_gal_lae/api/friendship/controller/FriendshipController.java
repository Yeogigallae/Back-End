package com.umc.yeogi_gal_lae.api.friendship.controller;

import com.umc.yeogi_gal_lae.api.friendship.dto.CreateInviteRequest;
import com.umc.yeogi_gal_lae.api.friendship.dto.CreateInviteResponse;
import com.umc.yeogi_gal_lae.api.friendship.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService; // 의존성 주입


    @PostMapping("/invite")
    public ResponseEntity<CreateInviteResponse> createInvite(@RequestBody CreateInviteRequest request) {
        String inviteUrl = friendshipService.generateInviteUrl(request.getInviterId(), request.getInviteeEmail());
        return ResponseEntity.ok(new CreateInviteResponse(inviteUrl));
    }

    @GetMapping("/accept")
    public ResponseEntity<String> acceptInvite(@RequestParam String token) {
        friendshipService.acceptInvite(token);
        return ResponseEntity.ok("Friendship created successfully!");
    }
}