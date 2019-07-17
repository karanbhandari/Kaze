package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

import androidx.core.content.res.ResourcesCompat;

import java.util.Observable;
import java.util.Observer;

import static java.lang.Math.round;

public class BoardView extends FrameLayout implements Observer {
    private float boardTop, opponentTop, playerTop, boardBottom, sectionWidth, gridItemSize;
    int paleYellow, paleBlue, paleOrange, palePurple, white, darkYellow, darkBlue;
    RectF topGapRect, opponentRect, neutralRect, playerRect, bottomGapRect;
    LinearGradient neutralRectShader;
    Paint mPaint;
    Board board;
    boolean isGrid = false;
    GameControlView opponentHUD, playerHUD;

    public BoardView(Context context){
        super(context);
        this.setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        board = Board.getInstance();
        board.addObserver(this);

        // get board boundaries
        Board.Boundaries boundaries = Helper.getBoardBoundaries();
        boardTop = boundaries.boardTop;
        opponentTop = boundaries.opponentTop;
        playerTop = boundaries.playerTop;
        boardBottom = boundaries.boardBottom;
        gridItemSize = Helper.getGridItemSize();
        sectionWidth = gridItemSize * Helper.getNumColumns();

        opponentHUD = new GameControlView(getContext());
        playerHUD = new GameControlView(getContext());

        addView(opponentHUD);
        addView(playerHUD);

        opponentHUD.setRotation(180);
        opponentHUD.setGravity(Gravity.BOTTOM);
        opponentHUD.setY(boundaries.opponentTop);
        float screenMiddle = (boundaries.boardBottom-boundaries.boardTop)/2 + boundaries.boardTop;
        playerHUD.setY(screenMiddle);

        // initialize colors
        paleBlue = ResourcesCompat.getColor(getResources(), R.color.paleBlue, null);
        paleYellow = ResourcesCompat.getColor(getResources(), R.color.paleYellow, null);
        palePurple = ResourcesCompat.getColor(getResources(), R.color.palePurple, null);
        paleOrange = ResourcesCompat.getColor(getResources(), R.color.paleOrange, null);
        white = ResourcesCompat.getColor(getResources(), R.color.white, null);
        darkYellow = ResourcesCompat.getColor(getResources(), R.color.gapYellow, null);
        darkBlue = ResourcesCompat.getColor(getResources(), R.color.gapBlue, null);

        // if there is a vertical gap, then create gap rectangles to paint
        if (boardTop == 0) {
            topGapRect = null;
            bottomGapRect = null;
        } else {
            topGapRect = new RectF(0, 0, sectionWidth, boardTop);
            bottomGapRect = new RectF(0, boardBottom, sectionWidth, boardBottom+boardTop);
        }

        // create rectangles for each board section
        opponentRect = new RectF(0, boardTop, sectionWidth, opponentTop);
        neutralRect = new RectF(0, opponentTop, sectionWidth, playerTop);
        playerRect = new RectF(0, playerTop, sectionWidth, boardBottom);

        // shader object for neutral region
        neutralRectShader = new LinearGradient(0, opponentTop, 0, playerTop, palePurple, paleOrange, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // if the dimensions of our board do not match the ratio of the device, center
        if (topGapRect != null) {
            mPaint.setColor(darkYellow);
            canvas.drawRect(topGapRect, mPaint);
        }

        mPaint.setColor(paleYellow);
        canvas.drawRect(opponentRect, mPaint);

        mPaint.setShader(neutralRectShader);
        canvas.drawRect(neutralRect, mPaint);
        mPaint.reset();

        mPaint.setColor(white);
        mPaint.setAlpha(90);
        mPaint.setStrokeWidth(gridItemSize / 6);
        float midpoint = (boardBottom+boardTop) / 2;
        canvas.drawLine(0, midpoint, sectionWidth, midpoint, mPaint);
        mPaint.reset();

        mPaint.setColor(paleBlue);
        canvas.drawRect(playerRect, mPaint);

        if (bottomGapRect != null) {
            mPaint.setColor(darkBlue);
            canvas.drawRect(bottomGapRect, mPaint);
        }

        if (isGrid) {
            mPaint.setColor(0);
            mPaint.setAlpha(66);
            canvas.drawRect(neutralRect, mPaint);
            mPaint.reset();

            // draw the rows
            int oppRowsBottom = (int) ((opponentTop-boardTop)/gridItemSize);
            int neutralRowsBottom = (int) ((playerTop-boardTop)/gridItemSize);
            int numRows = board.getNumRows();
            mPaint.setColor(darkYellow);
            mPaint.setStrokeWidth(gridItemSize / 8);
            for (int i = 0; i < numRows; ++i) {
                if (i <= oppRowsBottom || i >= neutralRowsBottom) {
                    if (i == neutralRowsBottom) {
                        mPaint.setColor(darkBlue);
                    }
                    int y = (int) (boardTop + i * gridItemSize);
                    canvas.drawLine(0, y, sectionWidth, y, mPaint);
                }
            }

            // draw the columns
            int cols = board.getNumColumns();
            for (int i = 0; i < cols; ++i) {
                int x = (int) (i * gridItemSize);
                mPaint.setColor(darkYellow);
                canvas.drawLine(x, 0, x, opponentTop, mPaint);

                mPaint.setColor(darkBlue);
                canvas.drawLine(x, playerTop, x, boardBottom, mPaint);
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = board.getState();
        if (state == Board.State.BUILD) {
            isGrid = true;
        } else {
            isGrid = false;
        }
    }
}
