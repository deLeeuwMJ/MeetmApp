package com.jaysonm.meetm.util;

import android.content.Context;

import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.events.WrapApiListener;
import com.jaysonm.meetm.model.events.WrapApi;

public class EventUtil {

    private static final String WRAP_API_USER = "JaysonLowe";
    private static final String WRAP_API_REPOSITORY = "applications";
    private static final String WRAP_API_COLLECTION= "eventbrite-business-netherlands";
    private static final String WRAP_API_VERSION= "0.0.2";

    public static synchronized void requestApiEventData(int page, WrapApiListener wrapApiListener, Context context) {
        String key = context.getResources().getString(R.string.MEETM_WRAP_API_KEY);

        WrapApi wrapApi = new WrapApi.Builder()
                .withListener(wrapApiListener)
                .context(context)
                .user(WRAP_API_USER)
                .repository(WRAP_API_REPOSITORY)
                .collection(WRAP_API_COLLECTION)
                .version(WRAP_API_VERSION)
                .key(key)
                .page(page)
                .build();

        if(wrapApi != null)
            wrapApi.execute();
    }
}
