package com.geotask.myapplication.Controllers.Helpers;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * HashMapConverter
 *
 * This class is used by SQL if needed
 *
 * Created by Kyle on 2018-03-23.
 */
public class HashMapConverter {

    @TypeConverter
    public static String HashMapToJson(HashMap<String, Integer> hashMap) {
        Gson gson = new Gson();
        String json = gson.toJson(hashMap);
        return json;
    }

    @TypeConverter
    public static HashMap<String, Integer> JsonToList(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap<String, Integer> hashMap = gson.fromJson(jsonString, type);
        return hashMap;
    }
}
