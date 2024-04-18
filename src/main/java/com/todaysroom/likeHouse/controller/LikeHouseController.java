package com.todaysroom.likeHouse.controller;

import com.todaysroom.likeHouse.dto.LikeHouseRequestDto;
import com.todaysroom.likeHouse.dto.LikeHouseResponseDto;
import com.todaysroom.likeHouse.service.LikeHouseService;
import com.todaysroom.map.dto.HouseInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/like-house")
public class LikeHouseController {

    private final LikeHouseService likeHouseService;

    @GetMapping("/list")
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseList(@RequestParam("userId")Long id){
        return ResponseEntity.ok(likeHouseService.getUserLikedHouseList(id));
    }

    @GetMapping("/codes")
    public ResponseEntity<List<LikeHouseResponseDto>> userLikedAptCodes(@RequestParam("userId") Long id){
        return ResponseEntity.ok(likeHouseService.getLikedAptCode(id));
    }

    @PostMapping
    public ResponseEntity<Long> addLikedHouse(@RequestBody @Valid LikeHouseRequestDto likeHouseRequestDto){
        return ResponseEntity.created(URI.create("/" + likeHouseService.addLikedHouse(likeHouseRequestDto))).build();
    }

    @DeleteMapping
    public ResponseEntity deleteLikeHouse(@RequestBody @Valid LikeHouseRequestDto likeHouseRequestDto){
        likeHouseService.deleteLikedHouse(likeHouseRequestDto);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
