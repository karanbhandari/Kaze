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

    final int SCREEN_BOUNCE_THRESHOLD = 25;

    // Coordinates
    private float posX, posY;

    // Size
    private int size;

    // Speed
    private float speed;

    // direction
    private int [] dir = {1, 1};

    // For painting
    RectF rect;
    Paint paint;

    // Need access to the board
    Board board = Board.getInstance();

    // Animators
    ValueAnimator animatorX = null;
    ValueAnimator animatorY = null;

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

    public void reverseY(){
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
//        if ( Math.abs(getPosX()) <= getSize() ||  Math.abs(getPosX() + getSize() - metrics.widthPixels) <= getSize()){
//            reverseX();
//        }

        Random rand = new Random();
        int num = rand.nextInt(2);

        if (dir[0] == -1){
            // going left
            if (num == 1){
                return 0;
            } else {
                return 0;
            }
        } else {
            // going right
            if(num == 1){
                return metrics.widthPixels - getSize();
            } else {
                return metrics.widthPixels - getSize();
            }
        }
    }

    public void setNewEndX(float start){
        /*
         *  TODO:
         *      this methods will setup new endpoints for the animators.
         *      will possibly be called AFTER a bounce (hit)
         *
         *      - Need reference to both of the animators
         *      - update their animation values based on what we get here.
         *      - starting probably be getPosX() and ending will have to be calculated
         */

        animatorX.end();
        if (start <=0){
            start = 1;
        }
        Helper.setupAnimatorVals(animatorX, start - 1, getEndX());
        Log.d("BALL", "setNewEnd: new start: " + start + " new end: " + getEndX());
        Log.d("BALL", "setNewEnd: ball pos when animator reset: " + getPosX());
        setAnimatorTimeUsingSpeed(animatorX, start, getEndX());
        animatorX.start();

    }

    public float getEndY(){

        BoardView.Boundaries boardBoundaries = Helper.getBoundaries();

        Random rand = new Random();
        int num = rand.nextInt(2);

        // reverse direction of ball
//        if (Math.abs(getPosY() - topY) < getSize() || Math.abs(getPosY() + getSize() - botY) < getSize()){
//            reverseY();
//        }

        float actualHeight = boardBoundaries.boardBottom - boardBoundaries.boardTop;

        // moving up
        if (dir[1] == -1){
            if (num == 1){
                return boardBoundaries.boardTop;
            } else {
//                return actualHeight / 2;
                return boardBoundaries.boardTop;
            }

        } else {
            if (num == 1){
                return boardBoundaries.boardBottom - getSize();
            } else {
//                return actualHeight / 2;
                return boardBoundaries.boardBottom  - getSize();
            }
        }
    }

    public void setNewEndY(float start){

        animatorY.end();

        Helper.setupAnimatorVals(animatorY, start + 1, getEndY());
        setAnimatorTimeUsingSpeed(animatorY, start, getEndY());
        animatorY.start();

        // TODO - Implement bouncing off the two end points - remove Y hit griditem (i think already being done)

    }


    /*
    *
    * Helper Methods
    *
    * */

    public void pause(){
        animatorX.pause();
        animatorY.pause();
    }

    public void unpause(){
        animatorX.resume();
        animatorY.resume();
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
        animatorX = ValueAnimator.ofFloat(getPosX(), endPoint);

        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // On Update, set the X position of Ball
                float animatedVal = (float) animatorX.getAnimatedValue();
                setPosX(animatedVal);
                board.isHit(animatedVal, getPosY(), size, ball, getContext());

                if((animatedVal <= SCREEN_BOUNCE_THRESHOLD) && (dir[0] == -1)){
                    // means we are at the start of the screen
                    reverseX();
                    setNewEndX(animatedVal);
                }

                DisplayMetrics metrics = Helper.getDisplayMetrics(context);

                if((animatedVal + getSize() >= metrics.widthPixels - SCREEN_BOUNCE_THRESHOLD) && (dir[0] == 1)){
                    // means we are at the start of the screen
                    reverseX();
                    setNewEndX(animatedVal);
                }

            }
        });

        // setup what happens when animation starts over
        animatorX.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                // done
            }

        });

        setAnimatorTimeUsingSpeed(animatorX, getPosX(), endPoint);
        animatorX.setInterpolator(new LinearInterpolator());
        animatorX.setRepeatCount(ValueAnimator.INFINITE);
        animatorX.start();
    }

    public void addYAnimator(final float topY, final float botY){

        final Ball ball = this;

        final Context context = getContext();
        DisplayMetrics metrics = Helper.getDisplayMetrics(context);
        // subtract size cus endingFloat is supposed to be top left corner
        float endPoint = botY - getSize();
        animatorY = ValueAnimator.ofFloat(getPosY(), endPoint);

        // setup initial animator listener
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animatorY.getAnimatedValue();
                setPosY(animatedVal);
                board.isHit(getPosX(), animatedVal, size, ball, getContext());

                BoardView.Boundaries boundaries = Helper.getBoundaries();

                if((animatedVal <= (boundaries.boardTop + SCREEN_BOUNCE_THRESHOLD) ) && (dir[1] == -1)){
                    // means we are at the start of the screen
                    reverseY();
                    setNewEndY(animatedVal);
                }


                if((animatedVal + getSize() >= boundaries.boardBottom - SCREEN_BOUNCE_THRESHOLD) && (dir[1] == 1)){
                    // means we are at the start of the screen
                    reverseY();
                    setNewEndY(animatedVal);
                }
            }
        });

        // setup what happens when animation starts over
        animatorY.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                // done

            }

        });
        setAnimatorTimeUsingSpeed(animatorY, getPosY(), endPoint);
        animatorY.setInterpolator(new LinearInterpolator());
        animatorY.setRepeatCount(ValueAnimator.INFINITE);
        animatorY.start();
    }

    // speed property is used to set the duration of the animation.
    private void setAnimatorTimeUsingSpeed(ValueAnimator animator, float start, float end){

        // calculate the time of the animation
        // get the distance that needs to be moved from the animator
        float distance = Math.abs(end - start);
        int ms = (int) (distance / getSpeed());
        animator.setDuration(ms);
    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = Helper.getGameState();

        switch(state) {
            case PLAY:
                this.setVisibility(View.VISIBLE);
                unpause();
                break;
            case BUILD:
                this.setVisibility(View.GONE); // should instead pause the ball, visibility should be VISIBLE
            case PAUSE:
                pause();
                break;
            case END:
                this.setVisibility(View.GONE);
                break;
            default:
                this.setVisibility(View.GONE);
        }
    }
}
