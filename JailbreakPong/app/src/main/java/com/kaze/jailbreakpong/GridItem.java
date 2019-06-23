package com.kaze.jailbreakpong;

import android.graphics.Canvas;

public class GridItem {
    private int row, column;   // position of GridItem in our Board object
    private boolean isVisible;

    public GridItem(int rowPos, int columnPos) {
        row = rowPos;
        column = columnPos;
        isVisible = false;
    }

    public GridItem(int row, int column, boolean isVisible) {
        row = row;
        column = column;
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

    public void draw(Canvas canvas) {
        // do nothing for blank item
        // children classes overwrite this function
    }
}