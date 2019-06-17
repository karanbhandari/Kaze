package com.kaze.jailbreakpong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

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

//        this.posX = (float) Resources.getSystem().getDisplayMetrics().widthPixels;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.speed = speed;

        rect = new RectF(posX, posY, posX + (float) size, posY + (float) size);
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

    public int getDir1(){
        return dir[0];
    }

    public void setDir() {
        dir[0] = -1 * dir[0];
    }


    /*
    *   Move
    */

    public void move(){

        this.posX = posX + ( (float) 0.1 * speed * dir[0] );
        this.posY = posY + ( (float)  0.1 * speed  * dir[1] );

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("BALL", "onDraw() called");
        canvas.drawOval(rect, paint);
        invalidate();
    }

    public void reverseDirections(){
        dir[0] = -1 * dir[0];
        dir[1] = -1 * dir[1];
    }

    public float getEndPoint(){

        if (dir[0] == -1){

            return min(0f, -posX);

        } else {

            float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            return screenWidth;

        }

    }


}
