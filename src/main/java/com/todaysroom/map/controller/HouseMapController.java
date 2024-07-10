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
    public ResponseEntity<List<GuGunDto>> gugunList(@RequestParam String sidoCode) {
        return ResponseEntity.ok(houseMapService.getGugunList(sidoCode));
    }

    @GetMapping("/dong")
    public ResponseEntity<List<DongDto>> dongList(@RequestParam String sidoName, @RequestParam String gugunName){
        return ResponseEntity.ok(houseMapService.getDongList(sidoName, gugunName));
    }

    @GetMapping("/gugun/house")
    public ResponseEntity<List<HouseInfoDto>> houseInfoListByGugun(@RequestParam String sidoName, @RequestParam String gugunName){
        return ResponseEntity.ok(houseMapService.getHouseInfoListByGuGun(sidoName, gugunName));
    }

    @GetMapping("/dong/house")
    public ResponseEntity<List<HouseInfoDto>> houseInfoListByDong(@RequestParam String sidoName, @RequestParam String gugunName, @RequestParam String dongName){
        return ResponseEntity.ok(houseMapService.getHouseInfoListByDong(sidoName, gugunName, dongName));
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
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseByGuGun(@RequestParam String sidoName, @RequestParam String gugunName, @RequestParam("userId") Long userId){
        return ResponseEntity.ok(houseMapService.getUserLikedHouseListByGuGun(sidoName, gugunName, userId));
    }

    @GetMapping("/dong/liked")
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseByDong(@RequestParam String sidoName,
                                                                   @RequestParam String gugunName,
                                                                   @RequestParam String dongName,
                                                                   @RequestParam("userId") Long userId){
        return ResponseEntity.ok(houseMapService.getUserLikedHouseListByDong(sidoName, gugunName, dongName, userId));
    }

    @GetMapping("/dong-search/liked")
    public ResponseEntity<List<HouseInfoDto>> userLikedHouseByDongSearch(@RequestParam String dongName, @RequestParam("userId") Long userId){
        return ResponseEntity.ok(houseMapService.getUserLikedHouseListByDongSearch(dongName, userId));
    }
}
