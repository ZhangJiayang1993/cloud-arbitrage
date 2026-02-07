package com.arbitrage;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.enums.LeagueEnum;
import com.arbitrage.api.enums.PlatformEnum;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.model.db.ComparisonTable;
import com.arbitrage.common.utils.DateUtils;
import com.arbitrage.provider.service.collection.PolymarketCollectionService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PolymarketCollection {

    public static void main(String[] args) {
        List<ComparisonTable> comparisonTableList = JSONArray.parseArray(DateUtils.testData).toJavaList(ComparisonTable.class);

        PlatformEnum platformEnum = PlatformEnum.POLYMARKET;
        List<CollectionGameDataResp> collectionGameDataRespList = new ArrayList<>();

        String minEndDate = DateUtils.getAfterDate(-1);
        String maxEndDate = DateUtils.getAfterDate(3);
        String getListUrl = "https://gamma-api.polymarket.com/events?series_id=10345&active=true&closed=false&end_date_min=" + minEndDate + "&end_date_max=" + maxEndDate + "&limit=10";

        JSONArray listResponseJson = JSONArray.parseArray(HttpUtil.get(getListUrl));
        for (int i = 0; i < listResponseJson.size(); i++) {
            JSONObject jsonObject = listResponseJson.getJSONObject(i);
            String seriesSlug = jsonObject.getString("seriesSlug");
            String league = seriesSlug.split("-")[0];
            LeagueEnum leagueEnum = LeagueEnum.getLeagueEnumPsByName(league);
            if (leagueEnum == null) {
                log.warn("未找到联赛枚举: {}", league);
                continue;
            }
            // polymarket 主客队是反的，需要交换
            String gameTitle = jsonObject.getString("title");
            String home = gameTitle.split(" vs. ")[1];
            String away = gameTitle.split(" vs. ")[0];
            String title = home + " vs " + away;

            String homeTeamId = null; // 主队id
            String awayTeamId = null; // 客队id
            String homeTeamName = null; // 主队
            String awayTeamName = null; // 客队
            String homeTeamCnName = null; // 主队中文名
            String awayTeamCnName = null; // 客队中文名

            for (ComparisonTable comparisonTable : comparisonTableList) {
                if (comparisonTable.getPolymarketName().equals(home)) {
                    homeTeamId = comparisonTable.getId().toString();
                    homeTeamName = comparisonTable.getEnName();
                    homeTeamCnName = comparisonTable.getCnName();
                } else if (comparisonTable.getPolymarketName().equals(away)) {
                    awayTeamId = comparisonTable.getId().toString();
                    awayTeamName = comparisonTable.getEnName();
                    awayTeamCnName = comparisonTable.getCnName();
                }
            }

            log.info("{} {} {}", platformEnum, leagueEnum, title);
            JSONArray marketsJson = jsonObject.getJSONArray("markets");
            JSONObject extendInfo = new JSONObject();

            CollectionGameDataResp collectionGameDataResp = new CollectionGameDataResp(platformEnum.getPlatformId(), platformEnum.getPlatform(), leagueEnum.getLeagueId(), leagueEnum.getLeague(), title, homeTeamId, awayTeamId, homeTeamName, awayTeamName, homeTeamCnName, awayTeamCnName, BigDecimal.ZERO, extendInfo, new ArrayList<>());
            for (int marketIndex = 0; marketIndex < marketsJson.size(); marketIndex++) {
                JSONObject marketJsonObject = marketsJson.getJSONObject(marketIndex);
                PolymarketCollectionService.improveCompetitionData(collectionGameDataResp, marketJsonObject);
            }
            collectionGameDataRespList.add(collectionGameDataResp);
        }
        log.info("xxxxxx {}", JSONObject.toJSONString(collectionGameDataRespList));

    }


}
