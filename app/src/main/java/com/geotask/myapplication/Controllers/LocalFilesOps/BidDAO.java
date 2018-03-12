package com.geotask.myapplication.Controllers.LocalFilesOps;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;

import java.util.List;

@Dao
public interface BidDAO {

    @Insert
    void insert(Bid bid);
    @Insert
    void insertMultiple(Bid... bids);

    @Query("SELECT * FROM bids WHERE provider_id LIKE :providerID")
    List<Bid> selectByProvider(String providerID);

    @Query("SELECT * FROM bids WHERE task_id LIKE :taskID")
    List<Bid> selectByTask(String taskID);

    //ToDo: enfore that provider can only bid once on a task
    @Query("SELECT * FROM bids WHERE provider_id LIKE :providerID AND task_id LIKE :taskID")
    Bid selectBid(String providerID, String taskID);

    @Update
    void update(Bid bid);

    @Delete
    void delete(Bid bid);

    @Query("DELETE FROM bids")
    void delete();
}
