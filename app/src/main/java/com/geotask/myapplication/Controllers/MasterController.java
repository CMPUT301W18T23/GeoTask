package com.geotask.myapplication.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.io.IOException;
import java.util.List;

/**
 * All data management goes through here. This controller will keep local SQL database synced to the server.
 * Contains ElasticsearchController and 3 DatabaseController.
 * Do not call ElasticSearchController and DatabaseControllers explicitly, use this instead.
 */
//ToDo add sync logic between local and server, ToDo JobScheduler
public class MasterController {

    private static ElasticsearchController controller = new ElasticsearchController();
    private static LocalDataBase database;

    public static void verifySettings(Context context) {
        controller.verifySettings();
        if(database == null) {
            database = LocalDataBase.getDatabase(context);
        }
    }

    public static void setTestSettings(String testSettings) {
        controller.verifySettings();
        controller.setTestSettings(testSettings);
    }

    /**
     * Do not use on production server
     * @throws IOException
     */
    public static void createIndex() throws IOException {
        controller.verifySettings();
        controller.createIndex();
    }

    /**
     * Do not use on production server
     * @throws IOException
     */
    public static void deleteIndex() throws IOException {
        controller.verifySettings();
        controller.deleteIndex();
    }

    public static void shutDown() {
        controller.verifySettings();
        controller.shutDown();
    }

    public static User existsProfile(String s) {
        controller.verifySettings();
        return controller.existsProfile(s);
    }

    /**
     * AsyncTask for putting document into server
     */
   public static class AsyncCreateNewDocument extends AsyncTask<GTData, Void, Void> {

        private Context context;

        public AsyncCreateNewDocument(Context context) {
            this.context = context;
        }
        /**
         *
         * @param dataList queue of GTData waiting for execution
         * @return assumes success, will not throw exceptions if failed
         */
        @Override
        protected Void doInBackground(GTData... dataList) {
            verifySettings(context);

            for(GTData data : dataList) {
                if (data instanceof Task){
                    database.taskDAO().insert((Task) data);
                } else if (data instanceof User) {
                    try {
                        controller.createNewDocument(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (data instanceof Bid) {
                    database.bidDAO().insert((Bid) data);
                }

                //ToDo JobSchedule
            }
            return null;
        }
    }

    /**
     * AsyncTask for getting a single document by ID
     */
    public static class AsyncGetDocument extends AsyncTask<AsyncArgumentWrapper, Void, GTData> {
        private AsyncCallBackManager callBack = null;
        private Context context;

        /**
         * contains the reference to the calling activity
         * @param callback
         */
        public AsyncGetDocument(AsyncCallBackManager callback, Context context) {
            this.callBack = callback;
            this.context = context;
        }

        /**
         *
         * @param argumentList
         * @return returns null if failed, otherwise returns GTData
         */
        @Override
        protected GTData doInBackground(AsyncArgumentWrapper... argumentList) {
            verifySettings(context);

            GTData result = null;

            for (AsyncArgumentWrapper argument : argumentList) {
                verifySettings(context);

                if (argument.getType().equals(Task.class)){
                    result = database.taskDAO().selectByID(argument.getID());
                } else if (argument.getType().equals(User.class)) {
                    try {
                        result = controller.getDocument(argument.getID(), argument.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (argument.getType().equals(Bid.class)) {
                    result = database.bidDAO().selectByID(argument.getID());
                }
            }
            return result;
        }

        /**
         * returns GTData to main thread
         * @param data
         */
        @Override
        protected void onPostExecute(GTData data) {
            if(callBack != null) {
                callBack.onPostExecute(data);
            }
        }
    }

    /**
     * deletes a single document by ID
     */
    public static class AsyncDeleteDocument extends  AsyncTask<AsyncArgumentWrapper, Void, Void> {

        private Context context;

        public AsyncDeleteDocument(Context context){
            this.context = context;
        }

        /**
         *
         * @param argumentList
         * @return nothing, assumes success
         */
        @Override
        protected Void doInBackground(AsyncArgumentWrapper... argumentList) {
            verifySettings(context);

            for(AsyncArgumentWrapper argument : argumentList) {
                if (argument.getType().equals(Task.class)){
                    database.taskDAO().deleteByID(argument.getID());
                } else if (argument.getType().equals(User.class)) {
                    try {
                        controller.deleteDocument(argument.getID(), argument.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (argument.getType().equals(Bid.class)) {
                    database.bidDAO().deleteByID(argument.getID());
                }

                //ToDo JobScheduler
                try {
                    controller.deleteDocument(argument.getID(), argument.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static class AsyncDeleteBidsByTaskID extends AsyncTask<AsyncArgumentWrapper, Void, Void> {

        private Context context;

        public AsyncDeleteBidsByTaskID (Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(AsyncArgumentWrapper... argumentList) {
            verifySettings(context);

            for(AsyncArgumentWrapper argument : argumentList) {
                database.bidDAO().deleteByTaskID(argument.getID());

                //ToDo JobScheduler
                try {
                    controller.deleteDocumentByValue(argument.getID(), argument.getType());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * update in the best way. delete then re-add. has no guards against concurrency issues
     * also returns nothing, assumes success
     */
    public static class AsyncUpdateDocument extends AsyncTask<GTData, Void, Void> {


        private Context context;

        public AsyncUpdateDocument(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(GTData... dataList) {
            verifySettings(context);

            for(GTData data: dataList) {
                data.setClientOriginalFlag(true);
                if (data instanceof Task){
                    database.taskDAO().update((Task) data);
                } else if (data instanceof User) {
                    database.userDAO().update((User) data);
                } else if (data instanceof Bid) {
                    database.bidDAO().update((Bid) data);
                }

                //ToDo JobScheduler
//                try {
//                    controller.updateDocument(data);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
            return null;
        }
    }

    /**
     * synchonized search on main thread. cheaper than calling get() on AsyncTask, might crash app if not used properly
     * @param argumentWrapper
     * @return
     * @throws IOException
     */
    public static List<? extends GTData> Search(AsyncArgumentWrapper argumentWrapper) throws IOException {
        return controller.search(argumentWrapper.getSearchQuery(), argumentWrapper.getType());
    }

    /**
     * AsyncTask for search, returns result through callBack
     */
    public static class AsyncSearch extends AsyncTask<AsyncArgumentWrapper, Void, List<? extends GTData>> {
        private Context context;
        private AsyncCallBackManager callBack = null;

        public AsyncSearch(AsyncCallBackManager callback, Context context) {
            this.callBack = callback;
            this.context = context;
        }

        @Override
        protected List<? extends GTData> doInBackground(AsyncArgumentWrapper... argumentWrappers) {
            List<? extends GTData> resultList = null;
            verifySettings(context);

            for(AsyncArgumentWrapper argument : argumentWrappers) {
                Log.d("BUGSBUGSBUGS", String.valueOf(argument.getSQLQuery()));
                if (argument.getType().equals(Task.class)){
                    resultList = database.taskDAO().searchTasksByQuery(argument.getSQLQuery());
                } else if (argument.getType().equals(Bid.class)) {
                    resultList = database.bidDAO().searchBidsByQuery(argument.getSQLQuery());
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<? extends GTData> dataList) {
            if(callBack != null) {
                callBack.onPostExecute(dataList);
            }
        }
    }

    public static List<? extends GTData> slowSearch(AsyncArgumentWrapper argument){
        controller.verifySettings();
        List<? extends GTData> resultList = null;
        try {
            resultList = controller.search(argument.getSearchQuery(), argument.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }

}
