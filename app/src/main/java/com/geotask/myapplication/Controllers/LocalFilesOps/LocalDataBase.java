package com.geotask.myapplication.Controllers.LocalFilesOps;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;


/**
 * singleton RoomDataBase object used to instanstiate reference used to access database
 */
@Database(entities = {User.class, Bid.class, Task.class}, version = 11)
public abstract class LocalDataBase extends RoomDatabase{

    private static LocalDataBase instance;

    public abstract UserDAO userDAO();
    public abstract TaskDAO taskDAO();
    public abstract BidDAO bidDAO();

    public static LocalDataBase getDatabase(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                                            LocalDataBase.class,
                                            "local_data")
                                            .fallbackToDestructiveMigration()
                                            .build();
        }
        return instance;
    }
}
