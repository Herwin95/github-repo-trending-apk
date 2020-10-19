package com.trending.gittrends.data.repository;

import androidx.annotation.NonNull;

import android.app.Application;
import android.util.Log;

import com.trending.gittrends.data.model.NetworkState;
import com.trending.gittrends.data.model.RepoGit;
import com.trending.gittrends.data.dao.RepoGitDao;
import com.trending.gittrends.data.DatabaseRoom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.trending.gittrends.ui.activity.MainActivity.on_start_load_api_data;

public class MyRepository {

    private final static String TAG = "MyRepository";

    private RepoGitDao repoGitDao;

    private MediatorLiveData<NetworkState> netState;

    private static final int DATA_SIZE = 20;

    public MyRepository(Application application){
        DatabaseRoom databaseRoom = DatabaseRoom.getInstance(application);
        repoGitDao = databaseRoom.repoGitDao();
        this.netState = new MediatorLiveData<>();
    }

    public MediatorLiveData<NetworkState> getNetworkState(){
        return netState;
    }

    public LiveData<PagedList<RepoGit>> getRepoList(String query){
        String statement = "SELECT * FROM RepoGit " + query;
        SupportSQLiteQuery sqlite_query = new SimpleSQLiteQuery(statement, new Object[]{});

        return fetchRepository(sqlite_query);
    }

    public LiveData<PagedList<RepoGit>> fetchRepository(SupportSQLiteQuery query) {

        PagedList.Config pagedConfig = new PagedList.Config.Builder()
                .setPageSize(DATA_SIZE)
                .build();

        DataSource.Factory<Integer, RepoGit> mDataRepos = repoGitDao.getRepositories(query);

        return new LivePagedListBuilder<>(mDataRepos, pagedConfig).build();
    }

    public void insertData(){

        OkHttpClient client = new OkHttpClient();
        List<RepoGit> mTmpList = new ArrayList<>();

        netState.setValue(NetworkState.LOADING);

        final Request request = new Request.Builder().url("https://gtrend.yapie.me/repositories").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call,@NonNull IOException e) {
                /*checking if there is offline data available if the network gets failed*/
                if(repoGitDao.getRepoCount() <= 0){
                    netState.postValue(NetworkState.FAILED_ND);
                }else {
                    netState.postValue(NetworkState.FAILED);
                }
                on_start_load_api_data = -1;
            }

            @Override
            public void onResponse(@NonNull Call call,@NonNull final Response response) {
                final JSONArray jsonResponse;
                try {
                    if(response.isSuccessful() && response.body() != null){
                        jsonResponse = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject jsonObject1 = jsonResponse.getJSONObject(i);
                            RepoGit repoGit = new RepoGit(
                                    jsonObject1.optString("name"),
                                    jsonObject1.optString("description"),
                                    Integer.parseInt(jsonObject1.optString("stars")),
                                    jsonObject1.optString("language"),
                                    jsonObject1.optString("languageColor"));

                            mTmpList.add(repoGit);
                        }

                        /*deleting all the rows in the table and adding all the refreshed data fetched from the api since the response header
                        * of the cache is set to cache-control: max-age=0 and hence it requires a re-validation*/

                        repoGitDao.deleteData();

                        repoGitDao.insertData(mTmpList);

                        netState.postValue(NetworkState.LOADED);
                    }else {
                        netState.postValue(NetworkState.FAILED);
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    netState.postValue(NetworkState.FAILED);
                }
                /*setting on_start_load_api_data to tell the viewmodel that the initial data refresh has been completed on starting the
                * application*/

                on_start_load_api_data = -1;
            }
        });
    }
}
