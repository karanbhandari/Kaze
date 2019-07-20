package com.kaze.jailbreakpong;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.Observable;
import java.util.Observer;

public class BuildingView extends LinearLayout implements Observer {
    private LinearLayout buildingKit;
    private LinearLayout squareBrickBtn;
    private LinearLayout prisonBtn;
    private LinearLayout fakePrisonBtn;
    private LinearLayout doneBuildingBtn;
    private LinearLayout cancelDoneBtn;
    private Boolean isOpponent;
    int selectedColor;
    private Selected selected;
    TypedValue rippleEffect;

    enum Selected {
        BRICK, PRISON, FAKEPRISON, DONE;
    }

    public Selected getSelected() {
        return selected;
    }

    public BuildingView(Context context, Boolean isOpponent) {
        super(context);
        this.isOpponent = isOpponent;
        init(context);
    }

    @SuppressLint("RtlHardcoded")
    public void init(Context context) {
        selected = Selected.BRICK;
        selectedColor = ResourcesCompat.getColor(getResources(), R.color.darkTint, null);

        rippleEffect = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleEffect, true);

        Helper.addObserver(this);

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
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);

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
                    int [] numPrisons = Helper.getNumPrisonsPerPlayer();
                    if ((numPrisons[0] > 0 && isOpponent) || (numPrisons[1] > 0 && !isOpponent)) {
                        delayUpdateBackground(selected, Selected.DONE);
                        selected = Selected.DONE;
                        Helper.onDoneBuild(true);
                    } else {
                        int white = Color.TRANSPARENT;
                        int red = Color.RED;
                        prisonBtn.setBackground(new ColorDrawable(red));

                        ColorDrawable[] color = {new ColorDrawable(red), new ColorDrawable(white)};
                        TransitionDrawable transition = new TransitionDrawable(color);
                        prisonBtn.setBackground(transition);
                        transition.startTransition(1000);
                    }
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
                    Helper.onDoneBuild(false);
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
                    //fakePrisonBtn.setVisibility(VISIBLE);
                    doneBuildingBtn.setVisibility(VISIBLE);
                    cancelDoneBtn.setVisibility(GONE);
                } else if (curSelected == Selected.DONE) {
                    squareBrickBtn.setVisibility(GONE);
                    prisonBtn.setVisibility(GONE);
                    //fakePrisonBtn.setVisibility(GONE);
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
            int darkTint = ContextCompat.getColor(getContext(), R.color.darkTint);
            int red = Color.RED;
            ColorDrawable[] color = {new ColorDrawable(darkTint), new ColorDrawable(red)};
            TransitionDrawable transition = new TransitionDrawable(color);
            //This will work also on old devices. The latest API says you have to use setBackground instead.
            cancelDoneBtn.setBackground(transition);
            transition.startTransition(15000);
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        Board.State state = Helper.getGameState();

        if (state == Board.State.BUILD) {
            selected = Selected.BRICK;
            squareBrickBtn.setVisibility(VISIBLE);
            prisonBtn.setVisibility(VISIBLE);
            //fakePrisonBtn.setVisibility(VISIBLE);
            doneBuildingBtn.setVisibility(VISIBLE);
            cancelDoneBtn.setVisibility(GONE);
            updateBackground();
        } else {
            this.setVisibility(GONE);
        }

    }
}

