package com.kaze.jailbreakpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class UpperRightTriangle extends Brick {
    // HP = 1

    public UpperRightTriangle(Canvas canvas, int row, int column, int width, int height) {
        super(canvas, row, column, width, height, 1);
    }

    public void hit() {
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
    }

    public void draw(Canvas canvas, Color color) {

        int halfWidth = width / 2;

        Paint paint = new Paint();
        int x = this.row;
        int y = this.column;
        paint.setColor(/*Some color here that is passed along with the opacity value*/);


        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + width, y);
        path.lineTo(x, y - width);
        path.lineTo(x, y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
