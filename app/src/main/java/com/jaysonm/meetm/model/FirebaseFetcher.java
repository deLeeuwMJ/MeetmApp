package com.jaysonm.meetm.model;

import com.jaysonm.meetm.controllers.DataAvailableListener;

public abstract class FirebaseFetcher {

    private DataAvailableListener dataAvailableListener;

    public abstract void startFetching();
    public abstract void getFetchData();
    public abstract void setListener(DataAvailableListener dataAvailableListener);

    protected void setDataAvailableListener(DataAvailableListener dataAvailableListener) {
        this.dataAvailableListener = dataAvailableListener;
    }

    protected DataAvailableListener getDataAvailableListener() {
        return dataAvailableListener;
    }
}
