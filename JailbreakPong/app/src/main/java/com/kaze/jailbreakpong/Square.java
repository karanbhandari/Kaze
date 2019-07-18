package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Square extends Brick {
    // HP = 1
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Square(Context context, int row, int column, int width, int height, int lightColor, int darkColor) {
        super(context, row, column, width, height,1, lightColor, darkColor);
        paint.setColor(this.lightColor);
        paint.setShader(new LinearGradient(row, column, row+width, column+width, darkColor, lightColor, Shader.TileMode.MIRROR));
    }

    public void replace(BuildingView.Selected selection) {
        int gridRow = (int) column/width;
        int gridColumn = (int) row/height;
        if (selection == BuildingView.Selected.BRICK) {
            Helper.remove(gridRow, gridColumn);
        } else if (selection == BuildingView.Selected.PRISON) {
            Helper.add(selection, gridRow, gridColumn);
        }
        if (this.getParent() != null) ((ViewGroup) this.getParent()).removeView(this);
    }

    public void hit() {
        int gridRow = (int) column/width;
        int gridColumn = (int) row/height;
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
        if (this.hp == 0) {
            Helper.remove(gridRow, gridColumn);
            if (this.getParent() != null) ((ViewGroup) this.getParent()).removeView(this);
        }
        invalidate();   // force a redraw
    }

    @Override
    public boolean onHit(ArrayList<int[]> boundaries) {
        // do nothing for blank item
        // children classes overwrite this function.
        Log.d("SQUARE", "onHit: called from within Square class");

        // TODO:
        //  - this will eventually call the hit method()
        //  - also needs a reference to the Ball to reverse direction. Or maybe board??
        return false;
    }

    @Override
    public void hasHit(int [] coordinates){
        Log.d("SQUARE", "hasHit: called with x: " + coordinates[0] + " and y: " + coordinates[1]);
        hit();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(0, gapSize/2);

        Log.d("Square", "draw called ");

        int x = this.row;
        int y = this.column;

        if(hp <= 0){
            // not draw
            Log.d("SQUARE", "onDraw: called with " + hp + " hp");
            canvas.save();
            canvas.restore();
        } else {
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(x + width, y);
            path.lineTo(x + width, y + height);
            path.lineTo(x, y + height);
            path.lineTo(x, y);
            path.close();
            canvas.drawPath(path, paint);
            canvas.save();
            canvas.restore();
        }

    }
}
