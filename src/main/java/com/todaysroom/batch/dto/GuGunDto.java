package com.todaysroom.batch.dto;


import com.todaysroom.map.entity.Gugun;

public record GuGunDto(String guGunCode, String guGunName) {

    public static GuGunDto from(final Gugun gugun){
        return new GuGunDto(gugun.getGugunCode(), gugun.getGugunName());
    }
}
