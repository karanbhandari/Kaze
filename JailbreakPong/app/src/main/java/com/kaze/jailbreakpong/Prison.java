package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

public class Prison extends Brick {
    int HP = 1;
    //Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Drawable prisonXml;
    ColorFilter filter;

    public Prison(Context context, int row, int column, int width, int height, int lightColor, int darkColor) {
        super(context, row, column, width, height, 1, lightColor, darkColor);
        score = 10;
        prisonXml = ContextCompat.getDrawable(getContext(), R.drawable.ic_prison);
        prisonXml.mutate();
        filter = new PorterDuffColorFilter(lightColor, PorterDuff.Mode.SRC_IN);
    }

    public void replace(BuildingView.Selected selection) {
        int gridRow = (int) column/width;
        int gridColumn = (int) row/height;
        if (selection == BuildingView.Selected.BRICK) {
            Helper.removePrison(gridRow, gridColumn);
            Helper.add(selection, gridRow, gridColumn);
        } else if (selection == BuildingView.Selected.PRISON) {
            Helper.removePrison(gridRow, gridColumn);
        }
        if (this.getParent() != null) ((ViewGroup) this.getParent()).removeView(this);
    }

    public void hit() {
        int gridRow = (int) column/width;
        int gridColumn = (int) row/height;
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
        if (this.hp == 0) {
            Helper.removePrison(gridRow, gridColumn);
            if (this.getParent() != null) ((ViewGroup) this.getParent()).removeView(this);
        }
    }

    @Override
    public void hasHit(int [] coordinates, Ball ball, float pxX, float pxY){

        hit();

        final float THRESHOLD = 10;

        int[] ballDir = ball.getDir();

        BoardView.Boundaries boundaries = Helper.getBoundaries();

        float newStartX = 0f;
        float newStartY = 0f;
        float brickActualY = column + boundaries.boardTop;

        if (ballDir[1] == 1){

            // cases to consider
            if (pxY + ball.getSize() > brickActualY){

                if (ballDir[0] == 1){
                    // ball is moving to the right, needs to bounce to the left
                    newStartX = (coordinates[0] * Helper.getGridItemSize()) - ball.getSize();
                } else if (ballDir[0] == -1) {
                    // ball is moving to the left, needs to bounce to the right
                    newStartX = (coordinates[0] * Helper.getGridItemSize()) + Helper.getGridItemSize();
                }

                ball.reverseX();
                ball.setNewEndX(newStartX);

            } else if (Math.abs(pxY + ball.getSize() - brickActualY) <= THRESHOLD){

                if (ballDir[1] == 1){
                    // ball is moving to the bottom, needs to bounce to the top
                    newStartY =  boundaries.boardTop + (coordinates[1] * Helper.getGridItemSize()) - ball.getSize();
                } else {
                    // ball is moving to the top, needs to bounce back to the bottom
                    newStartY = boundaries.boardTop + (coordinates[1] * Helper.getGridItemSize()) + ball.getSize();
                }

                ball.reverseY();
                ball.setNewEndY(newStartY);

            }

        } else if (ballDir[1] == -1) {

            if (pxY < brickActualY + height){

                // reverse X

                if (ballDir[0] == 1){
                    // ball is moving to the right, needs to bounce to the left
                    newStartX = (coordinates[0] * Helper.getGridItemSize()) - ball.getSize();
                } else if (ballDir[0] == -1) {
                    // ball is moving to the left, needs to bounce to the right
                    newStartX = (coordinates[0] * Helper.getGridItemSize()) + Helper.getGridItemSize();
                }

                ball.reverseX();
                ball.setNewEndX(newStartX);

            } else if (Math.abs(pxY - (brickActualY + height)) <= THRESHOLD){

                // reverse Y

                if (ballDir[1] == 1){
                    // ball is moving to the bottom, needs to bounce to the top
                    newStartY =  boundaries.boardTop + (coordinates[1] * Helper.getGridItemSize()) - ball.getSize();
                } else {
                    // ball is moving to the top, needs to bounce back to the bottom
                    newStartY = boundaries.boardTop + (coordinates[1] * Helper.getGridItemSize()) + ball.getSize();
                }

                ball.reverseY();
                ball.setNewEndY(newStartY);

            }

        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(0, gapSize/2);

        Log.d("Prison", "draw called ");

        int x = this.row;
        int y = this.column;
        int spacing = width/10;

        prisonXml.setBounds(x+spacing, y+spacing, x+width-spacing, y+height-spacing);
        prisonXml.setColorFilter(filter);
        prisonXml.draw(canvas);

        canvas.save();
        canvas.restore();
    }
}
