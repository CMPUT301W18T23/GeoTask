package com.geotask.myapplication.Controllers.LocalFilesOps;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.geotask.myapplication.DataClasses.Bid;

import java.util.List;

/**
 * DataAccessObject for Bid and local SQL database
 */
@Dao
public interface BidDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bid bid);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultiple(Bid... bids);

    @Query("SElECT * FROM bids")
    List<Bid> selectAll();

    @Query("SELECT * FROM bids WHERE providerId LIKE :providerID")
    List<Bid> selectByProvider(String providerID);

    @Query("SELECT * FROM bids WHERE taskId LIKE :taskID")
    List<Bid> selectByTask(String taskID);

    //ToDo: enfore that provider can only bid once on a task
    @Query("SELECT * FROM bids WHERE providerId LIKE :providerID AND taskId LIKE :taskID")
    Bid selectBid(String providerID, String taskID);

    @Update
    void update(Bid bid);

    @Delete
    void delete(Bid bid);

    /**
     * wipes bids table, use responsibly
     */
    @Query("DELETE FROM bids")
    int delete();

    @Query("SELECT * FROM bids WHERE objectId LIKE :id")
    Bid selectByID(String id);

    @Query("DELETE FROM bids WHERE objectId LIKE :id")
    void deleteByID(String id);

    @Query("DELETE FROM bids WHERE taskId LIKE :id")
    void deleteByTaskID(String id);

    @RawQuery
    List<Bid> searchBidsByQuery(SupportSQLiteQuery query);

    @RawQuery
    List<String> searchBidsByQueryRetString(SupportSQLiteQuery query);
}
