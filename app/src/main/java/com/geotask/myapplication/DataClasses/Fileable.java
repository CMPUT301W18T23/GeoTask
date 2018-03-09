package com.geotask.myapplication.DataClasses;

import android.content.Context;

import java.lang.reflect.Type;

interface Fileable {
    void writeFile(Context context);
    GTData readFile(String filename, Context context, Type type);
}
