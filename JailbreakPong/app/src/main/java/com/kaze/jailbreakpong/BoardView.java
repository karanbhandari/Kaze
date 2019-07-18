package com.kaze.jailbreakpong;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.res.ResourcesCompat;

import java.util.Observable;
import java.util.Observer;

public class BoardView extends FrameLayout implements Observer {
    private float boardTop, opponentTop, playerTop, boardBottom;
    FrameLayout opponentBackground, playerBackground;
    View opponentGrid, playerGrid, neutralBackground;
    LinearLayout HUDContainer;
    Board board;
    GameControlView opponentHUD, playerHUD;

    public class Boundaries {
        float boardTop, playerTop, opponentTop, boardBottom;
        public Boundaries(float boardTop, float opponentTop, float playerTop, float boardBottom) {
            this.boardTop = boardTop;
            this.opponentTop = opponentTop;
            this.playerTop = playerTop;
            this.boardBottom = boardBottom;
        }
    }

    public BoardView(Context context){
        super(context);
        this.setWillNotDraw(false);
        board = Board.getInstance();
        board.addBoardView(this);
        Helper.addObserver(this);

        this.setId(R.id.boardView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        this.setLayoutParams(params);

        LayoutInflater.from(context).inflate(R.layout.board_background, this, true);
        opponentBackground = findViewById(R.id.opponentBackground);
        playerBackground = findViewById(R.id.playerBackground);
        neutralBackground = findViewById(R.id.neutralBackground);

        // wrapper to help center the 2 HUD's
        LinearLayout wrapper = new LinearLayout(getContext());
        LinearLayout.LayoutParams HUDContainerParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        wrapper.setLayoutParams(HUDContainerParams);
        wrapper.setGravity(Gravity.CENTER);
        addView(wrapper);

        // LinearLayout to wrap 2 HUD's
        HUDContainer = new LinearLayout(getContext());
        HUDContainerParams.gravity = Gravity.CENTER;
        HUDContainer.setLayoutParams(HUDContainerParams);
        HUDContainer.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(HUDContainer);

        // initialize opponent and player HUD
        opponentHUD = new GameControlView(getContext());
        playerHUD = new GameControlView(getContext());
        HUDContainer.addView(opponentHUD);
        HUDContainer.addView(playerHUD);

        // make opponent HUD take half of LinearLayout
        LinearLayout.LayoutParams HUDParams = (LinearLayout.LayoutParams) opponentHUD.getLayoutParams();
        HUDParams.weight = 1;
        opponentHUD.setLayoutParams(HUDParams);
        opponentHUD.setRotation(180);

        // make player HUD take half of Linear Layout
        HUDParams = (LinearLayout.LayoutParams) playerHUD.getLayoutParams();
        HUDParams.weight = 1;
        playerHUD.setLayoutParams(HUDParams);

        // add grids to each player's side of the board
        opponentGrid = new GridView(getContext());
        opponentBackground.addView(opponentGrid);
        playerGrid = new GridView(getContext());
        playerBackground.addView(playerGrid);

        final ViewTreeObserver viewTreeObserver = this.getViewTreeObserver();
        final FrameLayout reference = this;
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateSize();
                    reference.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        opponentGrid.invalidate();
        playerGrid.invalidate();
    }

    private BuildingView.Selected getPlayerSelected() {
        return playerHUD.getSelected();
    }

    private BuildingView.Selected getOpponentSelected() {
        return opponentHUD.getSelected();
    }

    private void updateSize() {
        updateBoundaries();
        BoardView.Boundaries boundaries = getBoundaries();
        ViewGroup.LayoutParams HUDContainerParams = HUDContainer.getLayoutParams();
        int neutralHeight = (int) (boundaries.playerTop - boundaries.opponentTop);
        HUDContainerParams.height = neutralHeight;
        HUDContainer.setLayoutParams(HUDContainerParams);
    }

    public BoardView.Boundaries getBoundaries() {
        return new Boundaries(boardTop, opponentTop, playerTop, boardBottom);
    }

    public void updateBoundaries() {
        int temp[] = {-1, -1};
        opponentBackground.getLocationInWindow(temp);
        this.boardTop = temp[1];

        neutralBackground.getLocationInWindow(temp);
        this.opponentTop = temp[1];

        playerBackground.getLocationInWindow(temp);
        this.playerTop = temp[1];

        this.boardBottom = temp[1] + playerBackground.getHeight();
    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = Helper.getGameState();
        if (state == Board.State.BUILD) {
            int tint = ResourcesCompat.getColor(getResources(), R.color.darkTint, null);
            neutralBackground.getBackground().setColorFilter(tint, PorterDuff.Mode.DARKEN);
            opponentGrid.setVisibility(VISIBLE);
            playerGrid.setVisibility(VISIBLE);
            opponentBackground.setClickable(true);
            playerBackground.setClickable(true);

        } else {
            neutralBackground.getBackground().setColorFilter(null);
            opponentGrid.setVisibility(GONE);
            playerGrid.setVisibility(GONE);
            opponentBackground.setClickable(false);
            playerBackground.setClickable(false);
        }
    }
}
