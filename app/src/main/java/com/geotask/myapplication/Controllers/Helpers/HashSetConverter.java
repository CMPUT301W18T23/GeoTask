package com.geotask.myapplication.Controllers.Helpers;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * Created by Michael on 3/30/2018.
 */

public class HashSetConverter {
    @TypeConverter
    public static String ListToJson(HashSet<String> bidList) {
        Gson gson = new Gson();
        String json = gson.toJson(bidList);
        return json;
    }

    @TypeConverter
    public static HashSet<String> JsonToList(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashSet<String>>(){}.getType();
        HashSet<String> bidList = gson.fromJson(jsonString, type);
        return bidList;
    }
}


