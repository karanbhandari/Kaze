package com.kaze.jailbreakpong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Observable;
import java.util.Observer;

public class GameControlView extends LinearLayout implements Observer {
    private LinearLayout wrapper, scoreboard;
    private LinearLayout buttonPanel;
    private ImageButton recordBtn;
    private ImageButton playPauseBtn;
    private ImageButton quitBtn;
    private ImageButton restartBtn;
    private BuildingView buildingKit;
    private TextView msg, playerScore, opponentScore;
    private boolean isOpponent = false;

    /* Programmatic Constructor */
    public GameControlView(Context context) {
        super(context);
        init(context);
    }
    public GameControlView(Context context, Boolean isOpponent) {
        super(context);
        this.isOpponent = isOpponent;
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
        Helper.addObserver(this);

        LayoutInflater.from(context).inflate(R.layout.game_controls_view, this, true);

        wrapper = findViewById(R.id.wrapper);
        buttonPanel = (LinearLayout) findViewById(R.id.buttonPanel);
        playPauseBtn = (ImageButton) findViewById(R.id.pauseBtn);
        restartBtn = findViewById(R.id.restartBtn);
        quitBtn = (ImageButton) findViewById(R.id.endBtn);
        recordBtn = (ImageButton) findViewById(R.id.recordBtn);
        msg = (TextView) findViewById(R.id.message);
        scoreboard = findViewById(R.id.scoreboard);
        playerScore = findViewById(R.id.playerScore);
        opponentScore = findViewById(R.id.opponentScore);

        buildingKit = new BuildingView(getContext(), isOpponent);
        addView(buildingKit);
        buildingKit.setVisibility(GONE);

        playPauseBtn.setImageResource(R.drawable.ic_pause_black_24dp);
        quitBtn.setImageResource(R.drawable.ic_close_black_24dp);
        recordBtn.setImageResource(R.drawable.ic_camera_alt_black_24dp);
        restartBtn.setImageResource(R.drawable.ic_refresh);

        this.setId(R.id.controlLayout);
        this.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);

        this.setLayoutTransition(new LayoutTransition());
        this.bringToFront();

        playPauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.togglePlayPause();
            }
        });

        quitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.restart();
            }
        });

        restartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.restart();
            }
        });

        recordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.toggleRecord();
            }
        });
    }

    public BuildingView.Selected getSelected() {
        if (buildingKit.getVisibility() == GONE) {
            return null;
        }
        return buildingKit.getSelected();
    }
    @Override
    public void update(Observable observable, Object o) {
        Board.State state = Helper.getGameState();

        switch(state) {
            case BUILD:
                buttonPanel.setVisibility(INVISIBLE);
                playPauseBtn.setVisibility(VISIBLE);
                scoreboard.setVisibility(INVISIBLE);
                restartBtn.setVisibility(GONE);
                msg.setVisibility(VISIBLE);
                msg.bringToFront();
                msg.setText("build your board!");
                msg.animate().alpha(0.0f).setStartDelay(2000)
                                .alpha(0.0f)
                                .setDuration(1500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        msg.clearAnimation();
                                        msg.setAlpha(1);
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
                scoreboard.setVisibility(VISIBLE);
                playPauseBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                msg.setVisibility(VISIBLE);
                msg.setText("game paused");
                msg.bringToFront();
                break;
            case PLAY:
                wrapper.setVisibility(VISIBLE);
                playPauseBtn.setVisibility(VISIBLE);
                buttonPanel.setVisibility(VISIBLE);
                scoreboard.setVisibility(VISIBLE);
                playPauseBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                msg.setVisibility(GONE);
                break;
            case END:
                wrapper.setVisibility(VISIBLE);
                buttonPanel.setVisibility(VISIBLE);
                scoreboard.setVisibility(VISIBLE);
                playPauseBtn.setVisibility(GONE);
                restartBtn.setVisibility(VISIBLE);
                msg.setVisibility(VISIBLE);
                msg.bringToFront();
                int oppScore = Helper.getOpponentScore();
                int plyrScore = Helper.getPlayerScore();
                String text = "game over";
                if (oppScore == plyrScore) {
                    text = "tie";
                } else if((isOpponent &&  oppScore > plyrScore) || (!isOpponent && oppScore < plyrScore)) {
                    text = "you win!";
                } else {
                    text = "you lost";
                }
                msg.setText(text);
                msg.animate().alpha(0.0f).setStartDelay(2000)
                        .alpha(0.0f)
                        .setDuration(1500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                msg.clearAnimation();
                                msg.setAlpha(1);
                                msg.setVisibility(GONE);
                                wrapper.setVisibility(GONE);
                                buildingKit.setVisibility(VISIBLE);
                                buildingKit.animate().alpha(1.0f);
                            }
                        });
                break;
            default:
                wrapper.setVisibility(GONE);
                playPauseBtn.setVisibility(VISIBLE);
                scoreboard.setVisibility(VISIBLE);
                buttonPanel.setVisibility(VISIBLE);
                msg.setVisibility(GONE);
                break;
        }

        if (Helper.isRecording()) {
            recordBtn.setColorFilter(Color.RED);
        } else {
            recordBtn.setColorFilter(Color.WHITE);
        }

        opponentScore.setText(Integer.toString(Helper.getOpponentScore()));
        playerScore.setText(Integer.toString(Helper.getPlayerScore()));
    }
}
