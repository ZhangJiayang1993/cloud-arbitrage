package com.arbitrage.provider.dao;

import com.arbitrage.api.model.db.ComparisonTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
@Component
public interface ComparisonTableDao {

    @Select("SELECT * FROM comparison_table WHERE type = #{type}")
    List<ComparisonTable> getComparisonTableByType(String type);
}
