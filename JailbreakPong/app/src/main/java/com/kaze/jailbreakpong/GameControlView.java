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
    private LinearLayout wrapper;
    private LinearLayout buttonPanel;
    private ImageButton recordBtn;
    private ImageButton playPauseBtn;
    private ImageButton quitBtn;
    private BuildingView buildingKit;
    private TextView msg;

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
        Helper.addObserver(this);

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
                playPauseBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                msg.setVisibility(VISIBLE);
                msg.setText("game paused");
                msg.bringToFront();
                break;
            case PLAY:
                wrapper.setVisibility(VISIBLE);
                buttonPanel.setVisibility(VISIBLE);
                playPauseBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                msg.setVisibility(GONE);
                break;
            default:
                wrapper.setVisibility(GONE);
                buttonPanel.setVisibility(VISIBLE);
                msg.setVisibility(GONE);
                break;
        }

        if (Helper.isRecording()) {
            recordBtn.setColorFilter(Color.RED);
        } else {
            recordBtn.setColorFilter(Color.WHITE);
        }
    }
}
