package com.jaysonm.meetm.model.events;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jaysonm.meetm.controllers.events.WrapApiListener;
import com.jaysonm.meetm.model.events.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventFactory {
    private RequestQueue mQueue;
    private WrapApiListener wrapApiListener;

    private final String TAG = getClass().getSimpleName();


    public EventFactory(Context context, WrapApiListener listener) {
        this.mQueue = Volley.newRequestQueue(context);
        this.wrapApiListener = listener;
    }

    public void getEvents(String webUrl) {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, webUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    wrapApiListener.onApiSuccess(parseJson(response));
                } catch (JSONException e) {
                    Log.e("JSON error in " + TAG, e.getMessage());
                    wrapApiListener.onApiFailure(e);
                }
            }
        }, error -> Log.e(TAG, error.toString()));

        this.mQueue.add(request);
    }

    private ArrayList<Event> parseJson(JSONObject json) throws JSONException {

        ArrayList<Event> events = new ArrayList<>();

        JSONArray eventArr = json.getJSONObject("data").getJSONArray("event");

        for (int i = 1; i < eventArr.length(); i++) {
            JSONObject tempEvent = eventArr.getJSONObject(i);

            String imgUrl = tempEvent.getString("image");
            String name = tempEvent.getString("alt");

            JSONArray dateCollection = tempEvent.getJSONArray("dateCollection");
            String date = dateCollection.getJSONObject(dateCollection.length() - 1).getString("items"); //always get the date

            JSONArray detailCollection = tempEvent.getJSONArray("detailCollection");

            JSONObject setObj = detailCollection.getJSONObject(0).getJSONObject("set");

            String location = null;
            String price = null;

            if (!setObj.isNull("items")) {
                JSONArray detailArr = setObj.getJSONArray("items");

                if (detailArr.length() != 0) {
                    location = (String) detailArr.get(0);
                    if (detailArr.length() > 1) {
                        price = (String) detailArr.get(1);
                    }
                }
            }

            String ticketUrl = detailCollection.getJSONObject(0).getJSONObject("set").getJSONObject("tickets").getString("link");

            events.add(
                    new Event(
                            name,
                            imgUrl,
                            date,
                            location,
                            price,
                            ticketUrl
                    )
            );
        }

        return events;
    }
}
