package com.kaze.jailbreakpong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.ImageView;

public class UpperLeftTriangle extends Brick {

    // HP = 1
    private RectF upper_left;



    public UpperLeftTriangle(Canvas canvas, int row, int column, int width, int height) {

        super(canvas, row, column, width, height, 1);
//        ImageView upper_left = new ImageView();
//        upper_left.setImageResource(R.drawable.top_left);

        this.rect = upper_left;
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
        path.lineTo(x, y + height);
        path.lineTo(x + width, y + height);
        path.lineTo(x, y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
