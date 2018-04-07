package com.geotask.myapplication;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

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
    private static double searchRange;
    private static String searchStatus;
    private static HashMap<String, Boolean> starHash;
    private static HashMap<String, Boolean> historyHash;
    private static Task lastClickedTask = null;
    private static ContentResolver syncResolver;
    private static Context context;
    private static User lastViewedUser;



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


}
