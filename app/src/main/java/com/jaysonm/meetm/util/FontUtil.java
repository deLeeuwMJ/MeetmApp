package com.jaysonm.meetm.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Locale;

public class FontUtil {

    public enum CUSTOM_FONTS {
        LEMON
    }

    public static Typeface getCustomTypeFace(Context context, CUSTOM_FONTS fontType) {
        AssetManager am = context.getApplicationContext().getAssets();

        String fontName;

        switch (fontType) {
            case LEMON:
                fontName = "lemon_juice.otf";
                break;
            default:
                fontName = "lemon_juice.otf";
                break;
        }

        return Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", fontName));
    }
}
