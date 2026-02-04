package com.arbitrage.api.model.order;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 有套利机会的比赛数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArbitrageData {
    private Integer leagueId; // 联赛id
    private String league; // 联赛

    private String enTitle; // en标题
    private String cnTitle; // cn标题

    private Integer homePlatformId; // 主队平台id
    private Integer awayPlatformId; // 客队平台id

    private String homePlatform; // 主队平台
    private String awayPlatform; // 客队平台

    private String homeTeamId; // 主队id
    private String awayTeamId; // 客队id

    private String homeTeamName; // 主队
    private String awayTeamName; // 客队

    private String homeTeamCnName; // 主队中文名
    private String awayTeamCnName; // 客队中文名

    private BigDecimal homeRebate; // 主队平台返点
    private BigDecimal awayRebate; // 客队平台返点

    private JSONObject homeExtendInfo; // 主队扩展信息
    private JSONObject awayExtendInfo; // 客队扩展信息

    private Integer oddsTypeId; // 盘口id
    private String oddsType; // 盘口类型
    private String oddsData; // 记录让球数据 大小盘数据

    private BigDecimal homeOdds; // 主队赔率
    private BigDecimal homeMaxBettingAmount; // 主队最大可下注金额

    private BigDecimal awayOdds; // 客队赔率
    private BigDecimal awayMaxBettingAmount; // 客队最大可下注金额

    // 只有部分盘口才有平局赔率
    private BigDecimal drawOdds; // 平局赔率
    private BigDecimal drawMaxBettingAmount; // 平局最大可下注金额

    private BigDecimal overOdds; // 大小盘 大赔率
    private BigDecimal overMaxBettingAmount; // 大小盘 最大可下注金额

    private BigDecimal underOdds; // 大小盘 小赔率
    private BigDecimal underMaxBettingAmount; // 大小盘 最大可下注金额

    /**
     * 套利因子 1/赔率1 + 1/赔率2 + ...
     * 小于1 表示有套利空间
     */
    private BigDecimal arbitrageFactor;


}
