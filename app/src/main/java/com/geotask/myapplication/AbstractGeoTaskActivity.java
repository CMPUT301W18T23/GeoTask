package com.geotask.myapplication;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * AbstractGeoTaskActivity
 *
 * This abstract class allows all of the child activities to share resources among each other.
 *
 */
public abstract class AbstractGeoTaskActivity extends AppCompatActivity{

    private static User currentUser;
    private static Task currentTask;
    private static List<Bid> bidList;
    private static ArrayList<Task> taskList;
    private static Account account;
    private static int viewMode = R.integer.MODE_INT_ALL;
    private static String searchKeywords;
    private static double searchRange = -1.0;
    private static String searchStatus;
    private static HashMap<String, Boolean> starHash;
    private static HashMap<String, Boolean> historyHash;
    private static Task lastClickedTask = null;
    private static ContentResolver syncResolver;
    private static Context context;
    private static User lastViewedUser;
    public static final int SET_TASK_LOCATION = 4;
    public static String locationString;
    public static Double user_locationX;
    public static Double user_locationY;
    public static LatLng user_location;

    private static FusedLocationProviderClient mFusedLocationClient; //for location grabbing

    private static String coordString; //current users location as a string


    public static User getLastViewedUser() {
        return lastViewedUser;
    }

    public static void setLastViewedUser(User lastViewedUser) {
        AbstractGeoTaskActivity.lastViewedUser = lastViewedUser;
    }
    /**
     * Method to retrieve the User object that the current user is using the app
     *
     * @return -  the current user using the app
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Method to set the new user who is using the app
     *
     * @param currentUser - new user who is using the app
     */
    public static void setCurrentUser(User currentUser) {
        AbstractGeoTaskActivity.currentUser = currentUser;
    }

    /**
     * Method to the get the previously set currentTask
     *
     * @return - the currentTask
     */
    public static Task getCurrentTask() {
        return currentTask;
    }

    /**
     * Method to set the currently viewed task
     *
     * @param currentTask - the new task being viewed
     */
    public static void setCurrentTask(Task currentTask) {
        AbstractGeoTaskActivity.currentTask = currentTask;
    }

    /**
     *  Method to get the previously set list of tasks displayed in the MenuActivity
     *
     * @return - the previously set taskList
     */
    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    /**
     * Method to set the new list of tasks to be displayed in the MenuActivity
     *
     * @param taskList - the new list of tasks to be displayed in the MenuActivity
     */
    public static void setTaskList(ArrayList<Task> taskList) {
        AbstractGeoTaskActivity.taskList = taskList;
    }

    /**
     * Method to get the bidList
     *
     * @return - the previoudly set bidList
     */
    public static List<Bid> getBidList() {
        return bidList;
    }

    /**
     * Method to return the bidList
     *
     * @param bidList - the set bidList
     */
    public static void setBidList(List<Bid> bidList) {
        AbstractGeoTaskActivity.bidList = bidList;
    }

    /**
     * Method to get the account
     *
     * @return - account set
     * //@see Michael Tang
     */
    public static Account getAccount() {
        return account;
    }

    /**
     * Method to set the account
     *
     * @param account - account to set
     * //@see Michael Tang
     */
    public static void setAccount(Account account) {
        AbstractGeoTaskActivity.account = account;
    }

    /**
     * Method to get the previously set viewMode
     *
     * @return - the previously set viewMode
     */
    public static int getViewMode() {
        return viewMode;
    }

    /**
     * Method to set the new viewMode
     *
     * @param viewMode - new viewMode
     */
    public static void setViewMode(int viewMode) {
        AbstractGeoTaskActivity.viewMode = viewMode;
    }

    /**
     * Method to get the searchKeywords
     *
     * @return - the previously entered keywords
     */
    public static String getSearchKeywords() {
        return searchKeywords;
    }

    /**
     * Method to set the previous searchKeywords
     *
     * @param searchKeywords - String of entered keywords
     */
    public static void setSearchKeywords(String searchKeywords) {
        AbstractGeoTaskActivity.searchKeywords = searchKeywords;
    }

    /**
     * Method to return the searchRange
     *
     * @return - the previously set searchRange
     */
    public static double getSearchRange() {
        return searchRange;
    }

    /**
     * Sets the search range
     *
     * @param searchRange - radius
     */
    public static void setSearchRange(double searchRange) {
        AbstractGeoTaskActivity.searchRange = searchRange;
    }

    /**
     * Method to load the local hash map
     */
    public static void setHistoryHash(){
        historyHash = new HashMap<String, Boolean>();
        for(String taskID : getCurrentUser().getHistoryList()){
            historyHash.put(taskID, true);
        }
    }

    /**
     * gets the HishoryHashMap
     */
    public static HashMap<String, Boolean> getHistoryHash(){
        return historyHash;
    }

    /**
     * Method to check if the currentUser has viewed the task
     *
     * @param taskID
     * @return
     */
    public static Boolean userViewed(String taskID){
        if(historyHash.containsKey(taskID)){
            return true;
        }
        addToHistoryHash(taskID);
        return false;
    }

    /**
     * adds to the HistoryHash map
     *
     * @param taskID - ID of the task to add
     */
    public static void addToHistoryHash(String taskID){
        if(!historyHash.containsKey(taskID)) {
            historyHash.put(taskID, true);
        }
    }

    /**
     * removes a taskID from the HistoryHash map
     *
     * @param taskID - ID of the task to remove
     */
    public static void removeFromHistoryHash(String taskID){
        if(historyHash.containsKey(taskID)) {
            historyHash.remove(taskID);
        }
    }

    /**
     * Saves the local History to the server
     */
    public static void saveHistoryHashToServer(){
        ArrayList<String> newHistoryList = new ArrayList<String>();
        for(String taskID : historyHash.keySet()){
            newHistoryList.add(taskID);
        }

        getCurrentUser().setHistoryList(newHistoryList);

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument(context);
        asyncUpdateDocument.execute(getCurrentUser());
    }

    /**
     * Sets the local hashtable
     */
    public static void setStarHash(){
        starHash = new HashMap<String, Boolean>();
        for(String taskID : getCurrentUser().getStarredList()){
            starHash.put(taskID, true);
        }
    }

    /**
     * gets the StarHashMap
     */
    public static HashMap<String, Boolean> getStarHash(){
        return starHash;
    }

    /**
     * toggles the star
     *
     * @param taskID - ID of task to toggle
     */
    public static void toggleStar(String taskID){
        if(userStarred(taskID)){
            removeFromStarHash(taskID);
            return;
        }
        addToStarHash(taskID);
    }

    /**
     * Method to check if the current user has starred the task
     *
     * @param taskID - taskID to chech if starred
     * @return - true if starred, false if not starred
     */
    public static Boolean userStarred(String taskID){
        if(starHash!= null && starHash.containsKey(taskID)){
            return true;
        }
        return false;
    }

    /**
     * remove a taskID from the starHash
     *
     * @param taskID - ID of the task to remove
     */
    public static void removeFromStarHash(String taskID){
        if(userStarred(taskID)) {
            starHash.remove(taskID);
        }
    }

    /**
     * Adds a taskID to the starHash
     *
     * @param taskID - ID of the task to add
     */
    public static void addToStarHash(String taskID){
        if(!userStarred(taskID)) {
            starHash.put(taskID, true);
        }
    }

    /**
     * Save the new list of starred tasks to the server
     */
    public static void saveStarHashToServer(){
        ArrayList<String> newStarList = new ArrayList<String>();
        for(String taskID : starHash.keySet()){
            newStarList.add(taskID);
        }

        getCurrentUser().setStarredList(newStarList);

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument(context);
        asyncUpdateDocument.execute(getCurrentUser());
    }


    /**
     * Checks if internet connection is available
     * @return Boolean network state
     * Taken from: https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     */
    public boolean networkIsAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected static void updateTaskMetaData(AsyncCallBackManager callback){
        /*
           Finding the lowest bid, and number of bids
        */

        //make the query
        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskID"});
        builder.addParameters(new String[] {getCurrentTask().getObjectID()});

        //perform the search
        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(callback, context);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> result = null;
        ArrayList<Bid> newBidList;

        try {
            //get the result
            result = (List<Bid>) asyncSearch.get();
            newBidList = new ArrayList<Bid>(result);
            Double lowest = -1.0;

            /*
                setting the lowestBid TextView by querying for lowest bid. Here we also set
                numBids while we are at it
             */
            if(newBidList.size() == 0){
                getCurrentTask().setStatusRequested();                                  //change the status
            } else  if(newBidList.size() == 1) {
                if(getCurrentTask().getStatus().toLowerCase().compareTo("requested") == 0){
                    getCurrentTask().setStatusBidded();
                }
                lowest = newBidList.get(0).getValue();
            } else {
                if(getCurrentTask().getStatus().toLowerCase().compareTo("requested") == 0){
                    getCurrentTask().setStatusBidded();
                }
                lowest = newBidList.get(0).getValue();
                for(Bid newBid : newBidList){                                     //iterate to find lowest
                    if(newBid.getValue() < lowest){
                        lowest = newBid.getValue();
                    }
                }
            }
            getCurrentTask().setLowestBid(lowest);
            getCurrentTask().setNumBids(newBidList.size());
            MasterController.AsyncUpdateDocument asyncUpdateDocument =  //update the status
                    new MasterController.AsyncUpdateDocument(context);
            asyncUpdateDocument.execute(getCurrentTask());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setLastClicked(Task task){
        lastClickedTask = task;
    }
    public static Task getLastClicked(){
        return lastClickedTask;
    }

    public static ContentResolver getSyncResolver() {
        return syncResolver;
    }

    public static void setSyncResolver(ContentResolver syncResolver) {
        AbstractGeoTaskActivity.syncResolver = syncResolver;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //set location client
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.context = getBaseContext();
    }

    public static String getSearchStatus() {
        return searchStatus;
    }

    public static void setSearchStatus(String searchStatus) {
        AbstractGeoTaskActivity.searchStatus = searchStatus;
    }

    /*
        Returns a string with the users last known latitude,longitude
     */
    public static String retrieveLocation(Activity currentActivity) {
        Log.e("testing", "retrieving location");
        if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("testing","permission already granted");
            //permission already granted, get the last location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(currentActivity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.e("testing", "onSuccess");

                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.e("testing", "loc is not null");
                                //set coordString to the correct location, formatted
                                StringBuilder SB = new StringBuilder();
                                SB.append(Double.toString(location.getLatitude()))
                                        .append(",").append(Double.toString((location.getLongitude())));
                                coordString = SB.toString();
                            } else {
                                Log.e("testing", "location was null, setting coord to \"null\"");
                                coordString =  "null";
                            }
                        }
                    });
        } else {
            Log.e("testing","asking for location");
            //permission not granted, ask for permission
            ActivityCompat.requestPermissions(currentActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);         //defining 1 to be the requestCode for accessing fine location

        }
        //end of permission check/request
        return coordString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("testing","permission granted after being asked for");
                    coordString = retrieveLocation(this);

                } else {
                    //permission was denied
                    Log.e("testing", "permission explicitly denied, setting coord to \"null\"");
                    coordString = "null";
                }
                return;
            }
        }
    }

    //http://ramsandroid4all.blogspot.ca/2014/09/converting-byte-array-to-bitmap-in.html
    public Bitmap ByteArrayToBitmap(byte[] byteArray)     {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    protected void syncTasksFromServer(AsyncCallBackManager callback){
        if(networkIsAvailable()) {
            //grab from server
            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            MasterController.AsyncSearchServer asyncSearchServer =
                    new MasterController.AsyncSearchServer(callback);
            asyncSearchServer.execute(new AsyncArgumentWrapper(builder, Task.class));

            //grab from local
            SQLQueryBuilder builder2 = new SQLQueryBuilder(Task.class);
            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(callback, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder2, Task.class));

            try {
                ArrayList<Task> serverList = (ArrayList<Task>) asyncSearchServer.get();
                ArrayList<Task> localList = (ArrayList<Task>) asyncSearch.get();

                if((serverList == null) || (localList == null)){
                    return;
                }
                HashMap<Task, Task> localHash = new HashMap<Task, Task>();
                for(Task task : localList){
                    localHash.put(task, task);
                }
                for(Task task : serverList){
                    if(localHash.containsKey(task)){
                        String string1 = new Gson().toJson(task);
                        String string2 = new Gson().toJson(localHash.get(task));
                        //if the tasks are not the same
                        if(string1.compareTo(string2) != 0){
                            MasterController.AsyncUpdateLocalDocument asyncUpdateLocalDocument =
                                    new MasterController.AsyncUpdateLocalDocument(this);
                            asyncUpdateLocalDocument.execute(task);
                        }
                    } else {
                        MasterController.AsyncCreateNewLocalDocument asyncCreateNewLocalDocument =
                                new MasterController.AsyncCreateNewLocalDocument(this);
                        asyncCreateNewLocalDocument.execute(task);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    protected void syncBidsFromServer(AsyncCallBackManager callback){
        if(networkIsAvailable()) {
            //grab from server
            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            MasterController.AsyncSearchServer asyncSearchServer =
                    new MasterController.AsyncSearchServer(callback);
            asyncSearchServer.execute(new AsyncArgumentWrapper(builder, Bid.class));

            //grab from local
            SQLQueryBuilder builder2 = new SQLQueryBuilder(Bid.class);
            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(callback, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder2, Bid.class));

            try {
                ArrayList<Bid> serverList = (ArrayList<Bid>) asyncSearchServer.get();
                ArrayList<Bid> localList = (ArrayList<Bid>) asyncSearch.get();

                if((serverList == null) || (localList == null)){
                    return;
                }
                Integer test1 = serverList.size();
                Integer test2 = localList.size();
                Log.i("Sizes", String.format("%d %d", test1, test2));
                HashMap<Bid, Bid> localHash = new HashMap<Bid, Bid>();
                for(Bid Bid : localList){
                    localHash.put(Bid, Bid);
                }
                for(Bid Bid : serverList){
                    if(localHash.containsKey(Bid)){
                        String string1 = new Gson().toJson(Bid);
                        String string2 = new Gson().toJson(localHash.get(Bid));
                        //if the Bids are not the same
                        if(string1.compareTo(string2) != 0){
                            MasterController.AsyncUpdateLocalDocument asyncUpdateLocalDocument =
                                    new MasterController.AsyncUpdateLocalDocument(this);
                            asyncUpdateLocalDocument.execute(Bid);
                        }
                    } else {
                        MasterController.AsyncCreateNewLocalDocument asyncCreateNewLocalDocument =
                                new MasterController.AsyncCreateNewLocalDocument(this);
                        asyncCreateNewLocalDocument.execute(Bid);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    protected void syncUsersFromServer(AsyncCallBackManager callback){
        if(networkIsAvailable()) {
            //grab from server
            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            MasterController.AsyncSearchServer asyncSearchServer =
                    new MasterController.AsyncSearchServer(callback);
            asyncSearchServer.execute(new AsyncArgumentWrapper(builder, User.class));

            //grab from local
            SQLQueryBuilder builder2 = new SQLQueryBuilder(User.class);
            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(callback, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder2, User.class));

            try {
                ArrayList<User> serverList = (ArrayList<User>) asyncSearchServer.get();
                ArrayList<User> localList = (ArrayList<User>) asyncSearch.get();

                if((serverList == null) || (localList == null)){
                    return;
                }
                HashMap<User, User> localHash = new HashMap<User, User>();
                for(User User : localList){
                    localHash.put(User, User);
                }
                for(User User : serverList){
                    if(localHash.containsKey(User)){
                        String string1 = new Gson().toJson(User);
                        String string2 = new Gson().toJson(localHash.get(User));
                        //if the Users are not the same
                        if(string1.compareTo(string2) != 0){
                            MasterController.AsyncUpdateLocalDocument asyncUpdateLocalDocument =
                                    new MasterController.AsyncUpdateLocalDocument(this);
                            asyncUpdateLocalDocument.execute(User);
                        }
                    } else {
                        MasterController.AsyncCreateNewLocalDocument asyncCreateNewLocalDocument =
                                new MasterController.AsyncCreateNewLocalDocument(this);
                        asyncCreateNewLocalDocument.execute(User);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
