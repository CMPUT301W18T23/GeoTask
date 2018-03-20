package com.geotask.myapplication.Controllers.LocalFilesOps;

import android.content.Context;
import android.os.AsyncTask;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;


public class UserDatabaseController {
    private static LocalDataBase database;

    public static class insertUser extends AsyncTask<User, Void, Void> {

        private Context context;

        public insertUser(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(User... users) {
            database = LocalDataBase.getDatabase(context);

            for(User user : users) {
                database.userDAO().insert(user);
            }
            return null;
        }
    }

    public static class selectUserUniqueID extends AsyncTask<String, Void, User> {

        private Context context;
        private AsyncCallBackManager callback;

        public selectUserUniqueID(Context context, AsyncCallBackManager callback) {
            this.context = context;
            this.callback = callback;
        }


        @Override
        protected User doInBackground(String... IDList) {
            database = LocalDataBase.getDatabase(context);

            User user = null;
            for(String id : IDList) {
                user = database.userDAO().selectByID(id);
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if(callback != null) {
                callback.onPostExecute(user);
            }
        }
    }
}
