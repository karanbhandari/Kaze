package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GridItem extends View {
    private int row, column;   // position of GridItem in our Board object
    private boolean isVisible;

    public GridItem(Context context, int rowPos, int columnPos) {
        super(context);
        row = rowPos;
        column = columnPos;
        isVisible = false;
    }

    public GridItem(Context context, int rowPos, int columnPos, boolean isVisible) {
        super(context);
        row = rowPos;
        column = columnPos;
        isVisible = isVisible;
    }

    public int[] getPosition() {
        int[] pos = {row, column};
        return pos;
    }

    public int getRow(){
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setPosition(int row, int column) {
        row = row;
        column = column;
    }

    public void onHit() {
        // do nothing for blank item
        // children classes overwrite this function.
    }

    public void onDraw(Canvas canvas) {
        // do nothing for blank item
        // children classes overwrite this function
    }
}