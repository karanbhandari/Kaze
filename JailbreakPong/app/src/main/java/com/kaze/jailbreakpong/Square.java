package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;

public class Square extends Brick {
    // HP = 1
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Square(Context context, int row, int column, int width, int height, int lightColor, int darkColor) {
        super(context, row, column, width, height,1, lightColor, darkColor);
        paint.setColor(this.lightColor);
        paint.setShader(new LinearGradient(row, column, row+width, column+width, darkColor, lightColor, Shader.TileMode.MIRROR));


    }

    public void hit() {
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(0, gapSize/2);

        Log.d("Square", "draw called ");

        int x = this.row;
        int y = this.column;

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
