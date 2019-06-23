package com.kaze.jailbreakpong;
import android.graphics.Canvas;

import java.util.ArrayList;

public class Board {
    private static Board board = new Board(); // singleton, only one Board allowed per game.
    private int freed, escaped; // score
    private float pxHeight, pxWidth, dpi, gap; // screen dimensions
    private float gridItemSize; // in px, the width/height of each square grid
    private int nRows, nCols; // number of GridItems horizontally and vertically on screen
    private ArrayList<ArrayList<GridItem>> grid = new ArrayList<>();

    private Board() {}

    // during runtime, MainActivity tells us the screen dimensions in pixels, and the dpi
    public void init(float dimX, float dimY, float density) {
        pxWidth = dimX;
        pxHeight = dimY;
        dpi = density;  // need for drawing in pixels
        nCols = 27; // default is 27 GridItems horizontally across
        nRows = 48; // default is 48 GridItems vertically across
        gridItemSize = dimX/nCols; // the px width of each GridItem
        gap = pxHeight - nRows * gridItemSize;

        for(int col = 0; col < nCols; col++) {
            ArrayList<GridItem> tempArray = new ArrayList<>();
            for ( int row = 0; row < nRows; row++) {
                tempArray.add(new GridItem(row, col));
            }
            grid.add(tempArray);
        }
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

    public float getGridItemSize() {
        return gridItemSize;
    }

    public float getGapSize() {
        return gap;
    }

    public ArrayList<ArrayList<GridItem>> getGrid() {
        return grid;
    }

    public int getFreed() {
        return freed;
    }

    public int getEscaped() {
        return escaped;
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

    public void initBoard(int color) {
        BrickFactory bf= new BrickFactory(23, 23, color, "LowerLeftTriangle", (int) gridItemSize);
//        for(int r = 0; r < nRows; r++) {
//            for ( int c = 0; c < nCols; c++) {
//                if( r == 23 && c == 23) {
//                    grid[r][c] = bf;
//                }
//            }
//        }
        grid.get(23).set(23, bf.getItem());

    }
}