package com.trending.gittrends.data.model;

public class NetworkState {
    public enum Status{
        RUNNING,
        SUCCESS,
        FAILED,
        FAILED_NO_DATA
    }
    private final Status status;

    public static final NetworkState LOADING;
    public static final NetworkState LOADED;
    public static final NetworkState FAILED;
    public static final NetworkState FAILED_ND;

    static {
        LOADING = new NetworkState(Status.RUNNING);
        LOADED = new NetworkState(Status.SUCCESS);
        FAILED = new NetworkState(Status.FAILED);
        FAILED_ND = new NetworkState(Status.FAILED_NO_DATA);
    }

    public NetworkState(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

}