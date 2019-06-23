package com.kaze.jailbreakpong;
import android.graphics.Canvas;

public final class Drawing {
    private int nRows, nCols; // number of GridItems horizontally and vertically on screen

    public Drawing(int dimX, int dimY) {
        nRows = dimY;
        nCols = dimX;
    }

    public void initDraw(GridItem griditems[][], Canvas canvas) {
        for(int row = 0; row < nRows; ++row) {
            for (int cols = 0; cols < nCols; ++cols){
                griditems[row][cols].draw(canvas);
            }
        }
    }

}