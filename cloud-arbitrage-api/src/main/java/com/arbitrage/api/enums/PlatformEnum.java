package com.arbitrage.api.enums;

import lombok.Getter;

@Getter
public enum PlatformEnum {

    PS(1, "PS", CurrencyTypeEnum.RMB),
    POLYMARKET(2, "POLYMARKET", CurrencyTypeEnum.USD),
    ;

    private final Integer platformId;
    private final String platform;
    private final CurrencyTypeEnum currencyTypeEnum;

    PlatformEnum(Integer platformId, String platform, CurrencyTypeEnum currencyTypeEnum) {
        this.platformId = platformId;
        this.platform = platform;
        this.currencyTypeEnum = currencyTypeEnum;
    }
}
