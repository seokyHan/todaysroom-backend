package com.todaysroom.map.controller;

import com.todaysroom.map.service.HouseMapService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class HouseMapController {

    private final HouseMapService houseMapService;

    @GetMapping("/getHouseInfo")
    public ResponseEntity<String> houseInfo() {

        return ResponseEntity.ok(houseMapService.getHouseInfo());
    }
}
