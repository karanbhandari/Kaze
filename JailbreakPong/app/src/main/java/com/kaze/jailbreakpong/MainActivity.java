package com.kaze.jailbreakpong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    FrameLayout layout;

    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private MediaRecorder mediaRecorder;

    private int mScreenDensity;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private boolean wasRecording = false;

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 1800);
    }

    // View
    private String videoUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

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
        final MainActivity refMain = this;
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    setupBall();
                    setupPaddles();
                    Helper.addObserver(refMain);
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

    // call start recording when the isRecording is called



    public void startRecording() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                Toast.makeText(this, "Permissions", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_PERMISSION);
            }
        } else {
            toggleScreenShare();
        }
    }

    private void setupPaddles(){

        BoardView.Boundaries boardBoundaries = Helper.getBoundaries();

        Paddle paddle1 = new Paddle(getApplicationContext(), 200, boardBoundaries.playerTop, R.drawable.ic_bluepaddle);
        layout.addView(paddle1);

        Paddle paddle2 = new Paddle(getApplicationContext(), 200, boardBoundaries.opponentTop, R.drawable.ic_orangepaddle);
        layout.addView(paddle2);
    }

    private void toggleScreenShare() {
        if(Helper.isRecording() && !wasRecording) {
            wasRecording = true;
            initRecorder();
            recordScreen();
        } else if (!Helper.isRecording() && wasRecording) {
            wasRecording = false;
            mediaRecorder.stop();
            mediaRecorder.reset();

            startSharingIntent();
            // TODO: needs to be changed to Helper.Pause after rebase master
            Helper.togglePlayPause();

            stopRecordScreen();
        }
    }

    private void startSharingIntent() {

        ContentValues content = new ContentValues(4);
        content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                System.currentTimeMillis() / 1000);
        content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, videoUri);

        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);

        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("video/*");
        String shareBody = "Your Jailbreak Pong video is here";
        String shareSub = "JailbreakPong Video";
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        myIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(myIntent, "Share video"));
    }

    private void recordScreen() {
        if(mediaProjection == null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("videoCapture", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            videoUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + new StringBuilder("/JailBreakPong").append(new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss")
                    .format(new Date())).append(".mp4").toString();

            mediaRecorder.setOutputFile(videoUri);
            mediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(512*1000);
            mediaRecorder.setVideoFrameRate(30);

            mediaRecorder.setOrientationHint(0);
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CTRL+O

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != REQUEST_CODE) {
            Toast.makeText(this, "UNK ERROR", Toast.LENGTH_SHORT).show();
            return;
        }

        if(resultCode != RESULT_OK) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaProjectionCallback = new MediaProjectionCallback();
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        mediaProjection.registerCallback(mediaProjectionCallback, null);

        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        // Ctrl+O

        @Override
        public void onStop() {
            if(Helper.isRecording()) {
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopRecordScreen();
            super.onStop();
        }
    }

    private void stopRecordScreen() {
        if(virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();
        destroyMediaProjection();
    }

    private void destroyMediaProjection() {
        if(mediaProjection != null) {
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    toggleScreenShare();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO
                            }, REQUEST_PERMISSION);
                }
            }
        }
        return;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (!wasRecording && Helper.isRecording() || wasRecording && !Helper.isRecording()) {    // if toggle was updated, toggle recording
            startRecording();
        }
    }
}