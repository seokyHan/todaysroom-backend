package com.todaysroom.map.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum AptKey {

    ROAD_NAME_BUILDING_PART_CODE("roadNameBuildingPartCode","도로명건물부번호코드"),
    LEGAL_COMPANION_NUMBER_CODE("legalCompanionNumberCode","법정동지번코드"),
    LOCAL_CODE("localCode","지역코드"),
    YEAR("year","년"),
    ROAD_NAME_GUNGU_CODE("roadNameGunguCode","도로명시군구코드"),
    ROAD_NAME_SERIAL_CODE("roadNameSerialCode","도로명일련번호코드"),
    APT_NAME("aptName","아파트"),
    ROAD_NAME_GROUND_CODE("roadNameGroundCode","도로명지상지하코드"),
    LOCAL_NUMBER("localNumber","지번"),
    ROAD_NAME("roadName","도로명"),
    RAGISTRATION_DATE("RegistrationDate","등기일자"),
    MONTH("month","월"),
    LEGAL_EUP_MYEON_CODE("legalEupMyeonCode","법정동읍면동코드"),
    LEGAL_SI_GUNGU_CODE("legalSiGunguCode","법정동시군구코드"),
    BUILD_YEAR("buildYear","건축년도"),
    EXCLUSIVE_AREA("exclusiveArea","전용면적"),
    CANCEL_REASON("cancelReason","해제사유발생일"),
    LEGAL("leagl","법정동"),
    SERIAL_NUMBER("serialNumber","일련번호"),
    AMOUNT("amount","거래금액"),
    ROAD_NAME_CODE("roadNameCode","도로명코드"),
    LEGAL_BUILDING("legalBuilding","법정동본번코드"),
    LOCAL_OF_AGENCY("locationOfAgency","중개사소재지"),
    STATUTORY_EASTERN_CODE("statutoryEasternCode","법정동부번코드"),
    CONTRACT_TYPE("contractType","거래유형"),
    RELEASE_STATUS("releaseStatus","해제여부"),
    FLOOR("floor","층"),
    DAY("day","일"),
    ROAD_NAME_BUILDING_CODE("roadNameBuildingCode","도로명건물본번호코드");

    private final String englishKey;
    private final String koreanKey;

}
