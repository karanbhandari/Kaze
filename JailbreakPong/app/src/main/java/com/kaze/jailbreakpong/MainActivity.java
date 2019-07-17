package com.kaze.jailbreakpong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        setupBoard();

        setupBall();


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
    }

    private void setupBoard(){

        final Board board = Board.getInstance();
        board.init(getApplicationContext());

        BoardView boardView = new BoardView(getApplicationContext());
        FrameLayout fl = findViewById(R.id.FrameLayout);
        fl.addView(boardView);

        int playerTileColorLight = ResourcesCompat.getColor(getResources(), R.color.gradientBlueLight, null);
        int playerTileColorDark = ResourcesCompat.getColor(getResources(), R.color.gradientBlueDark, null);
        int opponentTileColorLight = ResourcesCompat.getColor(getResources(), R.color.gradientYellowLight, null);
        int opponentTileColorDark = ResourcesCompat.getColor(getResources(), R.color.gradientYellowDark, null);
        board.initBoard(fl, getApplicationContext(), playerTileColorLight, playerTileColorDark, opponentTileColorLight, opponentTileColorDark);

    }

    private void setupBall(){
        Board.Boundaries boardBoundaries = Helper.getBoardBoundaries();
        // create a ball
        Ball ball = Helper.initBall(this, 0, boardBoundaries.boardTop, 100, 1f);
        ball.addAnimators(boardBoundaries.boardTop, boardBoundaries.boardBottom);

        // add to the layout
        FrameLayout layout = findViewById(R.id.FrameLayout);
        layout.addView(ball);
    }
}