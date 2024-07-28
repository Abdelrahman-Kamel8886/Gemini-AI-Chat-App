package com.abdok.geminichat.Local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.Utils.ModelChatConverter;
import com.abdok.geminichat.Utils.UriTypeConverter;

@Database(entities = {ModelCache.class}, version = 1, exportSchema = false)
@TypeConverters({ModelChatConverter.class})
public abstract class CacheDatabase extends RoomDatabase {

    private static CacheDatabase instance;

    public abstract ChatDao cacheDao();

    public static synchronized CacheDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            CacheDatabase.class, "cache_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
