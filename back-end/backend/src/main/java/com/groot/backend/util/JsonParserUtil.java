package com.groot.backend.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonParserUtil {

    /**
     * parse json of plant net API
     * @param json : string
     * @return top 5 plant name and score(as String)
     */
    public static String[][] plantNameParser(String json) {
        String[][] ret = new String[5][2];
        JsonElement jsonElement = JsonParser.parseString(json);

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray results = jsonObject.get("results").getAsJsonArray();

        for(int i=0; i<5; i++) {
            String score = results.get(i).getAsJsonObject()
                    .get("score").getAsString();
            String sciName = results.get(i).getAsJsonObject()
                    .get("species").getAsJsonObject()
                    .get("scientificNameWithoutAuthor").getAsString();
            ret[i][0] = sciName;
            ret[i][1] = score;
        }

        return ret;
    }
}
