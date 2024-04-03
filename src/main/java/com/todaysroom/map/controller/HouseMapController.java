package com.todaysroom.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.todaysroom.map.dto.GuGunDto;
import com.todaysroom.map.dto.HouseInfoDto;
import com.todaysroom.map.dto.SidoDto;
import com.todaysroom.map.service.HouseMapService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "map")
public class HouseMapController {

    private final HouseMapService houseMapService;

    @GetMapping("/sidoList")
    public ResponseEntity<List<SidoDto>> sidoList() {

        return ResponseEntity.ok(houseMapService.getSidoList());
    }

    @GetMapping("/gugunList")
    public ResponseEntity<List<GuGunDto>> gugunList(@RequestParam("sidoCode") String sidoCode) {

        return ResponseEntity.ok(houseMapService.getGugunList(sidoCode));
    }
}
