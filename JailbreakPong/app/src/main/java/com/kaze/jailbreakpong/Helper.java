package com.kaze.jailbreakpong;

import android.content.Context;
import android.content.res.TypedArray;
import android.nfc.Tag;
import android.util.Log;

public class Helper {

    private final static String DEBUG = "HELPER";



    public static int getActionBarHeight(Context context){

        int actionBarHeight = 0;
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        Log.d(DEBUG, "getActionBarHeight: actionBarHeight: " + actionBarHeight);
        return actionBarHeight;
    }

    public static int getStatusBarHeight(Context context){

        // status bar
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        Log.d(DEBUG, "getStatusBarHeight: statusBarHeight: " + statusBarHeight);
        return statusBarHeight;
    }
}
