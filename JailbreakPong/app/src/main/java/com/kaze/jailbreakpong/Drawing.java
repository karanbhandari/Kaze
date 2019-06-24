package com.kaze.jailbreakpong;
import android.graphics.Canvas;
import android.text.Layout;
import android.util.Log;

import java.util.ArrayList;

public final class Drawing {
    private int nRows, nCols; // number of GridItems horizontally and vertically on screen

    public Drawing(int rows, int cols) {
        nRows = rows;
        nCols = cols;
    }

    public void initDraw(ArrayList<ArrayList<GridItem>> griditems, Canvas canvas) {
        Log.d("Drawing", "The value of nrows and ncols" + nRows + " cols: " + nCols);
        for(int row = 0; row < nRows; ++row) {
            for (int cols = 0; cols < nCols; ++cols){
                Log.d("Drawing", "Rows:" + row + " cols ; " + cols);
                griditems.get(row).get(cols).draw(canvas);
            }
        }
    }

}
