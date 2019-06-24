package com.kaze.jailbreakpong;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.round;

public class Board {
    private static Board board = new Board(); // singleton, only one Board allowed per game.
    private int freed, escaped; // score
    private float pxHeight, pxWidth, dpi, gap; // screen dimensions
    private float gridItemSize; // in px, the width/height of each square grid
    private int nRows, nCols; // number of GridItems horizontally and vertically on screen
    private int gapBtm, oppBtm, midBtm, playerBtm, sectionHeight, midHeight;
    private ArrayList<ArrayList<GridItem>> grid = new ArrayList<>();

    private Board() {}

    // during runtime, MainActivity tells us the screen dimensions in pixels, and the dpi
    public void init(Context context, float dimX, float dimY, float density) {
        pxWidth = dimX;
        pxHeight = dimY;
        dpi = density;  // need for drawing in pixels
        nCols = 12; // default is 18 GridItems horizontally across
        nRows = 21; // default is 32 GridItems vertically across
        gridItemSize = dimX/nCols; // the px width of each GridItem
        gap = pxHeight - nRows * gridItemSize;

        int plyNRows = (int) ceil((float) nRows/3);
        sectionHeight = (int) (plyNRows * gridItemSize);
        int midNRows = nRows - plyNRows * 2;
        midHeight = (int) (midNRows * gridItemSize);

        gapBtm = round(gap/2);
        oppBtm = gapBtm + sectionHeight;
        midBtm = oppBtm + midHeight;
        playerBtm = midBtm + sectionHeight;

        for(int row = 0; row < nRows; row++) {
            ArrayList<GridItem> tempArray = new ArrayList<>();
            for ( int col = 0; col < nCols; col++) {
                tempArray.add(new GridItem(context, row, col));
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

    public float getGapBtm() {
        return gapBtm;
    }

    public float getOppBtm() {
        return oppBtm;
    }

    public float getMidBtm() {
        return midBtm;
    }

    public float getPlayerBtm() {
        return playerBtm;
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

    public void initBoard(FrameLayout fl, Context context, int playerTileColorLight, int playerTileColorDark, int opponentTileColorLight, int opponentTileColorDark) {
        int rowsOpponent = (int) ((getOppBtm()-getGapBtm())/getGridItemSize());
        int rowsMiddle = (int) ((getMidBtm()-getGapBtm())/getGridItemSize());
        int rowsPlayer = (int) ((getPlayerBtm()-getGapBtm())/getGridItemSize());
        String brickTypes[] = {"LowerLeftTriangle", "LowerRightTriangle", "UpperLeftTriangle", "UpperRightTriangle", "Square"};
        Random rand = new Random(1234);

        for(int or = 0; or <  10; ++or) {
            int row = rand.nextInt(rowsOpponent-1);
            int col = rand.nextInt(getNumColumns());
            int brickType = rand.nextInt(5);

            BrickFactory bf= new BrickFactory(context, col, row, opponentTileColorLight, opponentTileColorDark, "Square", (int) gridItemSize);
            grid.get(row).set(col, bf.getItem());
            fl.addView(bf.getItem());
        }

        for(int pr = 0; pr <  10; ++pr) {
            int row = rand.nextInt(rowsPlayer - (rowsMiddle+1)) + rowsMiddle+1;
            int col = rand.nextInt(getNumColumns());
            int brickType = rand.nextInt(5);

            BrickFactory bf = new BrickFactory(context, col, row, playerTileColorLight, playerTileColorDark, "Square", (int) gridItemSize);
            grid.get(row).set(col, bf.getItem());
            fl.addView(bf.getItem());
        }
//        int oppCoordinates[][] = {{0, 4}, {1, 4}, {2, 4}, {2, 5}, {2, 6}, {2, 7}, {1, 7}, {0, 7}};
//        int plyCoordinates[][] = {{20, 4}, {19, 4}, {18, 4}, {18, 5}, {18, 6}, {18, 7}, {19, 7}, {20, 7}};
//
//        for (int i = 0; i < 8; ++i) {
//            int row = oppCoordinates[i][0];
//            int col = oppCoordinates[i][1];
//
//            BrickFactory bf= new BrickFactory(context, col, row, opponentTileColorLight, opponentTileColorDark, "Square", (int) gridItemSize);
//            grid.get(row).set(col, bf.getItem());
//            fl.addView(bf.getItem());
//        }
//
//        for (int i = 0; i < 8; ++i) {
//            int row = plyCoordinates[i][0];
//            int col = plyCoordinates[i][1];
//
//            BrickFactory bf= new BrickFactory(context, col, row, playerTileColorLight, playerTileColorDark, "Square", (int) gridItemSize);
//            grid.get(row).set(col, bf.getItem());
//            fl.addView(bf.getItem());
//        }

    }
}