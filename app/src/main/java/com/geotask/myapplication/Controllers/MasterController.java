package com.geotask.myapplication.Controllers;

import android.os.AsyncTask;

import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.DataClasses.GTData;

import java.io.IOException;
import java.util.List;

public class MasterController {

    static ElasticsearchController controller = new ElasticsearchController();

    public static void verifySettings() {
        controller.verifySettings();
    }

    public static void setTestSettings(String testSettings) {
        controller.setTestSettings(testSettings);
    }

    public static void createIndex() throws IOException {
        controller.createIndex();
    }

    public static void deleteIndex() throws IOException {
        controller.deleteIndex();
    }

    public static void shutDown() {
        controller.shutDown();
    }

    public static boolean existsProfile(String s) {
        return controller.existsProfile(s);
    }

    public static class AsyncCreateNewDocument extends AsyncTask<GTData, Void, Void> {

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

    public static class AsyncGetDocument extends AsyncTask<AsyncArgumentWrapper, Void, GTData> {
        private AsyncCallBackManager callBack = null;

        public AsyncGetDocument(AsyncCallBackManager callback) {
            this.callBack = callback;
        }

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

        @Override
        protected void onPostExecute(GTData data) {
            if(callBack != null) {
                callBack.onPostExecute(data);
            }
        }
    }

    public static class AsyncDeleteDocument extends  AsyncTask<AsyncArgumentWrapper, Void, Void> {

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

    public static List<? extends GTData> Search(AsyncArgumentWrapper argumentWrapper) throws IOException {
        return controller.search(argumentWrapper.getSearchQuery(), argumentWrapper.getType());
    }
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
