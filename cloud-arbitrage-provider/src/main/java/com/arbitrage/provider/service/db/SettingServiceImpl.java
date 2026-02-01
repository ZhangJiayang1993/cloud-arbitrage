package com.arbitrage.provider.service.db;

import com.arbitrage.api.model.db.GlobalSetting;
import com.arbitrage.api.service.SettingService;
import com.arbitrage.provider.dao.SettingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    SettingDao settingDao;

    @Override
    public List<GlobalSetting> list() {
        return settingDao.list();
    }

    @Override
    public String getValueByType(String type) {
        return settingDao.getSettingByType(type);
    }

    @Override
    public GlobalSetting getByType(String type) {
        return settingDao.getByType(type);
    }

    @Override
    public GlobalSetting updateByType(GlobalSetting setting) {
        settingDao.updateByType(setting.getType(), setting.getJsonField());
        return setting;
    }
}
