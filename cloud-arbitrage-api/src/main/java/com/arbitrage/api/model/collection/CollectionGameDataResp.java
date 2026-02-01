package com.arbitrage.api.model.collection;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionGameDataResp {

    private Integer platformId; // 平台id
    private String platform; // 平台
    private Integer leagueId; // 联赛id
    private String league; // 联赛
    private String title; // 标题
    private String homeTeamId; // 主队id
    private String awayTeamId; // 客队id
    private String homeTeamName; // 主队
    private String awayTeamName; // 客队
    private String homeTeamCnName; // 主队中文名
    private String awayTeamCnName; // 客队中文名
    private BigDecimal rebate; // 返点
    private JSONObject extendInfo; // 扩展信息

    private List<OddsTypeResp> odds; // 盘口信息

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsTypeResp {
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

        private JSONObject extendInfo; // 扩展信息
    }
}
