package com.arbitrage.scheduler.timer;

import com.arbitrage.api.constants.RedisConstants;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.service.CollectionService;
import com.arbitrage.common.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GameDataScheduler {

    @Autowired
    CollectionService psCollectionService;

    @Autowired
    RedisRepository redisRepository;

    @Scheduled(fixedDelay = 2000)
    public void collectPsData() {
        List<CollectionGameDataResp> psList = psCollectionService.collection();
        redisRepository.setExpire(RedisConstants.PS_GAME_DATA, psList, RedisConstants.TEN_SECONDS);
    }

}
