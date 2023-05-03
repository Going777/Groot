package com.groot.backend.util;

public class PlantCodeUtil {

    /**
     * smell code : 1~4
     */
    public static String[] smellCode = {"", "강함", "중간", "약함", "거의 없음"};

    /**
     * management level code : 1 ~ 3
     */
    public static String[] mgmtLevelCode = {"", "초보자", "경험자", "전문가"};

    public static String[] waterCycleCode = {"", "항상 흙을 축축하게 유지함(물에 잠김)",
            "흙을 촉촉하게 유지함(물에 잠기지 않도록 주의)", "토양 표면이 말랐을때 충분히 관수함",
            "화분 흙 대부분 말랐을때 충분히 관수함"
    };

}
