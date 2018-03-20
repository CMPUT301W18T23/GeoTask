package com.geotask.myapplication.Controllers.Helpers;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import com.geotask.myapplication.DataClasses.Bid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//https://stackoverflow.com/questions/44580702/android-room-persistent-library-how-to-insert-class-that-has-a-list-object-fie

/**
 * Used by SQL operations to store bidList field in Task.
 * DO not call explicitly.
 */
public class BidListConverter {

    @TypeConverter
    public static String ListToJson(ArrayList<String> bidList) {
        Gson gson = new Gson();
        String json = gson.toJson(bidList);
        return json;
    }

    @TypeConverter
    public static ArrayList<String> JsonToList(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>(){}.getType();
        ArrayList<String> bidList = gson.fromJson(jsonString, type);
        return bidList;
    }
}
