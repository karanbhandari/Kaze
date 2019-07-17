package com.kaze.jailbreakpong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Observable;
import java.util.Observer;

public class GameControlView extends LinearLayout implements Observer {
    LinearLayout wrapper;
    LinearLayout buttonPanel;
    ImageButton recordBtn;
    ImageButton playPauseBtn;
    ImageButton quitBtn;
    BuildingView buildingKit;
    Board board;
    TextView msg;

    /* Programmatic Constructor */
    public GameControlView(Context context) {
        super(context);
        init(context);
    }
    /* An XML Constructor */
    public GameControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    /* An XML Constructor */
    public GameControlView(Context context, AttributeSet attrs, int resId) {
        super(context, attrs, resId);
        init(context);
    }

    @SuppressLint("RtlHardcoded")
    public void init(Context context) {
        board = Board.getInstance();
        board.addObserver(this);
        Board.Boundaries boundaries = Helper.getBoardBoundaries();

        LayoutInflater.from(context).inflate(R.layout.game_controls_view, this, true);

        wrapper = findViewById(R.id.wrapper);
        buttonPanel = (LinearLayout) findViewById(R.id.buttonPanel);
        playPauseBtn = (ImageButton) findViewById(R.id.pauseBtn);
        quitBtn = (ImageButton) findViewById(R.id.endBtn);
        recordBtn = (ImageButton) findViewById(R.id.recordBtn);
        msg = (TextView) findViewById(R.id.message);

        buildingKit = new BuildingView(getContext());
        addView(buildingKit);
        buildingKit.setVisibility(GONE);

        playPauseBtn.setImageResource(R.drawable.ic_pause_black_24dp);
        quitBtn.setImageResource(R.drawable.ic_close_black_24dp);
        recordBtn.setImageResource(R.drawable.ic_camera_alt_black_24dp);

        this.setId(R.id.controlLayout);
        this.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);

        int halfNeutralHeight = (int) ((boundaries.playerTop - boundaries.opponentTop)/2);
        params = (LinearLayout.LayoutParams) buildingKit.getLayoutParams();
        params.height = halfNeutralHeight;
        this.setLayoutParams(params);

        this.setLayoutTransition(new LayoutTransition());

        float dpi = Helper.getDisplayMetrics(context).density;
//
//        float screenMiddle = (boundaries.boardBottom-boundaries.boardTop)/2 + boundaries.boardTop;
//        this.setY(screenMiddle);
        this.bringToFront();
    }

    public BuildingView.Selected getSelected() {
        return buildingKit.getSelected();
    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = board.getState();

        switch(state) {
            case BUILD:
                buttonPanel.setVisibility(INVISIBLE);
                msg.setText("build your board!");
                msg.animate().alpha(0.0f).setStartDelay(3000)
                                .alpha(0.0f)
                                .setDuration(1500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        msg.clearAnimation();
                                        msg.setVisibility(GONE);
                                        wrapper.setVisibility(GONE);
                                        buildingKit.setVisibility(VISIBLE);
                                        buildingKit.animate().alpha(1.0f);
                                    }
                                });

                break;
            case PAUSE:
                wrapper.setVisibility(VISIBLE);
                buttonPanel.setVisibility(VISIBLE);
                msg.setText("game paused");
                break;
            case PLAY:
                wrapper.setVisibility(VISIBLE);
                buttonPanel.setVisibility(VISIBLE);
                msg.clearComposingText();
                break;
            default:
                wrapper.setVisibility(GONE);
                buttonPanel.setVisibility(VISIBLE);
                msg.clearComposingText();
                break;
        }
    }
}
