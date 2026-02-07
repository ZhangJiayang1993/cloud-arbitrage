package com.arbitrage.provider.service.collection;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.enums.LeagueEnum;
import com.arbitrage.api.enums.OddsTypeEnum;
import com.arbitrage.api.enums.PlatformEnum;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.model.db.ComparisonTable;
import com.arbitrage.api.service.CollectionService;
import com.arbitrage.api.service.ComparisonTableService;
import com.arbitrage.api.service.SettingService;
import com.arbitrage.common.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.arbitrage.api.constants.CommonConstants.PS_ACCOUNT;
import static com.arbitrage.api.constants.RedisConstants.FIVE_MINUTES;
import static com.arbitrage.api.constants.RedisConstants.PS_AUTHORIZATION;
import static com.arbitrage.api.model.db.ComparisonTable.NBA_TEAM;

@Slf4j
@Service("PSCollectionService")
public class PSCollectionService implements CollectionService {

    private final RedisRepository redisRepository;

    private final SettingService settingService;

    private final ComparisonTableService comparisonTableService;

    private final PlatformEnum platformEnum = PlatformEnum.PS;

    public PSCollectionService(RedisRepository redisRepository, SettingService settingService, ComparisonTableService comparisonTableService) {
        this.redisRepository = redisRepository;
        this.settingService = settingService;
        this.comparisonTableService = comparisonTableService;
    }

    @Override
    public List<CollectionGameDataResp> collection() {
        List<ComparisonTable> comparisonTableList = comparisonTableService.getComparisonTableByType(NBA_TEAM);


        String authorization = getAuthorization();
        log.info("authorization: {}", authorization);
        List<CollectionGameDataResp> list = new ArrayList<>();
        String getListUrl = "https://api.ps3838.com/v3/fixtures?sportId=4&leagueIds=487&isLive=true";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authorization);
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

                String homeTeamId = null; // 主队id
                String awayTeamId = null; // 客队id
                String homeTeamName = null; // 主队
                String awayTeamName = null; // 客队
                String homeTeamCnName = null; // 主队中文名
                String awayTeamCnName = null; // 客队中文名

                for (ComparisonTable comparisonTable : comparisonTableList) {
                    if (comparisonTable.getPsName().equals(home)) {
                        homeTeamId = comparisonTable.getId().toString();
                        homeTeamName = comparisonTable.getEnName();
                        homeTeamCnName = comparisonTable.getCnName();
                    } else if (comparisonTable.getPsName().equals(away)) {
                        awayTeamId = comparisonTable.getId().toString();
                        awayTeamName = comparisonTable.getEnName();
                        awayTeamCnName = comparisonTable.getCnName();
                    }
                }
                if (homeTeamId == null) {
                    log.warn("未找到主队: {}", home);
                    continue;
                }
                if (awayTeamId == null) {
                    log.warn("未找到客队: {}", away);
                    continue;
                }


                if (gameIds.add(eventItem.getString("parentId"))) {
                    String title = home + " vs " + away;
                    log.info("title: {}", title);
                    JSONObject extendInfo = new JSONObject();
                    extendInfo.put("id", leagueId);
                    extendInfo.put("eventId", eventItem.getString("parentId"));
                    CollectionGameDataResp collectionGameDataResp = new CollectionGameDataResp(platformEnum.getPlatformId(), platformEnum.getPlatform(), leagueEnum.getLeagueId(), leagueEnum.getLeague(), title, homeTeamId, awayTeamId, homeTeamName, awayTeamName, homeTeamCnName, awayTeamCnName, BigDecimal.ZERO, extendInfo, new ArrayList<>());
                    list.add(collectionGameDataResp);
                }
            }

        }
        String eventIds = String.join(",", gameIds);
        log.info("eventIds: {}", eventIds);
        String getDetailUrl = "https://api.ps3838.com/v4/odds/parlay?sportId=4&leagueIds=487&oddsFormat=DECIMAL&eventIds=" + eventIds;
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
                JSONArray periodsArrayJson = eventItem.getJSONArray("periods");

                for (int periodIndex = 0; periodIndex < periodsArrayJson.size(); periodIndex++) {
                    JSONObject periodsJson = periodsArrayJson.getJSONObject(periodIndex);
                    int status = periodsJson.getIntValue("status");
                    if (status != 1) {
                        log.info("忽略的 status: {}", status);
                        continue;
                    }
                    Integer number = periodsJson.getInteger("number");
                    if (number == null || (number != 0 && number != 1)) {
                        log.info("忽略的 number: {}", number);
                        continue;
                    }
                    try {
                        // 胜负盘
                        JSONObject moneyLinesJson = periodsJson.getJSONObject("moneyline");
                        if (moneyLinesJson != null) {
                            BigDecimal homeOdds = moneyLinesJson.getBigDecimal("home");
                            BigDecimal awayOdds = moneyLinesJson.getBigDecimal("away");
                            OddsTypeEnum oddsTypeEnum = number == 0 ? OddsTypeEnum.MONEY_LINE : OddsTypeEnum.FIRST_HALF_MONEY_LINE;
                            CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(),
                                    oddsTypeEnum.getOddsType(), null, homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);

                            list.stream().filter(resp -> resp.getExtendInfo().getString("eventId").equals(gameId)).findFirst().ifPresent(resp -> resp.getOdds().add(oddsTypeResp));
                        }
                    } catch (Exception e) {
                        log.error("moneyLines error", e);
                    }

                    try {
                        // spreads 让球盘
                        JSONArray spreadsJson = periodsJson.getJSONArray("spreads");
                        if (spreadsJson != null && !spreadsJson.isEmpty()) {
                            for (int spreadIndex = 0; spreadIndex < spreadsJson.size(); spreadIndex++) {
                                JSONObject spreadItem = spreadsJson.getJSONObject(spreadIndex);
                                BigDecimal homeOdds = spreadItem.getBigDecimal("home");
                                BigDecimal awayOdds = spreadItem.getBigDecimal("away");
                                String handicap = spreadItem.getBigDecimal("hdp").negate().toString();
                                OddsTypeEnum oddsTypeEnum = number == 0 ? OddsTypeEnum.HANDICAP : OddsTypeEnum.FIRST_HALF_HANDICAP;
                                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(), oddsTypeEnum.getOddsType(), handicap, homeOdds, null, awayOdds, null, null, null, null, null, null, null, null);

                                list.stream().filter(resp -> resp.getExtendInfo().getString("eventId").equals(gameId)).findFirst().ifPresent(resp -> resp.getOdds().add(oddsTypeResp));

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
                                BigDecimal overOdds = overUnderItem.getBigDecimal("over");
                                BigDecimal underOdds = overUnderItem.getBigDecimal("under");
                                String overUnder = overUnderItem.getString("points");
                                OddsTypeEnum oddsTypeEnum = number == 0 ? OddsTypeEnum.OVER_UNDER : OddsTypeEnum.FIRST_HALF_OVER_UNDER;
                                CollectionGameDataResp.OddsTypeResp oddsTypeResp = new CollectionGameDataResp.OddsTypeResp(oddsTypeEnum.getOddsTypeId(),
                                        oddsTypeEnum.getOddsType(), overUnder, null, null, null, null, null, null, overOdds, null, underOdds, null, null);

                                list.stream().filter(resp -> resp.getExtendInfo().getString("eventId").equals(gameId)).findFirst().ifPresent(resp -> resp.getOdds().add(oddsTypeResp));

                            }
                        }
                    } catch (Exception e) {
                        log.error("overUnder error", e);
                    }
                }
            }
        }

        return list;
    }

    private String getAuthorization() {
        String authorization = (String) redisRepository.get(PS_AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            return authorization;
        }
        String psAccount = settingService.getValueByType(PS_ACCOUNT);
        if (StringUtils.isBlank(psAccount)) {
            return null;
        }
        JSONObject psAccountJson = JSONObject.parseObject(psAccount);
        String username = psAccountJson.getString("username");
        String password = psAccountJson.getString("password");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }

        String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        authorization = "Basic " + basicAuth;
        redisRepository.setExpire(PS_AUTHORIZATION, authorization, FIVE_MINUTES);
        return authorization;
    }
}
