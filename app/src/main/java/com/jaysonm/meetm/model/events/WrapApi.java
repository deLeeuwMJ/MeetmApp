package com.jaysonm.meetm.model.events;

import android.content.Context;
import android.util.Log;

import com.jaysonm.meetm.controllers.events.WrapApiListener;

public class WrapApi extends AbstractWrapApi {

    private static final String BUILD_ERROR = WrapApi.class.getSimpleName();
    private static final String BASE_URL = "https://wrapapi.com/use/";

    private final String user;
    private final String repository;
    private final String collection;
    private final String version;
    private final String key;
    private final int page;

    private WrapApi(WrapApi.Builder builder) {
        super(builder.context, builder.listener);
        this.user = builder.user;
        this.repository = builder.repository;
        this.collection = builder.collection;
        this.version = builder.version;
        this.key = builder.key;
        this.page = builder.page;
    }

    @Override
    protected String constructURL() {
        StringBuilder stringBuilder = new StringBuilder(BASE_URL);
        stringBuilder.append(this.user + "/");
        stringBuilder.append(this.repository + "/");
        stringBuilder.append(this.collection + "/");
        stringBuilder.append(this.version);
        stringBuilder.append("?count=");
        stringBuilder.append(this.page);
        stringBuilder.append("&wrapAPIKey=");
        stringBuilder.append(this.key);

        return stringBuilder.toString();
    }

    public static class Builder {
        private WrapApiListener listener;
        private Context context;
        private String user;
        private String repository;
        private String collection;
        private String version;
        private String key;
        private int page;

        public Builder() {
            this.listener = null;
            this.context = null;
            this.user = null;
            this.repository = null;
            this.collection = null;
            this.version = null;
            this.key = null;
            this.page = 0;
        }

        public WrapApi.Builder withListener(WrapApiListener listener) {
            this.listener = listener;
            return this;
        }

        public WrapApi.Builder context(Context context) {
            this.context = context;
            return this;
        }

        public WrapApi.Builder user(String user) {
            this.user = user;
            return this;
        }

        public WrapApi.Builder repository(String repository) {
            this.repository = repository;
            return this;
        }

        public WrapApi.Builder collection(String collection) {
            this.collection = collection;
            return this;
        }

        public WrapApi.Builder version(String version) {
            this.version = version;
            return this;
        }

        public WrapApi.Builder page(int page) {
            this.page = page;
            return this;
        }

        public WrapApi.Builder key(String key) {
            this.key = key;
            return this;
        }

        public WrapApi build() {
            if (this.page <= 0 || this.page > 49) {
                Log.e(BUILD_ERROR, "Page must be between 1 and 49");
                return null;
            } else if (this.listener == null) {
                Log.e(BUILD_ERROR, "Listener can't be null");
                return null;
            } else if (this.user == null) {
                Log.e(BUILD_ERROR, "User can't be null");
                return null;
            } else if (this.repository == null) {
                Log.e(BUILD_ERROR, "Repository can't be null");
                return null;
            } else if (this.collection == null) {
                Log.e(BUILD_ERROR, "Collection can't be null");
                return null;
            } else if (this.context == null) {
                Log.e(BUILD_ERROR, "Context can't be null");
                return null;
            } else {
                return new WrapApi(this);
            }
        }
    }
}
