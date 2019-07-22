package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Square extends Brick {
    // HP = 1
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Square(Context context, int row, int column, int width, int height, int lightColor, int darkColor) {
        super(context, row, column, width, height,1, lightColor, darkColor);
        paint.setColor(this.lightColor);
        paint.setShader(new LinearGradient(row, column, row+width, column+width, darkColor, lightColor, Shader.TileMode.MIRROR));
    }

    public void replace(BuildingView.Selected selection) {
        int gridRow = (int) column/width;
        int gridColumn = (int) row/height;
        if (selection == BuildingView.Selected.BRICK) {
            Helper.remove(gridRow, gridColumn);
        } else if (selection == BuildingView.Selected.PRISON) {
            Helper.add(selection, gridRow, gridColumn);
        }
        if (this.getParent() != null) ((ViewGroup) this.getParent()).removeView(this);
    }

    public void hit() {
        int gridRow = (int) column/width;
        int gridColumn = (int) row/height;
        this.hp-=1;
        // change the image so that it shows one with a broken brick or decrease color
        if (this.hp == 0) {
            Helper.remove(gridRow, gridColumn);
            if (this.getParent() != null) ((ViewGroup) this.getParent()).removeView(this);
        }
        invalidate();   // force a redraw
    }

    @Override
    public boolean onHit(ArrayList<int[]> boundaries) {
        // do nothing for blank item
        // children classes overwrite this function.
        return true;
    }

    @Override
    public void hasHit(int [] coordinates, Ball ball, float pxX, float pxY){
//        Log.d("SQUARE", "hasHit: coordinates: " + coordinates[0] + " " + coordinates[1]);
//        Log.d("SQUARE", "hasHit: hit coordinates: " + pxX + " " + pxY);
//        Log.d("SQUARE", "hasHit: brick coordinates: " + row + " " + column);
//        Log.d("SQUARE", "hasHit: brick coordinates: " + (row + width) + " " + column);
//        Log.d("SQUARE", "hasHit: brick coordinates: " + row + " " + (column + height));
//        Log.d("SQUARE", "hasHit: brick coordinates: " + (row + width)+ " " + (column + height));
        hit();

        // remember: pxX and pxY are the top left corners

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

        Log.d("Square", "draw called ");

        int x = this.row;
        int y = this.column;

        if(hp <= 0){
            // not draw
            canvas.save();
            canvas.restore();
        } else {
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(x + width, y);
            path.lineTo(x + width, y + height);
            path.lineTo(x, y + height);
            path.lineTo(x, y);
            path.close();
            canvas.drawPath(path, paint);
            canvas.save();
            canvas.restore();
        }

    }
}
