package com.kaze.jailbreakpong;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class Brick extends GridItem{

    protected boolean isVisible;
    protected int hp;
    protected int row;
    protected int column;
    protected int width;
    protected int height;
    protected int gapSize;
    private int opacity;
    protected int lightColor; // TODO: need to be changed after a hit to a new value with lesser opacity
    protected int darkColor;


    // this shoudl be created as a base class and the triangles and square could be the child classes

    protected Brick(Context context, int row, int column, int width, int height, int hp, int lightColor, int darkColor){

        super(context, row, column, true);

        isVisible = true;
        this.hp = hp;
        this.gapSize = (int) Helper.getBoundaries().boardTop * 2;

        int padding = 1;

//        this.canvas = canvas;
        this.row = row * width;
        this.column = column * height;
        this.width = width;
        this.height = height;
        this.opacity = 1;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean isVisible(){
        return isVisible;
    }

    public int getHp() {
        return hp;
    }

}