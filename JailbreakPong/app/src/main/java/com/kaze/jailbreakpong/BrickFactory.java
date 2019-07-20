package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

public class BrickFactory {
    // creates new bricks

    GridItem item;

    public BrickFactory(Context context, int gridX, int gridY, int lightColor, int darkColor, String brickType, int gridItemSize) {
        if (brickType == "Square") {
            item = new Square(context, gridX, gridY, gridItemSize, gridItemSize, lightColor, darkColor);
        } else if (brickType == "Prison") {
            item = new Prison(context, gridX, gridY, gridItemSize, gridItemSize, lightColor, darkColor);
        }
    }

    public GridItem getItem() {
        return item;
    }
}
