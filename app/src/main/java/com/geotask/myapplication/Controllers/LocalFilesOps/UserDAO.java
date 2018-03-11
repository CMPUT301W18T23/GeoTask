package com.geotask.myapplication.Controllers.LocalFilesOps;

//https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.geotask.myapplication.DataClasses.User;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    void insert(User user);
    @Insert
    void insertMultiple(User... users);

    @Query("SELECT * FROM users")
    List<User> selectAll();

    @Query("SELECT * FROM users WHERE name LIKE :name")
    User selectByName(String name);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
