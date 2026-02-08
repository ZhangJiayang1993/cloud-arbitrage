package com.arbitrage.api.enums;

import lombok.Getter;

@Getter
public enum OrderStateEnum {

    NO_BET_PLACED(0, "未下注"),
    NORMAL(1, "正常"),
    ASYMMETRIC_POSTING(2, "单边账 存在有一方未下注的情况"),
    PARTIAL(3, "部分撤单"),
    REVOKE(4, "已全部撤单"),
    ENDED(5, "已结束 全部正常下单之后， 并且已经同步了比赛结果")
    ;

    private final Integer orderState;
    private final String desc;

    OrderStateEnum(Integer orderState, String desc) {
        this.orderState = orderState;
        this.desc = desc;
    }
}
