package com.kaze.jailbreakpong;

import android.graphics.Canvas;
import android.graphics.Color;

public class BrickFactory {
    // creates new bricks

    public BrickFactory(Canvas canvas, int gridX, int gridY, Color color, String brickType) {
        if (brickType == "LowerLeftTriangle") {
            new LowerLeftTriangle(canvas, gridX, gridY, 1, 1);
        } else if(brickType == "LowerRightTriangle") {
            new LowerRightTriangle(canvas, gridX, gridY, 1, 1);
        } else if(brickType == "UpperLeftTriangle") {
            new UpperLeftTriangle(canvas, gridX, gridY, 1, 1);
        } else if(brickType == "UpperRightTriangle") {
            new UpperRightTriangle(canvas, gridX, gridY, 1, 1);
        } else if(brickType == "Square") {
            new Square(canvas, gridX, gridY, 1, 1);
        }
    }
}
