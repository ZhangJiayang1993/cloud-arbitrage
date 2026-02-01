package com.arbitrage;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.enums.LeagueEnum;
import com.arbitrage.api.enums.OddsTypeEnum;
import com.arbitrage.api.enums.PlatformEnum;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class PSCollection {

    public static void main(String[] args) {
        PlatformEnum platformEnum = PlatformEnum.PS;
        List<CollectionGameDataResp> collectionGameDataRespList = new ArrayList<>();

        String getListUrl = "https://api.ps3838.com/v3/fixtures?sportId=4&leagueIds=487&isLive=true";
        Map<String, String> headers = new HashMap<>();
        //MTU4MTk2NTIxMjN6ank6R1RTNjMyOTAwNw==
        headers.put("Authorization", "Basic R1RTNjMyOTAwNzoxNTgxOTY1MjEyM3pqeQ==");
        headers.put("User-Agent", "Mozilla/5.0");
        HttpResponse getListResponse = HttpRequest.get(getListUrl).addHeaders(headers).execute();
        getListResponse.close();

        Set<String> gameIds = new HashSet<>();

        JSONObject listResponseJson = JSONObject.parseObject(getListResponse.body());
        JSONArray leagueJson = listResponseJson.getJSONArray("league");
        for (int leagueIndex = 0; leagueIndex < leagueJson.size(); leagueIndex++) {
            JSONObject leagueItem = leagueJson.getJSONObject(leagueIndex);
            String leagueId = leagueItem.getString("id");
            String league = leagueItem.getString("name");
            LeagueEnum leagueEnum = LeagueEnum.getLeagueEnumPsByName(league);
            if (leagueEnum == null) {
                log.warn("未找到联赛枚举: {}", league);
                continue;
            }

            JSONArray eventsJson = leagueItem.getJSONArray("events");
            for (int eventIndex = 0; eventIndex < eventsJson.size(); ++eventIndex) {
                //{"away":"San Antonio Spurs","altTeaser":false,"version":646508434,"parentId":1622978205,"home":"Charlotte Hornets","betAcceptanceType":0,"resultingUnit":"Regular","rotNum":"9521","id":1623347371,"starts":"2026-01-31T20:00:00Z","liveStatus":1,"status":"H","parlayRestriction":2}
                JSONObject eventItem = eventsJson.getJSONObject(eventIndex);
                String home = eventItem.getString("home");
                String away = eventItem.getString("away");
                if (gameIds.add(eventItem.getString("parentId"))) {
                    String title = home + " vs " + away;
                    JSONObject extendInfo = new JSONObject();
                    extendInfo.put("id", leagueId);
                    extendInfo.put("eventId", eventItem.getString("parentId"));
                    CollectionGameDataResp collectionGameDataResp = new CollectionGameDataResp(platformEnum.getPlatformId(), platformEnum.getPlatform(), leagueEnum.getLeagueId(), leagueEnum.getLeague(), title, null, null, null, null, null, null, BigDecimal.ZERO, extendInfo, new ArrayList<>());
                    collectionGameDataRespList.add(collectionGameDataResp);
                }
            }

        }
        String eventIds = String.join(",", gameIds);
        log.info("eventIds: {}", eventIds);
        String getDetailUrl = "https://api.ps3838.com/v4/odds/parlay?sportId=4&leagueIds=487&oddsFormat=HongKong&eventIds=" + eventIds;
        HttpResponse getDetailResponse = HttpRequest.get(getDetailUrl).addHeaders(headers).execute();
        getDetailResponse.close();
        JSONObject detailJson = JSONObject.parseObject(getDetailResponse.body());
        JSONArray leaguesJson = detailJson.getJSONArray("leagues");

        for (int leagueIndex = 0; leagueIndex < leaguesJson.size(); leagueIndex++) {
            JSONObject leagueItem = leaguesJson.getJSONObject(leagueIndex);
//            String id = leagueItem.getString("id");
            JSONArray eventsJson = leagueItem.getJSONArray("events");
            for (int eventIndex = 0; eventIndex < eventsJson.size(); eventIndex++) {
                JSONObject eventItem = eventsJson.getJSONObject(eventIndex);
                String gameId = eventItem.getString("id");
                JSONObject periodsJson = eventItem.getJSONArray("periods").getJSONObject(0);

                try {
                    // spreads 让球盘
                    JSONArray spreadsJson = periodsJson.getJSONArray("spreads");
                    if (spreadsJson != null && !spreadsJson.isEmpty()) {
                        for (int spreadIndex = 0; spreadIndex < spreadsJson.size(); spreadIndex++) {
                            JSONObject spreadItem = spreadsJson.getJSONObject(spreadIndex);
                            BigDecimal homeOdds = spreadItem.getBigDecimal("home").add(BigDecimal.ONE);
                            BigDecimal awayOdds = spreadItem.getBigDecimal("away").add(BigDecimal.ONE);
                            String handicap = spreadItem.getString("hdp");
                            OddsTypeEnum oddsTypeEnum = OddsTypeEnum.HANDICAP;
                            CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), handicap, homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);

                            collectionGameDataRespList.stream().filter(resp -> resp.getExtendInfo().getString("eventId").equals(gameId)).findFirst().ifPresent(resp -> resp.getOdds().add(oddsTypeResp));

                        }
                    }
                } catch (Exception e) {
                    log.error("spreads error", e);
                }

                try {
                    // 大小盘
                    JSONArray overUnderJson = periodsJson.getJSONArray("totals");
                    if (overUnderJson != null && !overUnderJson.isEmpty()) {
                        for (int overUnderIndex = 0; overUnderIndex < overUnderJson.size(); overUnderIndex++) {
                            JSONObject overUnderItem = overUnderJson.getJSONObject(overUnderIndex);
                            BigDecimal overOdds = overUnderItem.getBigDecimal("over").add(BigDecimal.ONE);
                            BigDecimal underOdds = overUnderItem.getBigDecimal("under").add(BigDecimal.ONE);
                            String overUnder = overUnderItem.getString("points");
                            OddsTypeEnum oddsTypeEnum = OddsTypeEnum.OVER_UNDER;
                            CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(),
                                    oddsTypeEnum.getOddsType(), overUnder, null, null, null, null, null, null, overOdds, null, underOdds, null, null);

                            collectionGameDataRespList.stream().filter(resp -> resp.getExtendInfo().getString("eventId").equals(gameId)).findFirst().ifPresent(resp -> resp.getOdds().add(oddsTypeResp));

                        }
                    }
                } catch (Exception e) {
                    log.error("overUnder error", e);
                }

                try {
                    // 胜负盘
                    JSONObject moneyLinesJson = periodsJson.getJSONObject("moneyline");
                    if (moneyLinesJson != null) {
                        BigDecimal homeOdds = moneyLinesJson.getBigDecimal("home");
                        BigDecimal awayOdds = moneyLinesJson.getBigDecimal("away");
                        OddsTypeEnum oddsTypeEnum = OddsTypeEnum.MONEY_LINE;
                        CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(),
                                oddsTypeEnum.getOddsType(), null, homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);

                        collectionGameDataRespList.stream().filter(resp -> resp.getExtendInfo().getString("eventId").equals(gameId)).findFirst().ifPresent(resp -> resp.getOdds().add(oddsTypeResp));
                    }
                } catch (Exception e) {
                    log.error("moneyLines error", e);
                }
            }

        }

        System.out.println(JSONObject.toJSONString(collectionGameDataRespList));

    }

}
