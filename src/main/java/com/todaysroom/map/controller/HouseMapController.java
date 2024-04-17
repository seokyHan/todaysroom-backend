package com.todaysroom.map.controller;


import com.todaysroom.map.dto.*;
import com.todaysroom.map.entity.HouseInfo;
import com.todaysroom.map.service.HouseMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "map")
public class HouseMapController {

    private final HouseMapService houseMapService;

    @GetMapping("/sido")
    public ResponseEntity<List<SidoDto>> sidoList() {
        return ResponseEntity.ok(houseMapService.getSidoList());
    }

    @GetMapping("/gugun")
    public ResponseEntity<List<GuGunDto>> gugunList(@RequestParam("sidoCode") String sidoCode) {
        return ResponseEntity.ok(houseMapService.getGugunList(sidoCode));
    }

    @GetMapping("/dong")
    public ResponseEntity<List<DongDto>> dongList(@RequestBody MapRequest mapRequest){
        return ResponseEntity.ok(houseMapService.getDongList(mapRequest));
    }

    @GetMapping("/gugun/house")
    public ResponseEntity<List<HouseInfoDto>> houseInfoListByGugun(@RequestBody MapRequest mapRequest){
        return ResponseEntity.ok(houseMapService.getHouseInfoListByGuGun(mapRequest));
    }

    @GetMapping("/dong/house")
    public ResponseEntity<List<HouseInfoDto>> houseInfoListByDong(@RequestBody MapRequest mapRequest){
        return ResponseEntity.ok(houseMapService.getHouseInfoListByDong(mapRequest));
    }

    @GetMapping("/dong-search")
    public ResponseEntity<List<HouseInfoDto>> houseInfoListByDongSearch(@RequestParam("dongName") String dongName){
        return ResponseEntity.ok(houseMapService.getHouseInfoListByDongSearch(dongName));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<HouseInfoDto>> houseInfoListRecommend(@RequestParam("dongName") String dongName){
        return ResponseEntity.ok(houseMapService.getHouseInfoRecommend(dongName));
    }

    @GetMapping("/gu/liked")
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseByGuGun(@RequestBody MapRequest mapRequest, @RequestParam("userId") Long userId){
        return ResponseEntity.ok(houseMapService.getUserLikedHouseListByGuGun(mapRequest, userId));
    }

    @GetMapping("/dong/liked")
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseByDong(@RequestBody MapRequest mapRequest, @RequestParam("userId") Long userId){
        return ResponseEntity.ok(houseMapService.getUserLikedHouseListByDong(mapRequest, userId));
    }

    @GetMapping("/dong-search/liked")
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseByDongSearch(@RequestBody MapRequest mapRequest, @RequestParam("userId") Long userId){
        return ResponseEntity.ok(houseMapService.getUserLikedHouseListByDongSearch(mapRequest.dongName(), userId));
    }
}
