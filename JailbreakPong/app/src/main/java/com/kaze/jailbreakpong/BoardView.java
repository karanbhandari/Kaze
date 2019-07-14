package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.SectionIndexer;

import androidx.core.content.res.ResourcesCompat;

import static java.lang.Math.round;

public class BoardView extends View {
    private float boardTop, opponentTop, playerTop, boardBottom, sectionWidth, gridItemSize;
    int paleYellow, paleBlue, paleOrange, palePurple, white, darkYellow, darkBlue;
    RectF topGapRect, opponentRect, neutralRect, playerRect, bottomGapRect;
    LinearGradient neutralRectShader;
    Paint mPaint;

    public BoardView(Context context){
        super(context);
        Board board = Board.getInstance();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // get board boundaries
        boardTop = board.getBoardTop();
        opponentTop = board.getOpponentTop();
        playerTop = board.getPlayerTop();
        boardBottom = board.getBoardBottom();
        gridItemSize = board.getGridItemSize();
        sectionWidth = gridItemSize * board.getNumColumns();

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
    }
}
