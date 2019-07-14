package com.kaze.jailbreakpong;
import android.content.Context;
import android.widget.FrameLayout;

import static java.lang.Math.ceil;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Random;

public class Board {
    private static Board board = new Board();     // singleton, only one Board allowed per game.
    private float gridItemSize;     // in px, the width/height of each square grid
    private int numRows, numCols;   // number of GridItems horizontally and vertically on screen
    private int playerRows, neutralRows;   // number of rows per player, and number of neutral ones
    private float verticalOffset;   // vertical offset in pixels required to center board
    private int freed, escaped;     // score
    private ArrayList<ArrayList<GridItem>> grid = new ArrayList<>(); // 2D array of GridItems, same dimension as board

    private Board() {}

    public class Boundaries {
        float boardTop, playerTop, opponentTop, boardBottom;
        public Boundaries(float boardTop, float opponentTop, float playerTop, float boardBottom) {
            this.boardTop = boardTop;
            this.opponentTop = opponentTop;
            this.playerTop = playerTop;
            this.boardBottom = boardBottom;
        }
    }

    // during runtime, MainActivity tells us the screen dimensions in pixels, and the dpi
    public void init(Context context) {
        // get current phone screen size
        float screenHeight = Helper.getDisplayMetrics(context).heightPixels + Helper.getNavbarHeight(context);
        float screenWidth = Helper.getDisplayMetrics(context).widthPixels;

        // square grid system that follows the 16:9 ratio
        numRows = 21; // default is 21 GridItems vertically across
        numCols = 12; // default is 12 GridItems horizontally across
        gridItemSize = screenWidth/numCols;

        // split the board evenly between the player, opponent, and neutral sections
        playerRows = (int) ceil((float) numRows/3);
        neutralRows = numRows - playerRows * 2;

        // vertically center the board
        // calculate the locations of each section boundary
        float verticalGap = screenHeight - numRows * gridItemSize;
        verticalOffset = round(verticalGap/2);

        // create array of GridItems, matching dimensions of board
        for(int row = 0; row < numRows; row++) {
            ArrayList<GridItem> tempArray = new ArrayList<>();
            for ( int col = 0; col < numCols; col++) {
                tempArray.add(new GridItem(context, row, col));
            }
            grid.add(tempArray);
        }
    }

    public static Board getInstance(){
        return board;
    }

    public int getNumColumns() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public float getGridItemSize() {
        return gridItemSize;
    }

    public Boundaries getBoardBoundaries() {
        float boardTop = verticalOffset;
        float opponentTop = playerRows * gridItemSize + verticalOffset;
        float playerTop = (playerRows + neutralRows) * gridItemSize + verticalOffset;
        float boardBottom = numRows * gridItemSize + verticalOffset;
        Boundaries boundaries = new Boundaries(boardTop, opponentTop, playerTop, boardBottom);

        return boundaries;
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

    public void initBoard(FrameLayout fl, Context context, int playerTileColorLight, int playerTileColorDark, int opponentTileColorLight, int opponentTileColorDark) {
        int rowsOpponent = playerRows - 1;                  // minus 1 row for opponent paddle clearance
        int rowsMiddle = rowsOpponent + 1 + neutralRows + 1;    // add 1 row for player paddle clearance
        int rowsPlayer = numRows;

        String brickTypes[] = {"LowerLeftTriangle", "LowerRightTriangle", "UpperLeftTriangle", "UpperRightTriangle", "Square"};
        Random rand = new Random(1234);

        for(int or = 0; or <  10; ++or) {
            int row = rand.nextInt(rowsOpponent);
            int col = rand.nextInt(getNumColumns());
            int brickType = rand.nextInt(5);

            BrickFactory bf= new BrickFactory(context, col, row, opponentTileColorLight, opponentTileColorDark, "Square", (int) gridItemSize);
            grid.get(row).set(col, bf.getItem());
            fl.addView(bf.getItem());
        }

        for(int pr = 0; pr <  10; ++pr) {
            int row = rand.nextInt(rowsPlayer - rowsMiddle) + rowsMiddle;
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

    private int[] translateToCoordinate(float pxX, float pxY) {
        int x = (int) Math.floor(pxX/gridItemSize);
        int y = (int) Math.floor(pxY/gridItemSize);

        int coordinate[] = {x, y};
        return coordinate;
    }

    public boolean isHit(float pxX, float pxY, float size) {
        ArrayList<int[]> boundaries = new ArrayList<int[]>();
        // worst case scenario, the ball is simultaneously on 4 gridItems
        boundaries.add(translateToCoordinate(pxX, pxY));
        boundaries.add(translateToCoordinate(pxX+size, pxY));
        boundaries.add(translateToCoordinate(pxX, pxY+size));
        boundaries.add(translateToCoordinate(pxX+size, pxY+size));

        boolean hasHit = false;
        ArrayList<int[]> hitCoordinates = new ArrayList<int[]>();

        for (int i = 0; i < 4; ++i) {
            int[] coordinate = boundaries.get(i);
            GridItem affectedGridItem = grid.get(coordinate[1]).get(coordinate[0]);
            if (!hitCoordinates.contains(affectedGridItem)) {
                hasHit = affectedGridItem.onHit(boundaries);
                hitCoordinates.add(coordinate);
            }
        }

        return hasHit;
    }
}