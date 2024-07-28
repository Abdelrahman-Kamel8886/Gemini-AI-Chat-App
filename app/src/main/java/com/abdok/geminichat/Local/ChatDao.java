package com.abdok.geminichat.Local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.Models.ModelChat;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ChatDao {

    @Insert
    Completable insertCache(ModelCache modelCache);

    @Query("SELECT * FROM cache_table")
    Single<List<ModelCache>> getAllCaches();

    @Query("DELETE FROM cache_table WHERE id = :cacheId")
    Completable deleteCacheById(int cacheId);

    @Delete
    Completable delete(ModelCache modelCache);

    @Query("UPDATE cache_table SET title = :title WHERE id = :cacheId")
    Completable updateTitleById(int cacheId, String title);

    @Query("SELECT * FROM cache_table ORDER BY id DESC LIMIT 1")
    Single <ModelCache> getLastItem();

    @Update
    Completable updateCache(ModelCache modelCache);


}