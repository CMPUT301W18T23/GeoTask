package com.geotask.myapplication.Controllers.LocalFilesOps;


import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.geotask.myapplication.DataClasses.Photo;

import java.util.List;

@Dao
public interface PhotoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultiple(Photo... photos);

    @Query("SELECT * FROM photos WHERE objectId LIKE :id")
    Photo selectByID(String id);


    @Query("DELETE FROM photos WHERE objectId LIKE :id")
    void deleteByID(String id);

    @Update
    void update(Photo photo);

    @RawQuery
    List<Photo> searchPhotosByQuery(SimpleSQLiteQuery build);
}
