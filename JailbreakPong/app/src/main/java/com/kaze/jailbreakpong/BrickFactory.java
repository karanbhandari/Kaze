package com.kaze.jailbreakpong;

import android.graphics.Canvas;
import android.graphics.Color;

public class BrickFactory {
    // creates new bricks

    GridItem item;

    public BrickFactory(int gridX, int gridY, int color, String brickType, int gridItemSize) {
        if (brickType == "LowerLeftTriangle") {
            item = new LowerLeftTriangle(gridX, gridY, gridItemSize, gridItemSize, color);
        } else if(brickType == "LowerRightTriangle") {
            item = new LowerRightTriangle(gridX, gridY, gridItemSize, gridItemSize, color);
        } else if(brickType == "UpperLeftTriangle") {
            item = new UpperLeftTriangle(gridX, gridY, gridItemSize, gridItemSize, color);
        } else if(brickType == "UpperRightTriangle") {
            item = new UpperRightTriangle(gridX, gridY, gridItemSize, gridItemSize, color);
        } else if(brickType == "Square") {
            item = new Square(gridX, gridY, gridItemSize, gridItemSize, color);
        }
    }

    public GridItem getItem() {
        return item;
    }
}
