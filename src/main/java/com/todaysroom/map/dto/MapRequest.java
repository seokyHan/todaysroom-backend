package com.todaysroom.map.dto;

import static org.apache.commons.lang3.StringUtils.split;

public record MapRequest(String sidoName,
                         String gugunName,
                         String dongName) {

}
