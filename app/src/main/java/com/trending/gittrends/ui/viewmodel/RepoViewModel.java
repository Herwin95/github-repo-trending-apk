package com.trending.gittrends.ui.viewmodel;

import android.app.Application;

import com.trending.gittrends.data.model.NetworkState;
import com.trending.gittrends.data.model.RepoGit;
import com.trending.gittrends.data.repository.MyRepository;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

public class RepoViewModel extends AndroidViewModel {

    private LiveData<PagedList<RepoGit>> repos;

    private MyRepository myRepository;

    private MutableLiveData<String> query = new MutableLiveData<>();

    public RepoViewModel(Application application) {
        super(application);
        myRepository = new MyRepository(application);
        repos = Transformations.switchMap(query, input -> myRepository.getRepoList(input));
    }

    public LiveData<PagedList<RepoGit>> getList() {
        return repos;
    }

    public void setQuery(String category) {
        this.query.setValue(category);
    }

    public LiveData<NetworkState> getNetState() {
        return myRepository.getNetworkState();
    }

    public void refreshFeed(){
        myRepository.insertData();
    }

}