package com.geotask.myapplication.Controllers;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.MenuActivity;

import java.io.IOException;
import java.util.List;

/**
 * All data management goes through here. This controller will keep local SQL database synced to the server.
 * Contains ElasticsearchController and 3 DatabaseController.
 * Do not call ElasticSearchController and DatabaseControllers explicitly, use this instead.
 */
//ToDo add sync logic between local and server
public class MasterController {

    static ElasticsearchController controller = new ElasticsearchController();

    public static void verifySettings() {
        controller.verifySettings();
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

    public static boolean existsProfile(String s) {
        controller.verifySettings();
        return controller.existsProfile(s);
    }

    /**
     * AsyncTask for putting document into server
     */
    public static class AsyncCreateNewDocument extends AsyncTask<GTData, Void, Void> {

        /**
         *
         * @param dataList queue of GTData waiting for execution
         * @return assumes success, will not throw exceptions if failed
         */
        @Override
        protected Void doInBackground(GTData... dataList) {
            controller.verifySettings();

            for(GTData data : dataList) {
                try {
                    controller.createNewDocument(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * AsyncTask for getting a single document by ID
     */
    public static class AsyncGetDocument extends AsyncTask<AsyncArgumentWrapper, Void, GTData> {
        private AsyncCallBackManager callBack = null;

        /**
         * contains the reference to the calling activity
         * @param callback
         */
        public AsyncGetDocument(AsyncCallBackManager callback) {
            this.callBack = callback;
        }

        /**
         *
         * @param argumentList
         * @return returns null if failed, otherwise returns GTData
         */
        @Override
        protected GTData doInBackground(AsyncArgumentWrapper... argumentList) {
            GTData result = null;
            controller.verifySettings();

            for (AsyncArgumentWrapper argument : argumentList) {
                try {
                    result = controller.getDocument(argument.getID(), argument.getType());
                } catch (Exception e) {
                    e.printStackTrace();
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

        /**
         *
         * @param argumentList
         * @return nothing, assumes success
         */
        @Override
        protected Void doInBackground(AsyncArgumentWrapper... argumentList) {
            controller.verifySettings();

            for(AsyncArgumentWrapper argument : argumentList) {
                try {
                    controller.deleteDocument(argument.getID(), argument.getType());
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

        @Override
        protected Void doInBackground(GTData... dataList) {
            controller.verifySettings();

            for(GTData data: dataList) {
                try {
                    controller.updateDocument(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        private AsyncCallBackManager callBack = null;

        public AsyncSearch(AsyncCallBackManager callback) {
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

}
