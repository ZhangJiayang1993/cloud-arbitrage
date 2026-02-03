package com.arbitrage.api.model.db;

import lombok.Data;

@Data
public class ComparisonTable {

    private Integer id;
    private String type; // 对照类型 NBA_TEAM=nba队伍
    private String cnName; // 中文名
    private String enName; // 英文名
    private String polymarketName;
    private String psName;

    public static final String NBA_TEAM = "NBA_TEAM";
    public static final String EPL_TEAM = "EPL_TEAM";
}
