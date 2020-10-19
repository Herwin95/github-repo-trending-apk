package com.trending.gittrends.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trending.gittrends.data.repository.MyRepository;
import com.trending.gittrends.data.model.NetworkState;
import com.trending.gittrends.R;
import com.trending.gittrends.ui.adapter.RepoAdapter;
import com.trending.gittrends.ui.viewmodel.RepoViewModel;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    ProgressBar mProgressBar;

    RecyclerView mRepoList;

    RelativeLayout mRelNetwork;

    RepoAdapter mRepoAdapter;

    RepoViewModel mRViewModel;

    Button mNetwork_btn;

    SwipeRefreshLayout mSwipe;

    public static int on_start_load_api_data = 1;

    private ImageView mClose;

    private SearchView mSearch;

    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*initialise views*/
        mRepoList = findViewById(R.id.repoList);
        mRelNetwork = findViewById(R.id.relative_layout_network_lost);
        mProgressBar = findViewById(R.id.progress_main);
        mSwipe = findViewById(R.id.swipe);
        mNetwork_btn = findViewById(R.id.retry_network);
        mClose = findViewById(R.id.back_arrow);
        mSearch = findViewById(R.id.searchButton);
        mTitle = findViewById(R.id.title);

        mRepoList.setLayoutManager(new LinearLayoutManager(this));
        mRepoList.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        mRepoAdapter = new RepoAdapter();
        mRepoList.setAdapter(mRepoAdapter);

        mRViewModel = ViewModelProviders.of(this).get(RepoViewModel.class);

        setVisibility();

        initialiseDatabaseObserver();

        initialiseNetworkObserver();

        initialiseSearch();

        mSwipe.setColorSchemeResources(R.color.white);
        mSwipe.setProgressBackgroundColorSchemeColor(Color.parseColor("#0366d6"));

        mSwipe.setOnRefreshListener(() -> mRViewModel.refreshFeed());

        mNetwork_btn.setOnClickListener(view -> mRViewModel.refreshFeed());

        mClose.setOnClickListener(view -> {
            mSearch.clearFocus();
            mClose.setVisibility(View.GONE);
        });

        refreshData();
    }

    private void setVisibility(){
        mRelNetwork.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mClose.setVisibility(View.GONE);
    }

    /* refreshing data on start of the application*/
    private void refreshData(){
        if(on_start_load_api_data == 1){
            mRViewModel.refreshFeed();
        }
    }

    private void initialiseDatabaseObserver(){
        /*Initialising initial query as "" to select all data available from the local database cache and submitting as paged list
        *in recycler view, checking if on_start_load_api = -1 so that the refreshed data gets displayed on starting the application*/

        mRViewModel.setQuery("");
        mRViewModel.getList().observe(this, repos -> {
            if (repos != null && on_start_load_api_data == -1){
                mRepoAdapter.submitList(repos);
            }
        });
    }

    private void initialiseNetworkObserver(){
        /* Handling different network states and changing the UI as per the state*/

        mRViewModel.getNetState().observe(this, networkState -> {
            if (networkState.getStatus().equals(NetworkState.Status.SUCCESS)) {
                hideProgress();
                setSwipeRefresh();
                displaySearch();
                mSwipe.setVisibility(View.VISIBLE);
                mRelNetwork.setVisibility(View.GONE);
            }else if(networkState.getStatus().equals(NetworkState.Status.FAILED) && mSwipe.isRefreshing()){
                hideProgress();
                setSwipeRefresh();
                mSwipe.setVisibility(View.GONE);
                mRelNetwork.setVisibility(View.VISIBLE);
                displaySearch();
            }else if (networkState.getStatus().equals(NetworkState.Status.FAILED_NO_DATA)){
                hideProgress();
                mRelNetwork.setVisibility(View.VISIBLE);
                displaySearch();
            }else if (networkState.getStatus().equals(NetworkState.Status.FAILED) && mSwipe.getVisibility() == View.GONE){
                hideProgress();
                mRelNetwork.setVisibility(View.VISIBLE);
            } else if (networkState.getStatus().equals(NetworkState.Status.FAILED)){
                hideProgress();
            } else if (networkState.getStatus().equals(NetworkState.Status.RUNNING) && mSwipe.isRefreshing()){
                hideProgress();
            }else {
                displayProgress();
            }
        });
    }

    private void initialiseSearch(){

        /* Hiding the title and displaying back button on click of search View*/
        mSearch.setOnSearchClickListener(view -> {
            mTitle.setVisibility(View.GONE);
            mClose.setVisibility(View.VISIBLE);
        });

        /*Handling on close of searchview*/
        mSearch.setOnCloseListener(() -> {
            mTitle.setVisibility(View.VISIBLE);
            mClose.setVisibility(View.GONE);
            return false;
        });

        /*Handing search utility*/
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String filtered_query = !TextUtils.isEmpty(newText) ? "where title like '" + newText + "%' collate nocase" : "";
                mRViewModel.setQuery(filtered_query);
                return true;
            }
        });

        /*Handling back button in title bar*/
        mSearch.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                mSearch.setIconified(true);
            }
        });
    }

    public void displaySearch(){
        if(mRelNetwork.getVisibility() == View.VISIBLE){
            mSearch.setVisibility(View.GONE);
            mClose.setVisibility(View.GONE);
            mTitle.setVisibility(View.VISIBLE);
        } else {
            mSearch.setVisibility(View.VISIBLE);
        }
    }

    public void displayProgress(){
        mProgressBar.setVisibility(View.VISIBLE);
        mRelNetwork.setVisibility(View.GONE);
    }

    public void setSwipeRefresh(){
        if(mSwipe.isRefreshing()) {
            mSwipe.setRefreshing(false);
        }
    }

    public void hideProgress(){
        mProgressBar.setVisibility(View.GONE);
    }

}