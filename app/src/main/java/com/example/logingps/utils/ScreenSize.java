package com.example.logingps.utils;

import android.content.Context;
import android.content.res.Configuration;

public class ScreenSize {
    public static int getScreenSize(Context context) {
        return context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    }
}
