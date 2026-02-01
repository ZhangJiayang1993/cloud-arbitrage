package com.arbitrage.api.model.db;

import lombok.Data;

import java.util.Date;

@Data
public class GlobalSetting {
    private String type;
    private String name;
    private String jsonField;
    private Date updateTime;
}
