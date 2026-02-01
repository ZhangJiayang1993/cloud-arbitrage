package com.arbitrage.api.service;


import com.arbitrage.api.model.db.GlobalSetting;

import java.util.List;

public interface SettingService {

    List<GlobalSetting> list();

    String getValueByType(String type);

    GlobalSetting getByType(String type);

    GlobalSetting updateByType(GlobalSetting setting);

}
