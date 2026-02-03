package com.arbitrage.scheduler.timer;

import com.arbitrage.api.constants.RedisConstants;
import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.service.CollectionService;
import com.arbitrage.common.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GameDataScheduler {

    private final CollectionService psCollectionService;

    private final CollectionService polymarketCollectionService;

    private final RedisRepository redisRepository;

    public GameDataScheduler(@Qualifier("PSCollectionService") CollectionService psCollectionService,
                             @Qualifier("PolymarketCollectionService") CollectionService polymarketCollectionService,
                             RedisRepository redisRepository) {
        this.psCollectionService = psCollectionService;
        this.polymarketCollectionService = polymarketCollectionService;
        this.redisRepository = redisRepository;
    }

    @Scheduled(fixedDelay = 2000)
    public void collectPsData() {
        List<CollectionGameDataResp> psList = psCollectionService.collection();
        redisRepository.setExpire(RedisConstants.PS_GAME_DATA, psList, RedisConstants.TEN_SECONDS);
    }

     @Scheduled(fixedDelay = 2000)
    public void collectPolymarketData() {
        List<CollectionGameDataResp> polymarketList = polymarketCollectionService.collection();
        redisRepository.setExpire(RedisConstants.POLYMARKET_GAME_DATA, polymarketList, RedisConstants.TEN_SECONDS);
    }

}
