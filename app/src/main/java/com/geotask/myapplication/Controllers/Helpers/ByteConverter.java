package com.geotask.myapplication.Controllers.Helpers;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class ByteConverter {
    @TypeConverter
    public static String ListToJson(List<byte[]> byteArray) {
        Gson gson = new Gson();
        String json = gson.toJson(byteArray);
        return json;
    }

    @TypeConverter
    public static List<byte[]> JsonToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<byte[]>>(){}.getType();
        List<byte[]> byteArray = gson.fromJson(json, type);
        return byteArray;
    }
}
