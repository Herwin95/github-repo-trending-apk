package com.trending.gittrends.data.dao;

import com.trending.gittrends.data.model.RepoGit;

import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

@Dao
public interface RepoGitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(List<RepoGit> repoEntries);

    @RawQuery(observedEntities = RepoGit.class)
    DataSource.Factory<Integer, RepoGit> getRepositories(SupportSQLiteQuery query);

    @Query("delete from RepoGit")
    void deleteData();

    @Query("SELECT count(*) FROM RepoGit")
    int getRepoCount();

}
