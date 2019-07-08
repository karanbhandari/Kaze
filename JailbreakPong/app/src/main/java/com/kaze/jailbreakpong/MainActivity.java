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
import android.widget.FrameLayout;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

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
        board.init(getApplicationContext(), pxWidth, pxHeight, density);
        //drawBoardBackground(board);
        BoardView boardView = new BoardView(getApplicationContext());
        FrameLayout fl = (FrameLayout) findViewById(R.id.FrameLayout);
        fl.addView(boardView);

        int playerTileColorLight = ResourcesCompat.getColor(getResources(), R.color.gradientBlueLight, null);
        int playerTileColorDark = ResourcesCompat.getColor(getResources(), R.color.gradientBlueDark, null);
        int opponentTileColorLight = ResourcesCompat.getColor(getResources(), R.color.gradientYellowLight, null);
        int opponentTileColorDark = ResourcesCompat.getColor(getResources(), R.color.gradientYellowDark, null);
        board.initBoard(fl, getApplicationContext(), playerTileColorLight, playerTileColorDark, opponentTileColorLight, opponentTileColorDark);
        //drawGridItems(board);

        // Code to add the ball to the layout. Need to 'merge' in future commits
//      DisplayMetrics metrics = Helper.getDisplayMetrics(this);
//      float x = (float) metrics.widthPixels / 2;
//      float y = (float) metrics.heightPixels / 2;
//
//      // create a ball
//      Ball ball = Helper.initBall(this, 0, 126, 100, 1f);
//
//      ball.addAnimators();
//
//      // add to the layout
//      LinearLayout layout = (LinearLayout) findViewById(R.id.Layout);
//      layout.addView(ball);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


//    public void drawGridItems(final Board board) {
//        final SurfaceView gridItemBoard = findViewById(R.id.boardBackground);
//        gridItemBoard.setZOrderOnTop(true);
//        gridItemBoard.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                surfaceHolder.setFormat(PixelFormat.RGBA_8888);
//                Canvas canvas = surfaceHolder.lockCanvas();
//                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//                int nRows = board.getNumRows();
//                int nCols = board.getNumColumns();
//                int gapBtm = (int) board.getGapBtm();
//
//                // at start adjust canvas down to account for gap
//                canvas.translate(0, gapBtm);
//
//                Drawing drawing = new Drawing(board.getNumRows(), board.getNumColumns());
//                drawing.initDraw(board.getGrid(), canvas);
//
//                surfaceHolder.unlockCanvasAndPost(canvas);
//                // at very end undo canvas translation
//                canvas.save();
//                canvas.restore();
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//            }
//        });
//    }

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