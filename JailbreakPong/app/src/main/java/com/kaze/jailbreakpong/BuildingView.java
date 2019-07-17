package com.kaze.jailbreakpong;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.Observable;
import java.util.Observer;

public class BuildingView extends LinearLayout implements Observer {
    LinearLayout buildingKit;
    LinearLayout squareBrickBtn;
    LinearLayout prisonBtn;
    LinearLayout fakePrisonBtn;
    LinearLayout doneBuildingBtn;
    LinearLayout cancelDoneBtn;
    int selectedColor;
    Board board;
    Selected selected;
    TypedValue rippleEffect;

    enum Selected {
        BRICK, PRISON, FAKEPRISON, DONE;
    }

    Selected getSelected() {
        return selected;
    }

    /* Programmatic Constructor */
    public BuildingView(Context context) {
        super(context);
        init(context);
    }
    /* An XML Constructor */
    public BuildingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    /* An XML Constructor */
    public BuildingView(Context context, AttributeSet attrs, int resId) {
        super(context, attrs, resId);
        init(context);
    }

    @SuppressLint("RtlHardcoded")
    public void init(Context context) {
        selected = Selected.BRICK;
        selectedColor = ResourcesCompat.getColor(getResources(), R.color.darkTint, null);

        rippleEffect = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleEffect, true);

        board = Board.getInstance();
        board.addObserver(this);
        Board.Boundaries boundaries = Helper.getBoardBoundaries();

        LayoutInflater.from(context).inflate(R.layout.building_controls, this, true);

        buildingKit = findViewById(R.id.buildingKit);
        squareBrickBtn = findViewById(R.id.squareBrickBtn);
        prisonBtn = findViewById(R.id.prisonBtn);
        fakePrisonBtn = findViewById(R.id.fakeprisonBtn);
        doneBuildingBtn = findViewById(R.id.doneBuildingBtn);
        cancelDoneBtn = findViewById(R.id.cancelDoneBtn);

        ImageView img = (ImageView) findViewById(R.id.squareBrickImg);
        img.setImageResource(R.drawable.ic_brick);
        img = (ImageView) findViewById(R.id.prisonImg);
        img.setImageResource(R.drawable.ic_prison);
        img = (ImageView) findViewById(R.id.fakeprisonImg);
        img.setImageResource(R.drawable.ic_fakeprison);
        img =  (ImageView) findViewById(R.id.doneBuildingImg);
        img.setImageResource(R.drawable.ic_done);
        img =  (ImageView) findViewById(R.id.cancelDoneImg);
        img.setImageResource(R.drawable.ic_close_black_24dp);

        this.setId(R.id.buildingLayout);
        this.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);

        int halfNeutralHeight = (int) ((boundaries.playerTop - boundaries.opponentTop)/2);
        LinearLayout.LayoutParams buildingKitParams = (LinearLayout.LayoutParams) buildingKit.getLayoutParams();
        buildingKitParams.height = halfNeutralHeight;
        buildingKit.setLayoutParams(buildingKitParams);

        this.bringToFront();
        this.setLayoutTransition(new LayoutTransition());

        updateBackground();

        squareBrickBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayUpdateBackground(selected, Selected.BRICK);
                    selected = Selected.BRICK;
                }
                return false;
            }
        });

        prisonBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayUpdateBackground(selected, Selected.PRISON);
                    selected = Selected.PRISON;
                }
                return false;
            }
        });

        fakePrisonBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayUpdateBackground(selected, Selected.FAKEPRISON);
                    selected = Selected.FAKEPRISON;
                }
                return false;
            }
        });

        doneBuildingBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayUpdateBackground(selected, Selected.DONE);
                    selected = Selected.DONE;
                }
                return false;
            }
        });

        cancelDoneBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayUpdateBackground(selected, Selected.BRICK);
                    selected = Selected.BRICK;
                }
                return false;
            }
        });
    }

    private void delayUpdateBackground(final Selected lastSelected, final Selected curSelected) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateBackground();
                if (lastSelected == Selected.DONE) {
                    cancelDoneBtn.setBackgroundResource(rippleEffect.resourceId);
                    squareBrickBtn.setVisibility(VISIBLE);
                    prisonBtn.setVisibility(VISIBLE);
                    fakePrisonBtn.setVisibility(VISIBLE);
                    doneBuildingBtn.setVisibility(VISIBLE);
                    cancelDoneBtn.setVisibility(GONE);
                } else if (curSelected == Selected.DONE) {
                    squareBrickBtn.setVisibility(GONE);
                    prisonBtn.setVisibility(GONE);
                    fakePrisonBtn.setVisibility(GONE);
                    doneBuildingBtn.setVisibility(GONE);
                    cancelDoneBtn.setVisibility(VISIBLE);
                }
            }
        }, 250);
    }

    private void updateBackground() {


        if (selected == Selected.BRICK) {
            squareBrickBtn.setBackgroundColor(selectedColor);
        } else {
            squareBrickBtn.setBackgroundResource(rippleEffect.resourceId);
        }

        if (selected == Selected.PRISON) {
            prisonBtn.setBackgroundColor(selectedColor);
        } else {
            prisonBtn.setBackgroundResource(rippleEffect.resourceId);;
        }

        if (selected == Selected.FAKEPRISON) {
            fakePrisonBtn.setBackgroundColor(selectedColor);
        } else {
            fakePrisonBtn.setBackgroundResource(rippleEffect.resourceId);;
        }

        if (selected == Selected.DONE) {
            int frost = ContextCompat.getColor(getContext(), R.color.frost);
            int red = ContextCompat.getColor(getContext(), R.color.ballRed);
            ColorDrawable[] color = {new ColorDrawable(frost), new ColorDrawable(red)};
            TransitionDrawable transition = new TransitionDrawable(color);
            //This will work also on old devices. The latest API says you have to use setBackground instead.
            cancelDoneBtn.setBackground(transition);
            transition.startTransition(30000);
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = board.getState();

        if (state != Board.State.BUILD) {
            this.setVisibility(GONE);
        }

    }
}

