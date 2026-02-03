package com.arbitrage.api.enums;

import lombok.Getter;

@Getter
public enum PlatformEnum {

    PS(1, "PS"),
    POLYMARKET(2, "POLYMARKET"),


    ;
    private final Integer platformId;
    private final String platform;

    PlatformEnum(Integer platformId, String platform) {
        this.platformId = platformId;
        this.platform = platform;
    }
}
