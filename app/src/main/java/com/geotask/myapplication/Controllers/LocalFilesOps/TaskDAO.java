package com.geotask.myapplication.Controllers.LocalFilesOps;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.geotask.myapplication.DataClasses.Task;

import java.util.List;

@Dao
interface TaskDAO {

    @Insert
    void insert(Task task);

    @Insert
    void insertMultiple(Task... tasks);

    @Query("SELECT * FROM tasks WHERE requester_id LIKE :requesterID")
    List<Task> selectByRequester(String requesterID);

    @Query("SELECT * FROM tasks")
    List<Task> selectAll();

    @Query("SELECT * FROM tasks WHERE status LIKE :status")
    List<Task> selectByStatus(String status);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
}
