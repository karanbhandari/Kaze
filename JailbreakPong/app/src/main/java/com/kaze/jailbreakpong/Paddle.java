package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


public class Paddle extends AppCompatImageView {

    int left, top,right,bottom;
    View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float dX = 0;
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    dX = view.getX() - event.getRawX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float value = event.getRawX() + dX;
                    if(value > 800){
                        value = 770;
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
        this.setX(x);
        this.setY(y);
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

}
