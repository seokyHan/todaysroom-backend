package com.todaysroom.map.dto;

import com.todaysroom.map.entity.Sido;

public record SidoDto(String sidoCode, String sidoName) {

    public static SidoDto from(Sido sido){
        return new SidoDto(sido.getSidoCode(), sido.getSidoName());
    }
}
