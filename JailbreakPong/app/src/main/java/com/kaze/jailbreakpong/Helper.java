package com.kaze.jailbreakpong;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Observer;

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

    // NOTE: on some phones with hardware navbar and disabled software buttons, getNavbarHeight still returns a positive value
    public static int getNavbarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // gets true boundaries
    public static BoardView.Boundaries getBoundaries() {
        Board board = Board.getInstance();
        return board.getBoundaries();
    }

    public static float getGridItemSize() {
        Board board = Board.getInstance();
        return board.getGridItemSize();
    }

    public static int getNumColumns() {
        Board board = Board.getInstance();
        return board.getNumColumns();
    }

    public static int getNumRows() {
        Board board = Board.getInstance();
        return board.getNumRows();
    }

    public static Board.State getGameState() {
        Board board = Board.getInstance();
        return board.getState();
    }

    public static void addObserver(Observer o) {
        Board board = Board.getInstance();
        board.addObserver(o);
    }

    public static void remove(BuildingView.Selected selection, int row, int col) {
        Board board = Board.getInstance();
        board.remove(selection, row, col);
    }

    public static void add(BuildingView.Selected selection, int row, int col) {
        Board board = Board.getInstance();
        board.add(selection, row, col);
    }

    public static void setupAnimatorVals(ValueAnimator animator, float newStart, float newEnd){

        // setup new values for the animator
        PropertyValuesHolder[] vals = (animator).getValues();
        vals[0].setFloatValues(newStart, newEnd);

        animator.setValues(vals);
    }
    
    // would use this in getNavBarHeight
    // however, doesn't work for emulators
    public static boolean hasSoftNavBar(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return resourceId > 0 && resources.getBoolean(resourceId);
    }
}
