package com.kaze.jailbreakpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class UpperLeftTriangle extends Brick {

    // HP = 1
    public UpperLeftTriangle(int row, int column, int width, int height, int color) {
        super(row, column, width, height, 1, color);
    }

    public void hit() {
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
    }

    @Override
    public void draw(Canvas canvas) {

        int halfWidth = width / 2;

        Paint paint = new Paint();
        int x = this.row;
        int y = this.column;
        paint.setColor(this.color); // todo: have to change the this.color to a new value with a certain opacity


        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width, y );
        path.lineTo(x, y - height);
        path.lineTo(x, y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
