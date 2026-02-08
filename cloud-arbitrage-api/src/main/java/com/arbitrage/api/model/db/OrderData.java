package com.arbitrage.api.model.db;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderData {

    private Long orderId;
    private String orderNo;

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

    // 扩展信息保存该比赛的采集数据 用于复盘
    private JSONObject homeExtendInfo; // 主队扩展信息
    private JSONObject awayExtendInfo; // 客队扩展信息
    private JSONObject drawExtendInfo; // 平局扩展信息

    private Integer oddsTypeId; // 盘口id
    private String oddsType; // 盘口类型
    private String oddsData; // 记录让球数据 大小盘数据

    private BigDecimal homeOdds; // 主队赔率
    private BigDecimal homeBetAmount; // 主队下注金额

    private BigDecimal awayOdds; // 客队赔率
    private BigDecimal awayBetAmount; // 客队下注金额

    // 只有部分盘口才有平局赔率
    private BigDecimal drawOdds; // 平局赔率
    private BigDecimal drawBetAmount; // 平局下注金额

    private BigDecimal overOdds; // 大小盘 大赔率
    private BigDecimal overBetAmount; // 大小盘下注金额

    private BigDecimal underOdds; // 大小盘 小赔率
    private BigDecimal underBetAmount; // 大小盘下注金额

    private Integer state; // 订单状态

    /**
     * 套利因子 1/赔率1 + 1/赔率2 + ...
     * 小于1 表示有套利空间
     */
    private BigDecimal arbitrageFactor;

    private JSONObject homeOrderInfo; // 主队下单信息
    private JSONObject awayOrderInfo; // 可对下单信息
    private JSONObject drawOrderInfo; // 平局下单信息

    private JSONObject homePlatformBetData; // 主队平台下注返回数据
    private JSONObject awayPlatformBetData; // 客队平台下注返回数据
    private JSONObject drawPlatformBetData; // 平局平台下注返回数据

    /**
     * 净利润
     */
    private BigDecimal netProfit;

    private Date createdDate;
    private Date updatedDate;

    private Boolean deleteFlag; // 删除标志
}
