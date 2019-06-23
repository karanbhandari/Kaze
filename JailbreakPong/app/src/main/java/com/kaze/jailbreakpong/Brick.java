package com.kaze.jailbreakpong;
import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class Brick extends GridItem{

    protected boolean isVisible;
    protected int hp;
    protected int row;
    protected int column;
    protected int width;
    protected int height;
    private int opacity;
    protected int color; // TODO: need to be changed after a hit to a new value with lesser opacity


    // this shoudl be created as a base class and the triangles and square could be the child classes

    protected Brick(int row, int column, int width, int height, int hp, int color){

        super(row, column, true);

        isVisible = true;
        this.hp = hp;

        int padding = 1;

//        this.canvas = canvas;
        this.row = row * width;
        this.column = column * height;
        this.width = width;
        this.height = height;
        this.opacity = 1;
        this.color = color;
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