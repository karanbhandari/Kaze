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

import java.util.Observable;
import java.util.Observer;
import java.util.Random;


public class Ball extends View implements Observer {

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

    Board board = Board.getInstance();

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

        Helper.addObserver(this);

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
        rect.left = getPosX();
        rect.right = getPosX() + getSize();
        rect.top = getPosY();
        rect.bottom = getPosY() + getSize();
        canvas.drawOval(rect, paint);
        invalidate();
    }

    public void reverseDirections(){
        reverseX();
        reverseY();
    }

    public void reverseX(){
        dir[0] = -1 * dir[0];
    }

    private void reverseY(){
        dir[1] = -1 * dir[1];
    }

    public float getEndX(){

        /*
        *   - only reverse direction if we've hit the end of the screen
        */

        DisplayMetrics metrics = Helper.getDisplayMetrics(getContext());

        // hit the end of the screen
        // Ideally this conditionshould be getPosX() == 0 and getPosX() + getSize == metrics.widthPixels
        // but Android doesn't play a long too well :(
        if ( Math.abs(getPosX()) <= getSize() ||  Math.abs(getPosX() + getSize() - metrics.widthPixels) <= getSize()){
            reverseX();
        }

        Random rand = new Random();
        int num = rand.nextInt(2);

        if (dir[0] == -1){
            // going left
            if (num == 1){
                return 0;
            } else {
                return 0;
//                return (float) (metrics.widthPixels / 2) - getSize();
            }
        } else {
            // going right
            if(num == 1){
                return metrics.widthPixels - getSize();
            } else {
                return metrics.widthPixels - getSize();
//                return (float) (metrics.widthPixels / 2) - getSize();
            }
        }
    }

    public void setNewEnd(){
        /*
         *  TODO:
         *      this methods will setup new endpoints for the animators.
         *      will possibly be called AFTER a bounce (hit)
         *
         *      - Need reference to both of the animators
         *      - update their animation values based on what we get here.
         *      - starting probably be getPosX() and ending will have to be calculated
         */
    }

    public float getEndY(float topY, float botY){

        Random rand = new Random();
        int num = rand.nextInt(2);

        // reverse direction of ball
        if (Math.abs(getPosY() - topY) < getSize() || Math.abs(getPosY() + getSize() - botY) < getSize()){
            reverseY();
        }

        float actualHeight = botY - topY;

        // moving up
        if (dir[1] == -1){
            if (num == 1){
                return topY;
            } else {
                return actualHeight / 2;
            }

        } else {
            if (num == 1){
                return botY - size;
            } else {
                return actualHeight / 2;
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

    public void addXAnimator(){

        final Ball ball = this;

        final Context context = getContext();
        DisplayMetrics metrics = Helper.getDisplayMetrics(context);
        float endPoint = metrics.widthPixels - getSize();
        final ValueAnimator animator = ValueAnimator.ofFloat(getPosX(), endPoint);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // On Update, set the X position of Ball
                float animatedVal = (float) animator.getAnimatedValue();
                setPosX(animatedVal);
                board.isHit(animatedVal, 0, size, ball);
            }
        });

        // setup what happens when animation starts over
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);
                Log.d("BALL", "onAnimationRepeat: getPostX(): " + getPosX());
                // get end direction of ball
                float newEnd = getEndX();
                Helper.setupAnimatorVals((ValueAnimator) animation, getPosX(), newEnd);

            }
        });
        setAnimatorTimeUsingSpeed(animator, speed);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void addYAnimator(final float topY, final float botY){

        // subtract size cus endingFloat is supposed to be top left corner
        float endPoint = botY - getSize();
        final ValueAnimator animator = ValueAnimator.ofFloat(getPosY(), endPoint);

        // setup initial animator listener
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();
                setPosY(animatedVal);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);

                // get end direction of ball
                float newEndY = getEndY(topY, botY);

                // setup new values for the animator
                Helper.setupAnimatorVals((ValueAnimator) animation, getPosY(), newEndY);

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

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = Helper.getGameState();

        switch(state) {
            case PLAY:
                this.setVisibility(View.VISIBLE);
                break;
            case PAUSE:
                this.setVisibility(View.GONE); // should instead pause the ball, visibility should be VISIBLE
                break;
            case END:
                this.setVisibility(View.GONE);
                break;
            default:
                this.setVisibility(View.GONE);
        }
    }
}
