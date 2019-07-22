package com.kaze.jailbreakpong;

import android.content.Context;
import android.view.View;
import android.graphics.Canvas;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GridItem extends View {
    private int row, column;   // position of GridItem in our Board object
    protected int score;
    private boolean isVisible;

    public GridItem(Context context, int rowPos, int columnPos) {
        super(context);
        row = rowPos;
        column = columnPos;
        isVisible = false;
    }

    public GridItem(Context context, int rowPos, int columnPos, boolean isVisible) {
        super(context);
        row = rowPos;
        column = columnPos;
        isVisible = isVisible;
    }

    public int[] getPosition() {
        int[] pos = {row, column};
        return pos;
    }

    public int getRow(){
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setPosition(int row, int column) {
        row = row;
        column = column;
    }

    public int getScore() {
        return score;
    }

    public boolean onHit(ArrayList<int[]> boundaries) {
        // do nothing for blank item
        // children classes overwrite this function.
        return false;
    }

    public void replace(BuildingView.Selected selection) {
        Helper.add(selection, row, column);
//        ((ViewGroup) this.getParent()).removeView(this);
    }

    public void hasHit(int [] coordinates, Ball ball, float pxX, float pxY){
        // need pxX, pxY to figure out which side the hit was one

    }

    public void onDraw(Canvas canvas) {
        // do nothing for blank item
        // children classes overwrite this function
    }
}