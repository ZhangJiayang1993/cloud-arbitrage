package com.arbitrage.provider.dao;

import com.arbitrage.api.model.db.GlobalSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SettingDao {

    @Select("SELECT json_field FROM setting WHERE type = #{type} LIMIT 1")
    String getSettingByType(String type);

    @Select("SELECT * FROM setting ORDER BY type")
    List<GlobalSetting> list();

    @Select("SELECT * FROM setting WHERE type = #{type}")
    GlobalSetting getByType(@Param("type") String type);

    @Update("UPDATE setting SET json_field = #{jsonField}::jsonb, update_time = NOW() WHERE type = #{type}")
    void updateByType(@Param("type") String type, @Param("jsonField") String jsonField);

}
