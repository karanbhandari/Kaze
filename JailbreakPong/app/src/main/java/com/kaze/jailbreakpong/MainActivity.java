package com.kaze.jailbreakpong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.FrameLayout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        setupBoard();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        // try to hide the actionbar
        // if it throws an exception, keep going, not an issue.
        try {
            getSupportActionBar().hide();
        }catch (Exception e) { /* do nothing */ }
    }

    private void setupBoard(){

        final Board board = Board.getInstance();
        board.init(getApplicationContext());

        BoardView boardView = new BoardView(getApplicationContext());
        layout.addView(boardView);

        // create ball and paddle after boardView is completed inflated, to get accurate boundaries
        final ViewTreeObserver viewTreeObserver = boardView.getViewTreeObserver();
        final BoardView ref = boardView;
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    setupBall();
                    setupPaddles();
                    board.initObservers();
                    ref.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private void setupBall(){
        BoardView.Boundaries boardBoundaries = Helper.getBoundaries();
        // create a ball
        Ball ball = Helper.initBall(this, 0, boardBoundaries.boardTop, (int) Helper.getGridItemSize(), 0.5f);
        ball.addAnimators(boardBoundaries.boardTop, boardBoundaries.boardBottom);

        // add to the layout
        layout.addView(ball);
    }

    private void setupPaddles(){

        BoardView.Boundaries boardBoundaries = Helper.getBoundaries();

        Paddle paddle1 = new Paddle(getApplicationContext(), 200, boardBoundaries.playerTop, R.drawable.ic_bluepaddle);
        layout.addView(paddle1);

        Paddle paddle2 = new Paddle(getApplicationContext(), 200, boardBoundaries.opponentTop, R.drawable.ic_orangepaddle);
        layout.addView(paddle2);
    }
}