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
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.*;

import java.util.Random;


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
        Log.d("CONSTRUCTOR", "Ball: posX: " + posX);

        rect = new RectF(posX, getTop(), posX + size, getTop() + size);

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

        DisplayMetrics metrics = Helper.getDisplayMetrics(getContext());
        Random rand = new Random();
        int num = rand.nextInt(2);
        if (dir[0] == -1){
            return 0;
//            if (num == 0){
//
//            } else {
//                return metrics.widthPixels / 2;
//            }
        } else {
            if (num == 0){
                return metrics.widthPixels - getSize();
            } else {
                return metrics.widthPixels / 2;
            }
        }
    }

    public float getEndY(Context context, float topY, float botY){

        Random rand = new Random();
        int num = rand.nextInt(4);

        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int statusBarHeight = Helper.getStatusBarHeight(context);
        int actionBarHeight = Helper.getActionBarHeight(context);

        float properHeight = screenHeight - statusBarHeight - actionBarHeight;

        // moving up
        if (dir[1] == -1){
            if (num != 0){
                return topY;
            } else {
                return (botY - topY)/ 2;
            }

        } else {

            if (num == 0){
                return botY - size;
            } else {
                return (botY - topY)/ 2;
            }
        }
    }


    /*
    *
    * Animator methods
    *
    * */

    public void addAnimators(float topY, float botY){
        addXAnimator();
        addYAnimator(topY, botY);
    }

    private void addXAnimator(){

        final Context context = getContext();
        DisplayMetrics metrics = Helper.getDisplayMetrics(context);
        float endingFloat = metrics.widthPixels - getSize();
        final ValueAnimator animator = ValueAnimator.ofFloat(getPosX(), endingFloat);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();
                Log.d("Animator", "onAnimationUpdate: animatedVal: " + getPosX());
                setPosX(animatedVal);
                rect.left = getPosX();
                rect.right = getPosX() + getSize();
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
        setAnimatorTimeUsingSpeed(animator, speed);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void addYAnimator(final float topY, final float botY){
        final Context context = getContext();

        // subtract size cus endingFloat is supposed to be top left corner
        float endingFloat = botY - size;
        final ValueAnimator animator = ValueAnimator.ofFloat(getPosY(), endingFloat);
        Log.d("YAnim", "height calced: " + endingFloat);

        // setup initial animator listener
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();
                setPosY(animatedVal);
                rect.top = getPosY();
                rect.bottom = getPosY() + getSize();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);
                // reverse direction of ball
                reverseY();
                // get end direction of ball
                float newEndY = getEndY(context, topY, botY);
                // setup new values for the animator
                PropertyValuesHolder[] curVals = ((ValueAnimator)animation).getValues();
                curVals[0].setFloatValues(getPosY(), newEndY);

                ((ValueAnimator)animation).setValues(curVals);

            }
        });

        setAnimatorTimeUsingSpeed(animator, speed);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    // speed property is used to set the duration of the animation.
    private void setAnimatorTimeUsingSpeed(ValueAnimator animator, float speed){
        // calculate the time of the animation
        int ms = (int) (( 1 /speed ) * 1000);
        animator.setDuration(ms);
    }

}
