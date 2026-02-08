package com.arbitrage.provider.service.collection;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.enums.LeagueEnum;
import com.arbitrage.api.enums.OddsTypeEnum;
import com.arbitrage.api.enums.PlatformEnum;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.model.db.ComparisonTable;
import com.arbitrage.api.service.CollectionService;
import com.arbitrage.api.service.ComparisonTableService;
import com.arbitrage.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.arbitrage.api.model.db.ComparisonTable.NBA_TEAM;

@Slf4j
@Service("PolymarketCollectionService")
public class PolymarketCollectionService implements CollectionService {

    @Autowired
    ComparisonTableService comparisonTableService;

    @Override
    public List<CollectionGameDataResp> collection() {
        List<ComparisonTable> comparisonTableList = comparisonTableService.getComparisonTableByType(NBA_TEAM);

        PlatformEnum platformEnum = PlatformEnum.POLYMARKET;
        List<CollectionGameDataResp> list = new ArrayList<>();

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
            CollectionGameDataResp collectionGameDataResp = new CollectionGameDataResp(platformEnum.getPlatformId(), platformEnum.getPlatform(), platformEnum.getCurrencyTypeEnum(), leagueEnum.getLeagueId(), leagueEnum.getLeague(), title, homeTeamId, awayTeamId, homeTeamName, awayTeamName, homeTeamCnName, awayTeamCnName, BigDecimal.ZERO, extendInfo, new ArrayList<>());
            for (int marketIndex = 0; marketIndex < marketsJson.size(); marketIndex++) {
                JSONObject marketJsonObject = marketsJson.getJSONObject(marketIndex);
                improveCompetitionData(collectionGameDataResp, marketJsonObject);
            }
            list.add(collectionGameDataResp);
        }

        return list;
    }

    public static void improveCompetitionData(CollectionGameDataResp collectionGameDataResp, JSONObject marketJsonObject) {
        BigDecimal bestBid = marketJsonObject.getBigDecimal("bestBid");
        BigDecimal bestAsk = marketJsonObject.getBigDecimal("bestAsk");
        if (bestBid == null || bestAsk == null) {
            return;
        }

        String sportsMarketType = marketJsonObject.getString("sportsMarketType");
        BigDecimal bestBidOdds = BigDecimal.ONE.divide(BigDecimal.ONE.subtract(bestBid), 3, RoundingMode.HALF_UP);
        BigDecimal bestAskOdds = BigDecimal.ONE.divide(bestAsk, 3, RoundingMode.HALF_UP);

        if (bestBidOdds.compareTo(BigDecimal.TEN) > 0 || bestAskOdds.compareTo(BigDecimal.TEN) > 0) {
            return;
        }

        switch (sportsMarketType) {
            case "moneyline": {
                // 胜负盘
//                log.info("胜负盘 {}", marketJsonObject.getString("question"));
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.MONEY_LINE;
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), null, bestBidOdds, null, bestAskOdds, null, null, null, null, null, null, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "spreads": {
                // 让分盘
                String question = marketJsonObject.getString("question");
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.HANDICAP;
                BigDecimal line = marketJsonObject.getBigDecimal("line");
                boolean homeSpreads = question.contains(collectionGameDataResp.getHomeTeamName());
                line = homeSpreads ? line : line.negate();
                BigDecimal homeOdds = homeSpreads ? bestAskOdds : bestBidOdds;
                BigDecimal awayOdds = homeSpreads ? bestBidOdds : bestAskOdds;
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), line.toString(), homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "totals": {
                // 大小分盘
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.OVER_UNDER;
//                log.info("大小分盘 {}", marketJsonObject.getString("question"));
                BigDecimal line = marketJsonObject.getBigDecimal("line");
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), line.toString(), null, null, null, null, null, null, bestAskOdds, null, bestBidOdds, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "first_half_moneyline": {
                // 上半场胜负盘
//                log.info("上半场胜负盘 {}", marketJsonObject.getString("question"));
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.FIRST_HALF_MONEY_LINE;
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), null, bestBidOdds, null, bestAskOdds, null, null, null, null, null, null, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "first_half_spreads": {
                // 上半场让分盘
//                log.info("上半场让分盘 {}", marketJsonObject.getString("question"));
                String question = marketJsonObject.getString("question");
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.FIRST_HALF_HANDICAP;
                BigDecimal line = marketJsonObject.getBigDecimal("line");
                boolean homeSpreads = question.contains(collectionGameDataResp.getHomeTeamName());
                line = homeSpreads ? line : line.negate();
                BigDecimal homeOdds = homeSpreads ? bestAskOdds : bestBidOdds;
                BigDecimal awayOdds = homeSpreads ? bestBidOdds : bestAskOdds;
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), line.toString(), homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "first_half_totals": {
                // 上半场总进球数盘
//                log.info("上半场总进球数盘 {}", marketJsonObject.getString("question"));
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.FIRST_HALF_OVER_UNDER;
                BigDecimal line = marketJsonObject.getBigDecimal("line");
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), line.toString(), null, null, null, null, null, null, bestAskOdds, null, bestBidOdds, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "second_half_moneyline": {
                // 下半场胜负盘
//                log.info("下半场胜负盘 {}", marketJsonObject.getString("question"));
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.SECOND_HALF_MONEY_LINE;
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), null, bestBidOdds, null, bestAskOdds, null, null, null, null, null, null, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "second_half_spreads": {
                // 下半场让分盘
//                log.info("下半场让分盘 {}", marketJsonObject.getString("question"));
                String question = marketJsonObject.getString("question");
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.SECOND_HALF_HANDICAP;
                BigDecimal line = marketJsonObject.getBigDecimal("line");
                boolean homeSpreads = question.contains(collectionGameDataResp.getHomeTeamName());
                line = homeSpreads ? line : line.negate();
                BigDecimal homeOdds = homeSpreads ? bestAskOdds : bestBidOdds;
                BigDecimal awayOdds = homeSpreads ? bestBidOdds : bestAskOdds;
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), line.toString(), homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }
            case "second_half_totals": {
                // 下半场总进球数盘
//                log.info("下半场总进球数盘 {}", marketJsonObject.getString("question"));
                OddsTypeEnum oddsTypeEnum = OddsTypeEnum.SECOND_HALF_OVER_UNDER;
                BigDecimal line = marketJsonObject.getBigDecimal("line");
                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), line.toString(), null, null, null, null, null, null, bestAskOdds, null, bestBidOdds, null, null);
                collectionGameDataResp.getOdds().add(oddsTypeResp);
                break;
            }

//            case "assists":
//                // 选手助攻盘 忽略
//                log.info("选手助攻盘 忽略{}", marketJsonObject.getString("question"));
//                break;
//            case "points":
//                // 选手球数盘 忽略
//                log.info("总进球数盘 忽略{}", marketJsonObject.getString("question"));
//                break;
//            case "rebounds":
//                // 篮板盘
//                log.info("篮板盘 忽略{}", marketJsonObject.getString("question"));
//                break;
        }
    }

}
