package com.arbitrage.api.service;

import com.arbitrage.api.model.collection.CollectionGameDataResp;
import com.arbitrage.api.model.order.ArbitrageData;

import java.util.List;

public interface CalculatorTwoWayArbitrageService {

    List<ArbitrageData> calculate(List<CollectionGameDataResp> all);
}
