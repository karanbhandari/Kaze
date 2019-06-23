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
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.os.Build.VERSION;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {
    int heightWithouNav = 0;

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

        /*
        * Getting screen size without navbar. its problematic with navbars
        * */


//        final RelativeLayout fullLayout = (RelativeLayout) findViewById(R.id.Layout);
//        ViewTreeObserver vto = fullLayout.getViewTreeObserver();
//
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //remove listener to ensure only one call is made.
//                fullLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                int h = fullLayout.getHeight();
//
//                heightWithouNav = h;
//            }
//        });

        // puts ball on screen
        Ball ball = init();

        // adds animator
        addAnimator(ball);

        addYAnimator(ball);

    }

    private void  addYAnimator(final Ball ball){

        final Context context = this;

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);

        // getMetrics does not account for statusBar or actionBar height
        int statusBarHeight = Helper.getStatusBarHeight(this);
        int actionBarHeight = Helper.getActionBarHeight(this);

        float endingFloat = metrics.heightPixels - statusBarHeight - actionBarHeight - ball.getSize();
        final ValueAnimator animator = ValueAnimator.ofFloat(0, endingFloat);

        Log.d("YAnim", "height calced: " + endingFloat);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();

                ball.setY(animatedVal);
                ball.setPosY(animatedVal);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);

                // reverse direction of ball
                ball.reverseY();

                // get end direction of ball
                float newEndY = ball.getEndPointY(context);

                PropertyValuesHolder[] vals = ((ValueAnimator)animation).getValues();

                Log.d("yolo2", "onAnimationStart: called with vals: " + vals[0].toString());
//                Log.d("yolo1", "ball posX: " + ball.getPosX());

                vals[0].setFloatValues(ball.getPosY() , newEndY);

//                Log.d("yolo1", "onAnimationStart: set vals: " + vals[0].toString());
                ((ValueAnimator)animation).setValues(vals);

            }
        });

        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }


    // prolly move this to Ball class
    private void addAnimator(final Ball ball){


        final ValueAnimator animator = ValueAnimator.ofFloat(0,
                Resources.getSystem().getDisplayMetrics().widthPixels - ball.getSize());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedVal = (float) animator.getAnimatedValue();
                ball.setX(animatedVal);
                ball.setPosX(animatedVal);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationEnd(animation);

                // reverse direction of ball
                ball.reverseX();

                int [] dir = ball.getDir();


                if (dir[0] == 1){
                    // moving right

                } else {
                    // moving left
                }

                // get end direction of ball
                float newEnd = ball.getEndPoint();

                PropertyValuesHolder[] vals = ((ValueAnimator)animation).getValues();


                Log.d("yolo1", "onAnimationStart: called with vals: " + vals[0].toString());
                Log.d("yolo1", "ball posX: " + ball.getPosX());

                vals[0].setFloatValues(ball.getPosX(), newEnd);

//                Log.d("yolo1", "onAnimationStart: set vals: " + vals[0].toString());
                ((ValueAnimator)animation).setValues(vals);

            }
        });

        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();


    }

    private Ball init(){

        Ball ball = new Ball(this, 0, 0, 100, 10);

        // add to the layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.Layout);
        layout.addView(ball);

        return ball;

    }

}
