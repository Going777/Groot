package com.groot.backend.config;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * JSON Serializer for swagger-ui
 * @author kmw
 */
public class SwaggerJsonSerializer implements JsonSerializer<String> {

    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        if(src.contains("openapi")) {
            // parse \
            return JsonParser.parseString(src.replace("\\", ""));
        }
        else {
            return new Gson().toJsonTree(src, typeOfSrc);
        }
    }
}
