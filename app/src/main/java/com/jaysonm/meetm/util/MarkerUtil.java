package com.jaysonm.meetm.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaysonm.meetm.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarkerUtil {
    private static final float DEFAULT_ANCHOR_POINT = 0.5f;

    public static void addDefaultMarker(GoogleMap googleMap, LatLng latLng, String title, String subtitle) {
        MarkerOptions tempMarker = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(subtitle);

        googleMap.addMarker(tempMarker);
    }

    public static void addCustomMarker(GoogleMap googleMap, LatLng latLng, String title, String subtitle, Object object, Bitmap bitmap) {

        MarkerOptions tempMarker = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(subtitle)
                .anchor(DEFAULT_ANCHOR_POINT, DEFAULT_ANCHOR_POINT)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        Marker marker = googleMap.addMarker(tempMarker);
        marker.setTag(object);
    }

    public static Bitmap createCustomMarkerBitmap(Context context, Uri uri) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.meet_marker_layout, null);

        CircleImageView markerImage = marker.findViewById(R.id.circle_image_view);

        if (uri == null) {
            markerImage.setImageResource(R.drawable.meetm_logo_512_small);
        } else {
            Picasso.get().load(uri).into(markerImage);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
}
