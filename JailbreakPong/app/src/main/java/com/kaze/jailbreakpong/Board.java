package com.kaze.jailbreakpong;
import android.graphics.Canvas;

public class Board {
    private static Board board = new Board(); // singleton, only one Board allowed per game.
    private int freed, escaped; // score
    private float pxHeight, pxWidth, dpi; // screen dimensions
    private float gridItemSize; // in px, the width/height of each square grid
    private int nRows, nCols; // number of GridItems horizontally and vertically on screen
    private GridItem grid[][];

    private Board() {}

    // during runtime, MainActivity tells us the screen dimensions in pixels, and the dpi
    public void init(float dimX, float dimY, float density) {
        pxWidth = dimX;
        pxHeight = dimY;
        dpi = density;  // need for drawing in pixels
        nCols = 27; // default is 27 GridItems horizontally across
        nRows = 48; // default is 48 GridItems vertically across
        gridItemSize = dimX/nCols; // the px width of each GridItem

        grid = new GridItem[nRows][nCols];
    }
    public static Board getInstance(){
        return board;
    }

    public int getNumColumns() {
        return nCols;
    }

    public int getNumRows() {
        return nRows;
    }

    public int getFreed() {
        return freed;
    }

    public int getEscaped() {
        return escaped;
    }

    public float getGridItemSize() {
        return gridItemSize;
    }

    // when destroyed opponent's prisons, increase your score
    public void setFreed(int freed) {
        freed = freed;
    }

    // when opponent destroys your prisons, increase their score.
    public void setEscaped(int escaped) {
        escaped = escaped;
    }

    public float covertPxToDp(float px) {
        return px/dpi;
    }

    public float convertDpToPx(float dp) {
        return dp * dpi;
    }

    public void onDraw(Canvas canvas) {

    }

    public void drawGrid() {

    }
}
