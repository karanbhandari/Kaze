package com.kaze.jailbreakpong;
import android.graphics.RectF;

public class Brick {

    private RectF rect;

    private boolean isVisible;
    private int hp;

    // this shoudl be created as a base class and the triangles and square could be the child classes

    public Brick(int row, int column, int width, int height, int type){
        // type = 1:
        // type = 2:
        // type = 3:
        // type = 4:
        // type = 5: square

        isVisible = true;

        int padding = 1;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
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

    public void hit() {
        hp-=1;
    }

    public int getHp() {
        return hp;
    }


}