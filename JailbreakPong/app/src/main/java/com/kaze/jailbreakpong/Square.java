package com.kaze.jailbreakpong;

import android.graphics.Canvas;

public class Square extends Brick {
    // HP = 1

    public Square(Canvas canvas, int row, int column, int width, int height) {
        super(canvas, row, column, width, height, 1);
    }

    public void hit() {
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
    }
}
