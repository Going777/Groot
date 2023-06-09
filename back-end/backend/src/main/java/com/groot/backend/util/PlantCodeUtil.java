package com.groot.backend.util;

import java.util.Map;
import static java.util.Map.entry;

public class PlantCodeUtil {

    /**
     * smell code : 1~4
     */
    public static String[] smellCode = {"", "강함", "중간", "약함", "거의 없음"};

    /**
     * management level code : 1 ~ 3
     */
    public static String[] mgmtLevelCode = {"", "초보자", "경험자", "전문가"};

    /**
     * management level to code
     */
    public static Map<String, Integer> mgmtLevel = Map.ofEntries(
            entry("쉬움", 1), entry("보통", 2), entry("어려움", 3)
    );

    /**
     * light demand string to number of bits to shift
     */
    public static Map<String, Integer> lightLevel = Map.ofEntries(
            entry("낮음", 0), entry("중간", 1), entry("높음", 2)
    );

    /**
     * amount of illuminance by light demand code 1~7
     */
    public static int[][] lightDemand = new int[][] {
            {0, 0}, {300, 800}, {800, 1500}, {300, 1500}, {1500, 10000},
            {300, 10000}, {800, 10000}, {300, 10000}
    };

    public static String[] waterCycleCode = {"", "항상 흙을 축축하게 유지함(물에 잠김)",
            "흙을 촉촉하게 유지함(물에 잠기지 않도록 주의)", "토양 표면이 말랐을때 충분히 관수함",
            "화분 흙 대부분 말랐을때 충분히 관수함"
    };

    public static int[][] waterPeriods = {{0, 0}, {2,4}, {5,10}, {15,25}, {30,40}};

    public static int[] waterCycle =  {0, 3, 7, 20, 35};

    /**
     * Character codes for grw types
     */
    public static Map<String, Integer> characterCode = Map.ofEntries(
            entry("군생형", 1), entry("다육형", 2), entry("로제트형", 3),
            entry("포도송이형", 4), entry("불규칙형", 5), entry("탑형", 6),
            entry("관목형",7), entry("직립형", 8), entry("포복형",9),
            entry("덩굴성", 10), entry("풀모양", 11), entry("", 12),
            entry("gone", 13)
    );

    /**
     * Character names
     */
    public static String[] characterName = {
            "", "군생형", "다육형", "로제트형", "포도송이형", "불규칙형", "탑형",
            "관목형", "직립형", "포복형", "덩굴성", "풀모양", "", "호엥"
    };

    /**
     * Succulent plants Except 다육형
     */
    public static String[] succulents = {"군생형", "로제트형", "포도송이형", "불규칙형", "탑형"};

    /**
     * Returns single character
     * @param grwType : can contain multiple types, distinguished by comma
     * @return single character code
     */
    public static Long characterCode(String grwType) {

        String type = grwType.split(",")[0];

        return (long)characterCode.get(type);
    }

}
