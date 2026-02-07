package com.arbitrage.provider.service.db;

import com.alibaba.fastjson.JSONObject;
import com.arbitrage.api.model.db.ComparisonTable;
import com.arbitrage.api.service.ComparisonTableService;
import com.arbitrage.common.redis.RedisRepository;
import com.arbitrage.provider.dao.ComparisonTableDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.arbitrage.api.constants.RedisConstants.COMPARISON_TABLE_KEY;

@Slf4j
@Service
public class ComparisonTableServiceImpl implements ComparisonTableService {

    final ComparisonTableDao comparisonTableDao;

    final RedisRepository redisRepository;

    public ComparisonTableServiceImpl(ComparisonTableDao comparisonTableDao, RedisRepository redisRepository) {
        this.comparisonTableDao = comparisonTableDao;
        this.redisRepository = redisRepository;
    }

    @Override
    public List<ComparisonTable> getComparisonTableByType(String type) {
        String key = String.format(COMPARISON_TABLE_KEY, type);
        String comparisonTableStr = (String) redisRepository.get(key);
        if (StringUtils.isNotBlank(comparisonTableStr)) {
            return JSONObject.parseArray(comparisonTableStr, ComparisonTable.class);
        }
        List<ComparisonTable> list = comparisonTableDao.getComparisonTableByType(type);
        if (list != null && !list.isEmpty()) {
            redisRepository.setExpire(key, JSONObject.toJSONString(list), 60 * 60);
        }
        return list;
    }
}
