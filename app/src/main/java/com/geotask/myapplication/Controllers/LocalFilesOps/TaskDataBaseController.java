package com.geotask.myapplication.Controllers.LocalFilesOps;


import android.content.Context;
import android.os.AsyncTask;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.DataClasses.Task;


public class TaskDataBaseController {
    private static LocalDataBase database;

    public static class insertTask extends AsyncTask<Task, Void, Void> {

        private Context context;

        public insertTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Task... taskList) {
            database = LocalDataBase.getDatabase(context);

            for(Task task : taskList){
                database.taskDAO().insert(task);
            }
            return null;
        }
    }

    public static class selectTask extends AsyncTask<String, Void, Task> {
        private Context context;
        private AsyncCallBackManager callback;

        public selectTask(Context context, AsyncCallBackManager callback) {
            this.context = context;
            this.callback = callback;
        }


        @Override
        protected Task doInBackground(String... taskIDList) {
            database = LocalDataBase.getDatabase(context);

            Task task = null;
            for(String taskID : taskIDList) {
                task = database.taskDAO().selectByID(taskID);
            }

            return task;
        }

        @Override
        protected void onPostExecute(Task task) {
            if(callback != null) {
                callback.onPostExecute(task);
            }
        }
    }
}
