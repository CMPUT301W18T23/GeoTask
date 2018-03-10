package com.geotask.myapplication.Controllers;

//http://corochann.com/asynctask-implementation-framework-to-be-independent-from-activity-460.html#C_Register_callback_to_invoke_Activity_method

import com.geotask.myapplication.DataClasses.GTData;

import java.util.List;

public interface AsyncCallBackManager {

    void onPostExecute(GTData data);
    void onPostExecute(List<? extends GTData> searchResult);
}
