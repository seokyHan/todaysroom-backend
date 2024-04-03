package com.todaysroom.map.dto;


import com.todaysroom.map.entity.Gugun;

public record GuGunDto(String guGunCode,
                       String guGunName,
                       String sidoName,
                       String sidoCode
) {

    public static GuGunDto from(final Gugun gugun){
        return new GuGunDto(gugun.getGugunCode(),
                gugun.getGugunName(),
                gugun.getSidoName(),
                gugun.getSidoCode()
        );
    }
}
