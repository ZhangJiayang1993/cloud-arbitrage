package com.arbitrage.api.enums;

import lombok.Getter;

@Getter
public enum LeagueEnum {

    NBA(1, "NBA"),
    ;

    private final Integer leagueId; // 联赛id
    private final String league; // 联赛

    LeagueEnum(Integer leagueId, String league) {
        this.leagueId = leagueId;
        this.league = league;
    }

     /**
     * 根据联赛名称获取联赛枚举
     * @param name 联赛名称
     * @return 联赛枚举
     */
    public static LeagueEnum getLeagueEnumPsByName(String name) {
        for (LeagueEnum leagueEnum : values()) {
            if (leagueEnum.getLeague().equalsIgnoreCase(name)) {
                return leagueEnum;
            }
        }
        return null;
    }

}
