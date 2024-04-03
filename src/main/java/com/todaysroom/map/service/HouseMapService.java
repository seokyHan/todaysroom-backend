package com.todaysroom.map.service;


import com.todaysroom.map.dto.GuGunDto;
import com.todaysroom.map.dto.SidoDto;
import com.todaysroom.map.entity.Gugun;
import com.todaysroom.map.repository.GugunRepository;
import com.todaysroom.map.repository.SidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class HouseMapService {

    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;

    public List<SidoDto> getSidoList(){
        return sidoRepository.findAll().stream().map(SidoDto::from).collect(Collectors.toList());
    }

    public List<GuGunDto> getGugunList(String sidoCode){
        List<Gugun> gugunList = gugunRepository.findBySidoCode(sidoCode);
        return gugunList.stream().map(GuGunDto::from).collect(Collectors.toList());
    }



}
