package com.kaze.jailbreakpong;

import android.content.Context;
import android.content.res.TypedArray;
import android.nfc.Tag;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Helper {

    private final static String DEBUG = "HELPER";


    // initializes and returns and instance of Ball
    public static Ball initBall(Context context, float x, float y, int size, float speed){

        // x and y are the left and top coordinates of the
        // 'rectangle' that is used to draw the ball
        Ball ball = new Ball(context, x, y, size, speed);
        Log.d("HELPER", "initBall: ball.posX : " + ball.getPosX());

        return ball;
    }

    // self documenting
    public static DisplayMetrics getDisplayMetrics(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);

        return metrics;
    }

    // self documenting
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

    // self documenting
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
