package com.trending.gittrends.data;

import android.content.Context;

import com.trending.gittrends.data.model.RepoGit;
import com.trending.gittrends.data.dao.RepoGitDao;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {RepoGit.class}, version = 1)
public abstract class DatabaseRoom extends RoomDatabase {

    private static DatabaseRoom sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "gittrends.db";

    public abstract RepoGitDao repoGitDao();

    public static DatabaseRoom getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (DatabaseRoom.class) {
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        DatabaseRoom.class, DatabaseRoom.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .addCallback(mCallback)
                        .build();
            }
        }

        return sInstance;
    }

    private static RoomDatabase.Callback mCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

}
