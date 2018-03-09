package com.geotask.myapplication.DataClasses;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class GTData implements Serializable, Fileable{
    private String objectID = "";
    private transient Type type;

    protected void setType(Type type) {
        this.type = type;
    }

    public Type getType(){
        return type;
    }

    public void setObjectID(String ID) {
        this.objectID = ID;
    }

    public String getObjectID() {
        return objectID;
    }

    public void saveFile(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(this.objectID, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);

            Gson gson = new Gson();
            gson.toJson(this, writer);
            writer.flush();

            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GTData readFile(String filename, Context context, Type type) {
        GTData data = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            Gson gson = new Gson();
            data = gson.fromJson(reader, type);
            data.setType(type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }
}
