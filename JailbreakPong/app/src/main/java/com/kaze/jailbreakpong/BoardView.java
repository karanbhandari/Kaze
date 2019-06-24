package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.res.ResourcesCompat;

import static java.lang.Math.round;

public class BoardView extends View {
    private int gapBtm, oppBtm, midBtm, playerBtm, sectionWidth, gridItemSize;
    Paint mPaint;

    public BoardView(Context context){
        super(context);
        Board board = Board.getInstance();

        gapBtm = (int) board.getGapBtm();
        oppBtm = (int) board.getOppBtm();
        midBtm = (int) board.getMidBtm();
        playerBtm = (int) board.getPlayerBtm();
        gridItemSize = (int) board.getGridItemSize();
        sectionWidth = (int) gridItemSize * board.getNumColumns();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw the View
        int paleYellow, paleBlue, paleOrange, palePurple, white;
        paleBlue = ResourcesCompat.getColor(getResources(), R.color.paleBlue, null);
        paleYellow = ResourcesCompat.getColor(getResources(), R.color.paleYellow, null);
        palePurple = ResourcesCompat.getColor(getResources(), R.color.palePurple, null);
        paleOrange = ResourcesCompat.getColor(getResources(), R.color.paleOrange, null);
        white = ResourcesCompat.getColor(getResources(), R.color.white, null);

        // if the dimensions of our board do not match the ratio of the device, center
        if (gapBtm != 0) {
            int gapYellow = ResourcesCompat.getColor(getResources(), R.color.gapYellow, null);
            mPaint.setColor(gapYellow);
            Rect oppGap = new Rect(0, 0, sectionWidth, gapBtm);
            canvas.drawRect(oppGap, mPaint);
        }

        mPaint.setColor(paleYellow);
        Rect oppBackground = new Rect(0, gapBtm, sectionWidth, oppBtm);
        canvas.drawRect(oppBackground, mPaint);

        Rect intermediate = new Rect(0, oppBtm, sectionWidth, midBtm);
        mPaint.setShader(new LinearGradient(0, oppBtm, 0, midBtm, palePurple, paleOrange, Shader.TileMode.CLAMP));
        canvas.drawRect(intermediate, mPaint);
        mPaint.reset();

        mPaint.setColor(white);
        mPaint.setAlpha(90);
        mPaint.setStrokeWidth(gridItemSize / 6);
        int midpoint = (int) round(oppBtm + (midBtm - oppBtm) / 2);
        canvas.drawLine(0, midpoint, sectionWidth, midpoint, mPaint);
        mPaint.reset();

        mPaint.setColor(paleBlue);
        Rect playerBackground = new Rect(0, midBtm, sectionWidth, playerBtm);
        canvas.drawRect(playerBackground, mPaint);

        if (gapBtm != 0) {
            int gapBlue = ResourcesCompat.getColor(getResources(), R.color.gapBlue, null);
            int bottom = playerBtm + gapBtm;
            mPaint.setColor(gapBlue);
            Rect playerGap = new Rect(0, playerBtm, sectionWidth, bottom);
            canvas.drawRect(playerGap, mPaint);
        }
    }
}
