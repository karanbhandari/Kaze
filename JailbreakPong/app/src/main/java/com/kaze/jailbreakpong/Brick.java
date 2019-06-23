package com.kaze.jailbreakpong;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.widget.ImageView;

public abstract class Brick {

    protected RectF rect;
//    protected ImageView rect;

    protected boolean isVisible;
    protected int hp;
    protected Canvas canvas;
    protected int row;
    protected int column;
    protected int width;
    protected int height;
    private int opacity;


    // this shoudl be created as a base class and the triangles and square could be the child classes

    protected Brick(Canvas canvas, int row, int column, int width, int height, int hp){

        isVisible = true;
        this.hp = hp;

        int padding = 1;

        this.canvas = canvas;
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;

//        rect = new RectF(column * width + padding,
//                row * height + padding,
//                column * width + width - padding,
//                row * height + height - padding);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public int getHp() {
        return hp;
    }


}