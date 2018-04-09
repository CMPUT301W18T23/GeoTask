package com.geotask.myapplication.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Photo;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * All data management goes through here. This controller will keep local SQL database synced to the server.
 * Contains ElasticsearchController and 3 DatabaseController.
 * Do not call ElasticSearchController and DatabaseControllers explicitly, use this instead.
 */
public class MasterController {

    private static ElasticsearchController controller = new ElasticsearchController();
    private static LocalDataBase database;

    public static void verifySettings(Context context) {
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
        database.close();
    }

    public static User existsProfile(String s) {
        controller.verifySettings();
        return controller.existsProfile(s);
    }

    /**
     * AsyncTask for putting document into server
     */
    public static class AsyncCreateNewLocalDocument extends AsyncTask<GTData, Void, Void> {

        private Context context;

        public AsyncCreateNewLocalDocument(Context context) {
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
                    database.userDAO().insert((User) data);
                } else if (data instanceof Bid) {
                    database.bidDAO().insert((Bid) data);
                } else if (data instanceof Photo) {
                    database.photoDAO().insert((Photo) data);
                }
            }
            return null;
        }
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
                    database.userDAO().insert((User) data);
                } else if (data instanceof Bid) {
                    database.bidDAO().insert((Bid) data);
                } else if (data instanceof Photo) {
                    database.photoDAO().insert((Photo) data);
                }

                try {
                    controller.createNewDocument(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static class AsyncGetDocumentNewest extends AsyncTask<AsyncArgumentWrapper, Void, GTData> {
        private AsyncCallBackManager callBack = null;
        private Context context;

        /**
         * contains the reference to the calling activity
         * @param callback
         */
        public AsyncGetDocumentNewest(AsyncCallBackManager callback, Context context) {
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

               // Log.i("getusergetuser", String.valueOf(result.getType().equals(User.class.toString())));
                try {
                    result = controller.getDocument(argument.getID(), Task.class);
                    Log.i("getusergetuser", result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(result.getType().equals(Task.class.toString())){
                    database.taskDAO().insert((Task) result);
                } else if(result.getType().equals(User.class.toString())) {
                    database.userDAO().insert((User) result);
                    Log.d("getusergetuser", result.toString());
                } else if (result.getType().equals(Bid.class.toString())) {
                    database.bidDAO().insert((Bid) result);
                } else if (result.getType().equals(Photo.class.toString())){
                    database.photoDAO().insert((Photo) result);
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
                    result = database.userDAO().selectByID(argument.getID());
                } else if (argument.getType().equals(Bid.class)) {
                    result = database.bidDAO().selectByID(argument.getID());
                } else if (argument.getType().equals(Photo.class)) {
                    result = database.photoDAO().selectByID(argument.getID());
                }

                if(result == null) {
                    try {
                        result = controller.getDocument(argument.getID(), argument.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;
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
    public static class AsyncDeleteLocalDocument extends  AsyncTask<AsyncArgumentWrapper, Void, Void> {

        private Context context;

        public AsyncDeleteLocalDocument(Context context){
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
                } else if (argument.getType().equals(Bid.class)) {
                    database.bidDAO().deleteByID(argument.getID());
                } else if (argument.getType().equals(Photo.class)) {
                    database.photoDAO().deleteByID(argument.getID());
                }
            }
            return null;
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
                } else if (argument.getType().equals(Bid.class)) {
                    database.bidDAO().deleteByID(argument.getID());
                } else if (argument.getType().equals(Photo.class)) {
                    database.photoDAO().deleteByID(argument.getID());
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
    public static class AsyncUpdateLocalDocument extends AsyncTask<GTData, Void, Void> {


        private Context context;

        public AsyncUpdateLocalDocument(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(GTData... dataList) {
            verifySettings(context);

            for(GTData data: dataList) {
                if (data instanceof Task){
                    database.taskDAO().update((Task) data);
                } else if (data instanceof User) {
                    database.userDAO().update((User) data);
                } else if (data instanceof Bid) {
                    database.bidDAO().update((Bid) data);
                } else if (data instanceof Photo) {
                    database.photoDAO().update((Photo) data);
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
                try {
                    controller.updateDocument(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (data instanceof Task){
                    database.taskDAO().update((Task) data);
                } else if (data instanceof User) {
                    database.userDAO().update((User) data);
                } else if (data instanceof Bid) {
                    database.bidDAO().update((Bid) data);
                } else if (data instanceof Photo) {
                    database.photoDAO().update((Photo) data);
                }
            }
            return null;
        }
    }


    /**
     * AsyncTask for search, returns result through callBack FROM SERVER
     */
    public static class AsyncSearchServer extends AsyncTask<AsyncArgumentWrapper, Void, List<? extends GTData>> {
        private AsyncCallBackManager callBack = null;

        public AsyncSearchServer(AsyncCallBackManager callback) {
            this.callBack = callback;
        }

        @Override
        protected List<? extends GTData> doInBackground(AsyncArgumentWrapper... argumentWrappers) {
            List<? extends GTData> resultList = null;
            controller.verifySettings();

            for(AsyncArgumentWrapper argument : argumentWrappers) {
                try {
                    resultList = controller.search(argument.getSearchQuery(), argument.getType());
                } catch (IOException e) {
                    e.printStackTrace();
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
                if (argument.getType().equals(Task.class)){
                    resultList = database.taskDAO().searchTasksByQuery(argument.getSQLQuery());
                } else if (argument.getType().equals(Bid.class)) {
                    resultList = database.bidDAO().searchBidsByQuery(argument.getSQLQuery());
                } else if (argument.getType().equals(Photo.class)) {
                    resultList = database.photoDAO().searchPhotosByQuery(argument.getSQLQuery());
                }
            }
            if(resultList != null) {
                Collections.sort(resultList);
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
}
