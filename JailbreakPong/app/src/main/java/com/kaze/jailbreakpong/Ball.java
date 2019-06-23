package com.kaze.jailbreakpong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import static android.content.ContentValues.TAG;
import static java.lang.Float.max;
import static java.lang.Float.min;


public class Ball extends View {

    // Coordinates
    private float posX, posY;

    // Size
    private int size;

    // Speed
    private float speed;

    // direction
    private int [] dir = {1, 1};

    RectF rect;
    Paint paint;


    /*
    * Constructors
    */
    public Ball(Context context, float posX, float posY, int size, float speed) {
        super(context);

        this.posX = posX;
        this.size = size;
        this.posY = posY;
        this.speed = speed;

        rect = new RectF(posX, posY, posX + size, posY + size);

        paint = new Paint();
        paint.setColor(Color.RED);

    }

    public Ball(Context context) {
        super(context);
    }

    /*
    * Get Set
    */

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setPosition(float posY, float posX){
        this.posX = posX;
        this.posY = posY;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int[] getDir() {
        return dir;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("BALL", "onDraw() called");
        canvas.drawOval(rect, paint);
        invalidate();
    }

    public void reverseDirections(){
        reverseX();
        reverseY();
    }

    private void reverseX(){
        dir[0] = -1 * dir[0];
    }

    private void reverseY(){
        dir[1] = -1 * dir[1];
    }

    public float getEndX(){

        if (dir[0] == -1){
            return 0;
        } else {
            float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            return screenWidth - getSize();
        }
    }

    public float getEndY(Context context){

        if (dir[1] == -1){
            return 0;
        } else {
            float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

            int statusBarHeight = Helper.getStatusBarHeight(context);
            int actionBarHeight = Helper.getActionBarHeight(context);

            Log.d("BALL", "getEndPointY: screenHeight: " + screenHeight);

            float toRet = screenHeight - statusBarHeight - actionBarHeight;
            Log.d("BALL", "getEndPointY: toRet: " + toRet);


            return screenHeight - statusBarHeight - actionBarHeight - size;
        }
    }


    /*
    *
    * Animator methods
    *
    * */

    public void addAnimators(){

        addXAnimator();
        addYAnimator();

    }

    private void addXAnimator(){

        final Context context = getContext();
        DisplayMetrics metrics = Helper.getDisplayMetrics(context);

        float endingFloat = metrics.widthPixels - getSize();
        final ValueAnimator animator = ValueAnimator.ofFloat(0, endingFloat);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();
                setX(animatedVal);
                setPosX(animatedVal);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);
                // reverse direction of ball
                reverseX();
                // get end direction of ball
                float newEnd = getEndX();

                // setup new values for the animator
                PropertyValuesHolder[] vals = ((ValueAnimator)animation).getValues();
                vals[0].setFloatValues(getPosX(), newEnd);

                ((ValueAnimator)animation).setValues(vals);

            }
        });
        int ms = (int) (( 1 /speed ) * 1000);
        animator.setDuration(ms);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void addYAnimator(){

        final Context context = getContext();
        DisplayMetrics metrics = Helper.getDisplayMetrics(context);

        // getMetrics does not account for statusBar or actionBar height
        int statusBarHeight = Helper.getStatusBarHeight(context);
        int actionBarHeight = Helper.getActionBarHeight(context);

        float endingFloat = metrics.heightPixels - statusBarHeight - actionBarHeight - getSize();
        final ValueAnimator animator = ValueAnimator.ofFloat(0, endingFloat);
        Log.d("YAnim", "height calced: " + endingFloat);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();
                setY(animatedVal);
                setPosY(animatedVal);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);

                // reverse direction of ball
                reverseY();

                // get end direction of ball
                float newEndY = getEndY(context);

                // setup new values for the animator
                PropertyValuesHolder[] vals = ((ValueAnimator)animation).getValues();
                vals[0].setFloatValues(getPosY(), newEndY);

                ((ValueAnimator)animation).setValues(vals);

            }
        });
        int ms = (int) (( 1 /speed ) * 1000);
        animator.setDuration(ms);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

}
