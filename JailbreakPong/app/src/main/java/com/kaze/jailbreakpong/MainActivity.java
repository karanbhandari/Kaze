package com.kaze.jailbreakpong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.util.DisplayMetrics;
import android.view.Display;
import android.graphics.Paint;
import android.view.WindowManager;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        float density = getResources().getDisplayMetrics().density;
        final float pxHeight = displayMetrics.heightPixels + getNavbarHeight();
        final float pxWidth = displayMetrics.widthPixels;

        Board board = Board.getInstance();
        board.init(pxWidth, pxHeight, density);
        final float gridItemSize = board.getGridItemSize();
        final float gapSize = board.getGapSize();
        final int nRows = board.getNumRows();
        final int nCols = board.getNumColumns();

        SurfaceView gridItemBoard = findViewById(R.id.gridItemBoard);
        final SurfaceView boardBackground = findViewById(R.id.boardBackground);

        boardBackground.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                surfaceHolder.setFormat(PixelFormat.RGBA_8888);
                Canvas canvas = surfaceHolder.lockCanvas();
                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                int paleYellow, paleBlue, paleOrange, palePurple, white;
                paleBlue = ResourcesCompat.getColor(getResources(), R.color.paleBlue, null);
                paleYellow = ResourcesCompat.getColor(getResources(), R.color.paleYellow, null);
                palePurple = ResourcesCompat.getColor(getResources(), R.color.palePurple, null);
                paleOrange = ResourcesCompat.getColor(getResources(), R.color.paleOrange, null);
                white = ResourcesCompat.getColor(getResources(), R.color.white, null);

                int top = 0;
                int sectionHeight = (int) round(nRows/3 * gridItemSize);
                int sectionWidth = round(nCols * gridItemSize);
                // if the dimensions of our board do not match the ratio of the device, center
                if (gapSize != 0) {
                    int gapYellow = ResourcesCompat.getColor(getResources(), R.color.gapYellow, null);

                    int bottom = round(gapSize/2);
                    mPaint.setColor(gapYellow);
                    Rect oppGap = new Rect(0, 0, sectionWidth, bottom);
                    canvas.drawRect(oppGap, mPaint);
                    top = bottom;
                }

                mPaint.setColor(paleYellow);
                Rect oppBackground = new Rect(0, top, sectionWidth, sectionHeight+top);
                canvas.drawRect(oppBackground, mPaint);

                top = top + sectionHeight;
                Rect intermediate = new Rect(0, top, sectionWidth, sectionHeight+top);
                mPaint.setShader(new LinearGradient(0, top, 0, sectionHeight+top, palePurple, paleOrange, Shader.TileMode.CLAMP));
                canvas.drawRect(intermediate, mPaint);
                mPaint.reset();

                mPaint.setColor(white);
                mPaint.setAlpha(90);
                mPaint.setStrokeWidth(gridItemSize/3);
                int midpoint = (int) round(nRows/2 * gridItemSize) + round(gapSize/2);
                canvas.drawLine(0, midpoint, sectionWidth, midpoint, mPaint);
                mPaint.reset();

                mPaint.setColor(paleBlue);
                top = sectionHeight +top;
                Rect playerBackground = new Rect(0, top, sectionWidth, sectionHeight+top);
                canvas.drawRect(playerBackground, mPaint);

                if (gapSize != 0) {
                    int gapBlue = ResourcesCompat.getColor(getResources(), R.color.gapBlue, null);
                    top = sectionHeight+top;
                    int bottom = top+round(gapSize/2);
                    mPaint.setColor(gapBlue);
                    Rect playerGap = new Rect(0, top, sectionWidth, bottom);
                    canvas.drawRect(playerGap, mPaint);
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        // TODO: MainActivity cannot keep this
        final GridItem initGrid[][] = board.getGrid();
        gridItemBoard.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                surfaceHolder.setFormat(PixelFormat.RGBA_8888);
                Canvas canvas = surfaceHolder.lockCanvas();
                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                Drawing drawing = new Drawing(nRows, nCols);
                drawing.initDraw(initGrid, canvas);

                // at start adjust canvas down to account for gap
                canvas.translate(0, round(gapSize/2));

                surfaceHolder.unlockCanvasAndPost(canvas);
                // at very end undo canvas translation
                canvas.restore();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public int getNavbarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}