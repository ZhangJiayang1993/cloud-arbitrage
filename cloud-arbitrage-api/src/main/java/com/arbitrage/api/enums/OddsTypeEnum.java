package com.arbitrage.api.enums;

import lombok.Getter;

@Getter
public enum OddsTypeEnum {

    MONEY_LINE(1, "胜负盘"),
    HANDICAP(2, "让球盘"),
    OVER_UNDER(3, "大小盘"),

    // 上半场
    FIRST_HALF_MONEY_LINE(4, "上半场胜负盘"),
    FIRST_HALF_HANDICAP(5, "上半场让球盘"),
    FIRST_HALF_OVER_UNDER(6, "上半场大小盘"),

    // 下半场
    SECOND_HALF_MONEY_LINE(7, "下半场胜负盘"),
    SECOND_HALF_HANDICAP(8, "下半场让球盘"),
    SECOND_HALF_OVER_UNDER(9, "下半场大小盘"),
    ;

    private final Integer oddsTypeId;
    private final String oddsType;

    OddsTypeEnum(Integer oddsTypeId, String oddsType) {
        this.oddsTypeId = oddsTypeId;
        this.oddsType = oddsType;
    }

    public static OddsTypeEnum getByOddsTypeId(Integer oddsTypeId) {
        for (OddsTypeEnum oddsTypeEnum : values()) {
            if (oddsTypeEnum.getOddsTypeId().equals(oddsTypeId)) {
                return oddsTypeEnum;
            }
        }
        return null;
    }
}
