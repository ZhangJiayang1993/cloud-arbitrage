package com.arbitrage.provider.service.collection;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.service.ExchangeRateService;
import com.arbitrage.common.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.arbitrage.api.constants.RedisConstants.USD_EXCHANGE_RATE;

/**
 * 汇率服务实现类
 */
@Slf4j
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final RedisRepository redisRepository;

    private static final String url = "https://gushitong.baidu.com/opendata?openapi=1&dspName=iphone&tn=tangram&client=app&query=USDCNY&code=&word=&resource_id=5343&ma_ver=4&finClientType=pc";

    public ExchangeRateServiceImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }


    @Override
    public BigDecimal getUsdExchangeRate() {
        try {
            BigDecimal usdExchangeRate = (BigDecimal) redisRepository.get(USD_EXCHANGE_RATE);
            if (usdExchangeRate != null) {
                return usdExchangeRate;
            }
            String resultStr = HttpUtil.get(url);
            JSONObject result = JSONObject.parseObject(resultStr);
            JSONArray data = result.getJSONArray("Result").getJSONObject(0).getJSONObject("DisplayData").getJSONObject("resultData").getJSONObject("tplData").getJSONObject("result").getJSONObject("minute_data").getJSONArray("priceinfo_new");
            usdExchangeRate = data.getJSONObject(data.size() - 1).getBigDecimal("price");
            if (usdExchangeRate != null && usdExchangeRate.compareTo(BigDecimal.ZERO) > 0) {
                redisRepository.setExpire(USD_EXCHANGE_RATE, usdExchangeRate, 60 * 60);
                return usdExchangeRate;
            }
        } catch (Exception e) {
            log.error("获取美金汇率失败", e);
        }
        return null;
    }
}
