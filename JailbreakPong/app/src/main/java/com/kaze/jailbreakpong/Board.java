package com.kaze.jailbreakpong;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.core.content.res.ResourcesCompat;

import static java.lang.Math.ceil;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Board extends Observable {
    private static Board board = new Board();     // singleton, only one Board allowed per game.
    private int numRows, numCols;   // number of GridItems horizontally and vertically on screen
    private int playerRows, neutralRows;   // number of rows per player, and number of neutral ones
    private int playerScore, opponentScore;     // score
    private int playerNumPrisons, opponentNumPrisons;
    private boolean isRecording = false;
    private State state;
    private BoardView boardView;
    private float gridItemSize;     // in px, the width/height of each square grid

    private ArrayList<ArrayList<GridItem>> grid = new ArrayList<>(); // 2D array of GridItems, same dimension as board

    private Board() {}

    public enum State { START, BUILD, PAUSE, PLAY, END; }

    // during runtime, MainActivity tells us the screen dimensions in pixels, and the dpi
    public void init(Context context) {
        state = State.BUILD;
        // square grid system that follows the 16:9 ratio
        numRows = 21; // default is 21 GridItems vertically across
        numCols = 12; // default is 12 GridItems horizontally across
        float screenWidth = Helper.getDisplayMetrics(context).widthPixels;
        gridItemSize = screenWidth/numCols;

        // split the board evenly between the player, opponent, and neutral sections
        playerRows = (int) ceil((float) numRows/3);
        neutralRows = numRows - playerRows * 2;

        playerScore = 0;
        opponentScore = 0;

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

    public State getState() {
        return state;
    }

    public int getNumPlayerRows() {
        return playerRows;
    }

    public int getNumNeutralRows() {
        return neutralRows;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void addBoardView(BoardView boardView) {
        this.boardView = boardView;
    }

    public BoardView.Boundaries getBoundaries() {
        if (boardView == null) return null;
        return boardView.getBoundaries();
    }

    public ArrayList<ArrayList<GridItem>> getGrid() {
        return grid;
    }

    public boolean getIsRecording() {
        return isRecording;
    }

    public void build(BuildingView.Selected selection, float x, float y) {
        if (selection == null) return;
        int coordinate[] = translateToCoordinate(x, y);

        // range of rows where no building is allowed
        int unallowedTop = playerRows - 2;
        int unallowedBottom = playerRows + neutralRows + 1;

        if (coordinate[1] > unallowedTop && coordinate[1] < unallowedBottom) {
            return;
        }

        GridItem item = grid.get(coordinate[1]).get(coordinate[0]);
        item.replace(selection);
    }

    public void add(BuildingView.Selected selection, int row, int column) {
        Context context = boardView.getContext();
        Resources res = boardView.getResources();
        int tileColorLight = -1;
        int tileColorDark = -1;

        if (selection == BuildingView.Selected.BRICK) {
            if (row <= playerRows) {
                tileColorLight = ResourcesCompat.getColor(res, R.color.gradientYellowLight, null);
                tileColorDark = ResourcesCompat.getColor(res, R.color.gradientYellowDark, null);
            } else {
                tileColorLight = ResourcesCompat.getColor(res, R.color.gradientBlueLight, null);
                tileColorDark = ResourcesCompat.getColor(res, R.color.gradientBlueDark, null);
            }
        } else if (selection == BuildingView.Selected.PRISON) {
            if (row <= playerRows) {
                tileColorLight = ResourcesCompat.getColor(res, R.color.jailYellow, null);
                tileColorDark = ResourcesCompat.getColor(res, R.color.gradientYellowDark, null);
                opponentNumPrisons += 1;
            } else {
                tileColorLight = ResourcesCompat.getColor(res, R.color.jailBlue, null);
                tileColorDark = ResourcesCompat.getColor(res, R.color.gradientBlueDark, null);
                playerNumPrisons += 1;
            }
        }

        String brickType = "";
        if (selection == BuildingView.Selected.BRICK) {
            brickType = "Square";
        } else if (selection == BuildingView.Selected.PRISON) {
            brickType = "Prison";
        }

        BrickFactory bf= new BrickFactory(context, column, row, tileColorLight, tileColorDark, brickType, (int) gridItemSize);
        grid.get(row).set(column, bf.getItem());
        boardView.addView(bf.getItem());
    }

    public void removePrison(int row, int column) {
        if (state == State.PLAY || state == State.BUILD) {
            if (row <= playerRows) {
                opponentNumPrisons -= 1;
            } else {
                playerNumPrisons -= 1;
            }
        }

        remove(row, column);
    }

    public void remove(int row, int column) {
        if (state == State.PLAY) {
            GridItem hitObj = grid.get(row).get(column);
            if (row <= playerRows) {
                playerScore += hitObj.getScore();
            } else {
                opponentScore += hitObj.getScore();
            }

            if (opponentNumPrisons == 0 || playerNumPrisons == 0) {
                state = State.END;
            }

            setChanged();
            notifyObservers();
        }

        GridItem replacementBlank = new GridItem(boardView.getContext(), row, column);
        grid.get(row).set(column, replacementBlank);
    }

    public void randomPrisonAdd() {
        int rowsOpponent = playerRows - 1;                  // minus 1 row for opponent paddle clearance
        int rowsMiddle = rowsOpponent + 1 + neutralRows + 1;    // add 1 row for player paddle clearance
        int rowsPlayer = numRows;
        Random rand = new Random();

        if (opponentNumPrisons < 1) {
            int row = rand.nextInt(rowsOpponent);
            int col = rand.nextInt(getNumColumns());
            add(BuildingView.Selected.PRISON, row, col);
        }

        if (playerNumPrisons < 1) {
            int row = rand.nextInt(rowsPlayer - rowsMiddle) + rowsMiddle;
            int col = rand.nextInt(getNumColumns());
            add(BuildingView.Selected.PRISON, row, col);
        }
    }

    public int[] getNumPrisonsPerPlayer() {
        int[] numPrisons = {opponentNumPrisons, playerNumPrisons};
        return numPrisons;
    }

    public void initBoard(FrameLayout fl, Context context, int playerTileColorLight, int playerTileColorDark, int opponentTileColorLight, int opponentTileColorDark) {
        int rowsOpponent = playerRows - 1;                  // minus 1 row for opponent paddle clearance
        int rowsMiddle = rowsOpponent + 1 + neutralRows + 1;    // add 1 row for player paddle clearance
        int rowsPlayer = numRows;

        String brickTypes[] = {"LowerLeftTriangle", "LowerRightTriangle", "UpperLeftTriangle", "UpperRightTriangle", "Square"};
        Random rand = new Random();

        int row = 0;
        int col = 11;

        BrickFactory bf= new BrickFactory(context, col, row, opponentTileColorLight, opponentTileColorDark, "Square", (int) gridItemSize);
        grid.get(row).set(col, bf.getItem());
        fl.addView(bf.getItem());

//        for(int or = 0; or <  10; ++or) {
//            int row = rand.nextInt(rowsOpponent);
//            int col = rand.nextInt(getNumColumns());
//            int brickType = rand.nextInt(5);
//
//            BrickFactory bf= new BrickFactory(context, col, row, opponentTileColorLight, opponentTileColorDark, "Square", (int) gridItemSize);
//            grid.get(row).set(col, bf.getItem());
//            fl.addView(bf.getItem());
//        }

//        for(int pr = 0; pr <  10; ++pr) {
//            int row = rand.nextInt(rowsPlayer - rowsMiddle) + rowsMiddle;
//            int col = rand.nextInt(getNumColumns());
//            int brickType = rand.nextInt(5);
//
//            BrickFactory bf = new BrickFactory(context, col, row, playerTileColorLight, playerTileColorDark, "Square", (int) gridItemSize);
//            grid.get(row).set(col, bf.getItem());
//            fl.addView(bf.getItem());
//        }
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
        float topOffset = getBoundaries().boardTop;
        int x = (int) Math.floor((pxX)/gridItemSize);
        int y = (int) Math.floor((pxY-topOffset)/gridItemSize);

        int coordinate[] = {x, y};
        return coordinate;
    }

    public boolean isHit(float pxX, float pxY, float size, Ball ball) {
        Log.d("BOARD", "isHit: called with pxX: " + pxX + " and pxY: " + pxY);
        ArrayList<int[]> boundaries = new ArrayList<int[]>();
        // worst case scenario, the ball is simultaneously on 4 gridItems
        boundaries.add(translateToCoordinate(pxX, pxY));
        boundaries.add(translateToCoordinate(pxX+size, pxY));
        boundaries.add(translateToCoordinate(pxX, pxY+size));
        boundaries.add(translateToCoordinate(pxX+size, pxY+size));

        boolean hasHit = false;
        ArrayList<int[]> visitedCoordinates = new ArrayList<int[]>();

        for (int i = 0; i < 4; ++i) {
            int[] coordinate = boundaries.get(i);
            if (coordinate[1] < grid.size() && coordinate[0] < grid.size()){  // to prevent out of bounds calls
                Log.d("BOARD", "isHit: called from within if with coordinate[1]: " + coordinate[1] + " and coordinate[0]: " + coordinate[0]);
                GridItem affectedGridItem = grid.get(coordinate[1]).get(coordinate[0]);
                if (!visitedCoordinates.contains(affectedGridItem.getPosition())) {
                    boolean localHasHit = affectedGridItem.onHit(boundaries);   // TODO - eric
                    affectedGridItem.hasHit(coordinate, ball);
                    visitedCoordinates.add(coordinate);

                    if (localHasHit) hasHit = true;
                }

            }
        }

        return hasHit;
    }

    public void onDoneBuild(boolean isDone) {
        boardView.onDoneBuild(isDone);
    }

    public void play() {
        setChanged();
        state = State.PLAY;
        notifyObservers();
    }

    public void pause() {
        setChanged();
        state = State.PAUSE;
        notifyObservers();
    }

    public void togglePlayPause() {
        if (state == State.PAUSE) {
            play();
        } else if (state == State.PLAY) {
            pause();
        }
    }

    public void restart() {
        state = State.BUILD;
        playerScore = 0;
        opponentScore = 0;
        setChanged();
        notifyObservers();
    }

    public void endRecording() {
        isRecording = false;
        setChanged();
        notifyObservers();
    }

    public void startRecording() {
        isRecording = true;
        setChanged();
        notifyObservers();
    }

    public void toggleRecord() {
        if (isRecording) {
            endRecording();
        } else {
            startRecording();
        }
    }

    // Observer methods
    // ===================
    public void initObservers() {
        setChanged();
        notifyObservers();
    }
    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }
    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }
    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }
    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}