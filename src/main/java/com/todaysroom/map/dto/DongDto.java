package com.todaysroom.map.dto;

import com.todaysroom.map.entity.Dong;

public record DongDto (Long id,
                       String dongCode,
                       String sidoName,
                       String gugunName,
                       String dongName){

    public static DongDto from (Dong dong){
        return new DongDto(dong.getId(),
                dong.getDongCode(),
                dong.getSidoName(),
                dong.getGugunName(),
                dong.getDongName());
    }
}
