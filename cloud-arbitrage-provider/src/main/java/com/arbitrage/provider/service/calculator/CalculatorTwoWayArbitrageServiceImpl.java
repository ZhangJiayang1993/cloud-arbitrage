package com.arbitrage.provider.service.calculator;

import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.enums.OddsTypeEnum;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.model.order.ArbitrageData;
import com.arbitrage.api.service.CalculatorTwoWayArbitrageService;
import com.arbitrage.api.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CalculatorTwoWayArbitrageServiceImpl implements CalculatorTwoWayArbitrageService {

    private final ExchangeRateService exchangeRateService;

    public CalculatorTwoWayArbitrageServiceImpl(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * 二向计算器
     */
    @Override
    public List<ArbitrageData> calculate(List<CollectionGameDataResp> all) {
        BigDecimal usdExchangeRate = exchangeRateService.getUsdExchangeRate();
        log.info("usdExchangeRate:{}", usdExchangeRate);
        // 实现二向计算逻辑
        List<ArbitrageData> arbitrageDataList = new ArrayList<>();
        for (CollectionGameDataResp gameData1 : all) {
            for (CollectionGameDataResp gameData2 : all) {
                if (gameData1.getLeagueId() != null && gameData1.getLeagueId().equals(gameData2.getLeagueId()) && gameData1.getHomeTeamId() != null && gameData1.getHomeTeamId().equals(gameData2.getHomeTeamId()) && gameData1.getAwayTeamId() != null && gameData1.getAwayTeamId().equals(gameData2.getAwayTeamId())) {
                    // 同一场比赛
                    // 遍历盘口类型
                    for (CollectionGameDataResp.OddsTypeResp oddsType1 : gameData1.getOdds()) {
                        for (CollectionGameDataResp.OddsTypeResp oddsType2 : gameData2.getOdds()) {
                            OddsTypeEnum oddsTypeEnum1 = OddsTypeEnum.getByOddsTypeId(oddsType1.getOddsTypeId());
                            OddsTypeEnum oddsTypeEnum2 = OddsTypeEnum.getByOddsTypeId(oddsType2.getOddsTypeId());

                            if (oddsTypeEnum1 == oddsTypeEnum2 && Objects.equals(oddsType1.getOddsData(), oddsType2.getOddsData())) {
                                // 同一种盘口类型
                                BigDecimal odds1;
                                BigDecimal odds2;

                                if (oddsTypeEnum1 == OddsTypeEnum.MONEY_LINE || oddsTypeEnum1 == OddsTypeEnum.HANDICAP) {
                                    odds1 = oddsType1.getHomeOdds();
                                    odds2 = oddsType2.getAwayOdds();
                                } else if (oddsTypeEnum1 == OddsTypeEnum.OVER_UNDER) {
                                    odds1 = oddsType1.getOverOdds();
                                    odds2 = oddsType2.getUnderOdds();
                                } else {
                                    continue;
                                }

                                BigDecimal arbitrageOpportunity = calculateArbitrageFactor(odds1, odds2);
                                if (hasArbitrageOpportunity(arbitrageOpportunity)) {
                                    log.info("二向套利机会: {} {} {} {} {} {} {}", gameData1.getPlatform(), gameData2.getPlatform(), gameData1.getTitle(), odds1, odds2, arbitrageOpportunity, oddsTypeEnum1);

                                    // ============================ 合并扩展信息
                                    JSONObject homeExtendInfo = new JSONObject();
                                    JSONObject awayExtendInfo = new JSONObject();
                                    JSONObject drawExtendInfo = new JSONObject();

                                    homeExtendInfo.putAll(gameData1.getExtendInfo());
                                    homeExtendInfo.putAll(oddsType1.getExtendInfo());

                                    awayExtendInfo.putAll(gameData2.getExtendInfo());
                                    awayExtendInfo.putAll(oddsType2.getExtendInfo());
                                    // ============================

                                    JSONObject homeOrderInfo = new JSONObject(); // 主队下单信息
                                    JSONObject awayOrderInfo = new JSONObject(); // 可对下单信息
                                    JSONObject drawOrderInfo = new JSONObject(); // 平局下单信息

                                    String enTitle = gameData1.getHomeTeamName() + " vs " + gameData1.getAwayTeamName();
                                    String cnTitle = gameData1.getHomeTeamCnName() + " vs " + gameData1.getAwayTeamCnName();

                                    ArbitrageData arbitrageData = new ArbitrageData(gameData1.getLeagueId(), gameData1.getLeague(),
                                            enTitle, cnTitle, gameData1.getPlatformId(), gameData2.getPlatformId(), gameData1.getPlatform(), gameData2.getPlatform(),
                                            gameData1.getHomeTeamId(), gameData2.getAwayTeamId(), gameData1.getHomeTeamName(), gameData2.getAwayTeamName(),
                                            gameData1.getHomeTeamCnName(), gameData2.getAwayTeamCnName(), gameData1.getRebate(), gameData2.getRebate(),
                                            homeExtendInfo, awayExtendInfo, drawExtendInfo, oddsType1.getOddsTypeId(), oddsType1.getOddsType(), oddsType1.getOddsData(),
                                            oddsType1.getHomeOdds(), null, gameData1.getCurrencyTypeEnum(), null,
                                            oddsType2.getAwayOdds(), null, gameData2.getCurrencyTypeEnum(), null,
                                            null, null, null, null,
                                            oddsType1.getOverOdds(), null, gameData2.getCurrencyTypeEnum(), null,
                                            oddsType2.getUnderOdds(), null, gameData2.getCurrencyTypeEnum(), null,
                                            homeOrderInfo, awayOrderInfo, drawOrderInfo, arbitrageOpportunity);
                                    arbitrageDataList.add(arbitrageData);
                                }
                            }
                        }
                    }
                }
            }
        }
        return arbitrageDataList;
    }

    /**
     * 二向计算器
     * 计算套利因子
     */
    public static BigDecimal calculateArbitrageFactor(BigDecimal odds1, BigDecimal odds2) {
        // 校验参数
        if (odds1 == null || odds2 == null) {
            return null;
        }
        // 计算套利因子
        return BigDecimal.ONE.divide(odds1, 4, RoundingMode.HALF_UP).add(BigDecimal.ONE.divide(odds2, 4, RoundingMode.HALF_UP));
    }

    /**
     * 是否有套利空间
     */
    public static boolean hasArbitrageOpportunity(BigDecimal arbitrageOpportunity) {
        if (arbitrageOpportunity == null) {
            return false;
        }
        return arbitrageOpportunity.compareTo(BigDecimal.ONE) < 0;
    }

}
