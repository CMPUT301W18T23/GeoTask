package com.geotask.myapplication.Controllers;

//http://corochann.com/asynctask-implementation-framework-to-be-independent-from-activity-460.html#C_Register_callback_to_invoke_Activity_method

/**
 * Used by AsyncTask to pass returns into main thread
 * How to Use:
 *      activity must implement this interface
 *      controllers that passes out returns must be given "this" as an argument
 *      Override both function and store the results in activity accordingly
 *
 *      currently does not check if calling UI thread still exists. will crash if this happens. will fix this later
 */

import com.geotask.myapplication.DataClasses.GTData;

import java.util.List;

public interface AsyncCallBackManager {

    /**
     * returns single object
     * @param data the returned object
     */
    void onPostExecute(GTData data);

    /**
     * returns multiple objects
     * @param searchResult list of objects returned
     */
    void onPostExecute(List<? extends GTData> searchResult);
}
