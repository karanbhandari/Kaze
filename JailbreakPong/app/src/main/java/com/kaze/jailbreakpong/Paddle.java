package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Observable;
import java.util.Observer;

public class Paddle extends AppCompatImageView implements Observer {

    int left;
    int screenWidth;
    int paddleWidth, paddleHeight;
    float dX = 0;
    View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float value = event.getRawX() + dX;

                    if(value <= 0) {
                        value = 0;
                    }
                    if(value >= screenWidth - paddleWidth ) {
                        value = screenWidth - paddleWidth;
                    }

                    left = (int)value;

                    view.animate()
                            .x(value)
                            .setDuration(0)
                            .start();
                    break;
                default:
                    return false;
            }

            return true;
        }

    };
    public Paddle(Context context, float x, float y, int imgId) {
        super(context);
        init(x, y, imgId);
    }

    private void init(float x, float y, int imgId) {
        this.setOnTouchListener(touchListener);
        this.setImageResource(imgId);
        this.left = (int) x;
        this.screenWidth = Helper.getDisplayMetrics(getContext()).widthPixels;
        int gridItemSize = (int) Helper.getGridItemSize();
        this.paddleWidth = (int) Helper.getGridItemSize() * 3;
        this.paddleHeight = (int) (gridItemSize *0.75);
        this.setLayoutParams(new LinearLayout.LayoutParams(paddleWidth, paddleHeight));
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = paddleHeight;
        params.width = paddleWidth;
        this.setLayoutParams(params);
        this.setScaleType(ScaleType.FIT_XY);

        this.setX(x);
        this.setY(y - (int) ((float) params.height/2));
        Helper.addObserver(this);
    }

    // TODO: also need to consider the 4 corners of the ball
//    public boolean isHit(float x, float y, float size) {
//        float topBound = y - (float) paddleHeight/2;
//        float bottomBound = y + (float) paddleHeight/2;
//        float leftBound = x;
//        float rightBound = x + paddleWidth;
//
//        return (leftBound <= x && x <= rightBound) && (topBound <= y && y <= bottomBound);
//    }

    public boolean isTouching(View v){
        Rect R1=new Rect((int)this.getX(), (int)this.getY(), (int)this.getX() + this.getWidth(), (int)this.getTop() - this.getHeight());
        Rect R2=new Rect((int)v.getX(), (int)v.getY(), (int)v.getX() + v.getWidth(), (int)v.getTop() - v.getHeight());
        return R1.intersect(R2);
    }
    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }

    public void checkHit(Ball ball, float newY){

        // need to check if ball.getPosX is within my own x ... x + width
        // if yes then check if ball.getPosY is within my own y ... y + height
        // if yes then reverse Y direction
        // set the start point to be my own Y coordinate, or even the animatedVal = ball.getPosY(). TRY

        if (ball.getPosX() >= left && ball.getPosX() <= (left + paddleWidth)){

            float y = this.getY();
            int[] dir = ball.getDir();

            if (dir[1] == 1){
                // ball going down
//                if (newY + ball.getSize() >= y && newY + ball.getSize() <= (y + paddleHeight)){
                if (newY + ball.getSize() >= y && newY + ball.getSize() <= (y + paddleHeight)){

                    ball.reverseY();
                    ball.setNewEndY(y - ball.getSize());
                    ball.speed += 0.1;
                }

            } else if (dir[1] == -1){

                if (ball.getPosY() >= y && ball.getPosY() <= (y + paddleHeight)){
                    ball.reverseY();
                    ball.setNewEndY(y + paddleHeight);
                    ball.speed += 0.1;
                }

            }

        }

    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = Helper.getGameState();

        switch(state) {
            case PLAY:
                this.setVisibility(View.VISIBLE);
                break;
            case PAUSE:
                this.setVisibility(View.GONE); // should instead disable touch events on the board, visibility should be VISIBLE
                break;
            case END:
                this.setVisibility(View.GONE);
                break;
            default:
                this.setVisibility(View.GONE);
        }
    }
}
