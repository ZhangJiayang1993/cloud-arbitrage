package com.arbitrage.api.service;


import com.arbitrage.api.model.db.ComparisonTable;

import java.util.List;

public interface ComparisonTableService {

    List<ComparisonTable> getComparisonTableByType(String type);

}
