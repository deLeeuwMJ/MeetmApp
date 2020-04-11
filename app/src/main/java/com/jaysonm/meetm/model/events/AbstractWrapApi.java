package com.jaysonm.meetm.model.events;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jaysonm.meetm.controllers.events.WrapApiListener;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractWrapApi extends AsyncTask<Void, Void, Void> {

    private static final String TAG = AbstractWrapApi.class.getSimpleName();

    protected ArrayList<WrapApiListener> _aListeners = new ArrayList();
    protected EventFactory eventFactory;

    protected AbstractWrapApi(Context context, WrapApiListener listener) {
        this.registerListener(listener);
        this.eventFactory = new EventFactory(context, listener);
    }

    public void registerListener(WrapApiListener mListener) {
        if (mListener != null) {
            this._aListeners.add(mListener);
        }
    }

    protected void dispatchOnStart() {
        Iterator i$ = this._aListeners.iterator();

        while (i$.hasNext()) {
            WrapApiListener mListener = (WrapApiListener) i$.next();
            mListener.onApiStart();
        }
    }

    private void dispatchOnCancelled() {
        Iterator i$ = this._aListeners.iterator();

        while (i$.hasNext()) {
            WrapApiListener mListener = (WrapApiListener) i$.next();
            mListener.onApiCancelled();
        }
    }

    protected Void doInBackground(Void... voids) {
        String url = this.constructURL();
        this.eventFactory.getEvents(url);
        Log.d(TAG, url);

        return null;
    }

    protected abstract String constructURL();

    protected void onPreExecute() {
        this.dispatchOnStart();
    }

    protected void onCancelled() {
        this.dispatchOnCancelled();
    }
}
