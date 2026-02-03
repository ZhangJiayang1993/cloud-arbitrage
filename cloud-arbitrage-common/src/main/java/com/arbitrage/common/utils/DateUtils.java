package com.arbitrage.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String getAfterDate(Integer days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String testData = "[\n" +
            "  {\n" +
            "    \"id\": 21,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"华盛顿奇才队\",\n" +
            "    \"en_name\": \"Wizards\",\n" +
            "    \"polymarket_name\": \"Wizards\",\n" +
            "    \"ps_name\": \"Washington Wizards\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 24,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"俄克拉荷马城雷霆队\",\n" +
            "    \"en_name\": \"Thunder\",\n" +
            "    \"polymarket_name\": \"Thunder\",\n" +
            "    \"ps_name\": \"Oklahoma City Thunder\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 17,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"新奥尔良鹈鹕队\",\n" +
            "    \"en_name\": \"Pelicans\",\n" +
            "    \"polymarket_name\": \"Pelicans\",\n" +
            "    \"ps_name\": \"New Orleans Pelicans\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 14,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"波特兰开拓者队\",\n" +
            "    \"en_name\": \"Trail Blazers\",\n" +
            "    \"polymarket_name\": \"Trail Blazers\",\n" +
            "    \"ps_name\": \"Portland Trail Blazers\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 23,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"孟菲斯灰熊队\",\n" +
            "    \"en_name\": \"Grizzlies\",\n" +
            "    \"polymarket_name\": \"Grizzlies\",\n" +
            "    \"ps_name\": \"Memphis Grizzlies\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 1,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"夏洛特黄蜂队\",\n" +
            "    \"en_name\": \"Hornets\",\n" +
            "    \"polymarket_name\": \"Hornets\",\n" +
            "    \"ps_name\": \"Charlotte Hornets\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 5,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"亚特兰大老鹰队\",\n" +
            "    \"en_name\": \"Hawks\",\n" +
            "    \"polymarket_name\": \"Hawks\",\n" +
            "    \"ps_name\": \"Atlanta Hawks\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 16,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"金州勇士队\",\n" +
            "    \"en_name\": \"Warriors\",\n" +
            "    \"polymarket_name\": \"Warriors\",\n" +
            "    \"ps_name\": \"Golden State Warriors\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 11,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"菲尼克斯太阳队\",\n" +
            "    \"en_name\": \"Suns\",\n" +
            "    \"polymarket_name\": \"Suns\",\n" +
            "    \"ps_name\": \"Phoenix Suns\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 27,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"芝加哥公牛队\",\n" +
            "    \"en_name\": \"Bulls\",\n" +
            "    \"polymarket_name\": \"Bulls\",\n" +
            "    \"ps_name\": \"Chicago Bulls\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 10,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"密尔沃基雄鹿队\",\n" +
            "    \"en_name\": \"Bucks\",\n" +
            "    \"polymarket_name\": \"Bucks\",\n" +
            "    \"ps_name\": \"Milwaukee Bucks\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 13,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"明尼苏达森林狼队\",\n" +
            "    \"en_name\": \"Timberwolves\",\n" +
            "    \"polymarket_name\": \"Timberwolves\",\n" +
            "    \"ps_name\": \"Minnesota Timberwolves\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 4,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"底特律活塞队\",\n" +
            "    \"en_name\": \"Pistons\",\n" +
            "    \"polymarket_name\": \"Pistons\",\n" +
            "    \"ps_name\": \"Detroit Pistons\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 2,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"奥兰多魔术队\",\n" +
            "    \"en_name\": \"Magic\",\n" +
            "    \"polymarket_name\": \"Magic\",\n" +
            "    \"ps_name\": \"Orlando Magic\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 7,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"纽约尼克斯队\",\n" +
            "    \"en_name\": \"Knicks\",\n" +
            "    \"polymarket_name\": \"Knicks\",\n" +
            "    \"ps_name\": \"New York Knicks\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 18,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"洛杉矶湖人队\",\n" +
            "    \"en_name\": \"Lakers\",\n" +
            "    \"polymarket_name\": \"Lakers\",\n" +
            "    \"ps_name\": \"Los Angeles Lakers\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 12,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"犹他爵士队\",\n" +
            "    \"en_name\": \"Jazz\",\n" +
            "    \"polymarket_name\": \"Jazz\",\n" +
            "    \"ps_name\": \"Utah Jazz\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 25,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"休斯敦火箭队\",\n" +
            "    \"en_name\": \"Rockets\",\n" +
            "    \"polymarket_name\": \"Rockets\",\n" +
            "    \"ps_name\": \"Houston Rockets\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 9,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"多伦多猛龙队\",\n" +
            "    \"en_name\": \"Raptors\",\n" +
            "    \"polymarket_name\": \"Raptors\",\n" +
            "    \"ps_name\": \"Toronto Raptors\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 26,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"布鲁克林篮网队\",\n" +
            "    \"en_name\": \"Nets\",\n" +
            "    \"polymarket_name\": \"Nets\",\n" +
            "    \"ps_name\": \"Brooklyn Nets\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 19,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"萨克拉门托国王队\",\n" +
            "    \"en_name\": \"Kings\",\n" +
            "    \"polymarket_name\": \"Kings\",\n" +
            "    \"ps_name\": \"Sacramento Kings\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 6,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"波士顿凯尔特人队\",\n" +
            "    \"en_name\": \"Celtics\",\n" +
            "    \"polymarket_name\": \"Celtics\",\n" +
            "    \"ps_name\": \"Boston Celtics\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 29,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"洛杉矶快船队\",\n" +
            "    \"en_name\": \"Clippers\",\n" +
            "    \"polymarket_name\": \"Clippers\",\n" +
            "    \"ps_name\": \"Los Angeles Clippers\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 8,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"费城76人队\",\n" +
            "    \"en_name\": \"76ers\",\n" +
            "    \"polymarket_name\": \"76ers\",\n" +
            "    \"ps_name\": \"Philadelphia 76ers\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 28,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"印第安纳步行者队\",\n" +
            "    \"en_name\": \"Pacers\",\n" +
            "    \"polymarket_name\": \"Pacers\",\n" +
            "    \"ps_name\": \"Indiana Pacers\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 30,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"克利夫兰骑士队\",\n" +
            "    \"en_name\": \"Cavaliers\",\n" +
            "    \"polymarket_name\": \"Cavaliers\",\n" +
            "    \"ps_name\": \"Cleveland Cavaliers\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 15,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"达拉斯独行侠队\",\n" +
            "    \"en_name\": \"Mavericks\",\n" +
            "    \"polymarket_name\": \"Mavericks\",\n" +
            "    \"ps_name\": \"Dallas Mavericks\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 3,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"迈阿密热火队\",\n" +
            "    \"en_name\": \"Heat\",\n" +
            "    \"polymarket_name\": \"Heat\",\n" +
            "    \"ps_name\": \"Miami Heat\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 20,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"丹佛掘金队\",\n" +
            "    \"en_name\": \"Nuggets\",\n" +
            "    \"polymarket_name\": \"Nuggets\",\n" +
            "    \"ps_name\": \"Denver Nuggets\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 22,\n" +
            "    \"type\": \"NBA_TEAM\",\n" +
            "    \"cn_name\": \"圣安东尼奥马刺队\",\n" +
            "    \"en_name\": \"Spurs\",\n" +
            "    \"polymarket_name\": \"Spurs\",\n" +
            "    \"ps_name\": \"San Antonio Spurs\"\n" +
            "  }\n" +
            "]";
}
