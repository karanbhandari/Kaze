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

        final Board board = Board.getInstance();
        board.init(pxWidth, pxHeight, density);
        drawBoardBackground(board);

        int playerTileColor = ResourcesCompat.getColor(getResources(), R.color.gradientBlueLight, null);
        int opponentTileColor = ResourcesCompat.getColor(getResources(), R.color.gradientYellowLight, null);
        board.initBoard(playerTileColor, opponentTileColor);
        drawGridItems(board);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Board board = Board.getInstance();
        drawBoardBackground(board);
        drawGridItems(board);
    }

    public void drawBoardBackground(final Board board) {
        final SurfaceView boardBackground = findViewById(R.id.boardBackground);

        boardBackground.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                surfaceHolder.setFormat(PixelFormat.RGBA_8888);
                Canvas canvas = surfaceHolder.lockCanvas();
                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                int gapBtm = (int) board.getGapBtm();
                int oppBtm = (int) board.getOppBtm();
                int midBtm = (int) board.getMidBtm();
                int playerBtm = (int) board.getPlayerBtm();
                int gridItemSize = (int) board.getGridItemSize();
                int sectionWidth = (int) gridItemSize * board.getNumColumns();

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

                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    public void drawGridItems(final Board board) {
        final SurfaceView gridItemBoard = findViewById(R.id.boardBackground);
        gridItemBoard.setZOrderOnTop(true);
        gridItemBoard.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                surfaceHolder.setFormat(PixelFormat.RGBA_8888);
                Canvas canvas = surfaceHolder.lockCanvas();
                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                int nRows = board.getNumRows();
                int nCols = board.getNumColumns();
                int gapBtm = (int) board.getGapBtm();

                // at start adjust canvas down to account for gap
                canvas.translate(0, gapBtm);

                Drawing drawing = new Drawing(board.getNumRows(), board.getNumColumns());
                drawing.initDraw(board.getGrid(), canvas);

                surfaceHolder.unlockCanvasAndPost(canvas);
                // at very end undo canvas translation
                canvas.save();
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