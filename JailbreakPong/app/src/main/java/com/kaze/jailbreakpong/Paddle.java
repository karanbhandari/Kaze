package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Observable;
import java.util.Observer;

public class Paddle extends AppCompatImageView implements Observer {

    int left;
    int screenWidth;
    int paddleWidth;
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
    public Paddle(Context context,float x, float y) {
        super(context);
        this.setOnTouchListener(touchListener);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setImageResource(R.drawable.paddle);
        this.left = (int) x;
        this.screenWidth = Helper.getDisplayMetrics(getContext()).widthPixels;
        this.paddleWidth = (int) Helper.getGridItemSize() * 4;
        this.setX(x);
        this.setY(y);
        Helper.addObserver(this);
    }

    public Paddle(Context context, AttributeSet attrs,float x, float y) {
        super(context, attrs);
        this.setOnTouchListener(touchListener);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setImageResource(R.drawable.paddle);
        this.setX(x);
        this.setY(y);
    }

    public Paddle(Context context, AttributeSet attrs, int defStyleAttr,float x, float y) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(touchListener);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setImageResource(R.drawable.paddle);
        this.setX(x);
        this.setY(y);
    }

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
            default:
                this.setVisibility(View.GONE);
        }
    }
}
