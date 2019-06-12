package com.kaze.jailbreakpong;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);

        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                // Do some drawing when surface is ready
                Canvas canvas = surfaceHolder.lockCanvas();
                Paint paint = new Paint();

                // Up to 200 bricks
                Brick[] bricks = new Brick[200];
                int numBricks = 0;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screenX = size.x;
                int screenY = size.y;

                int brickWidth = screenX / 8;
                int brickHeight = screenY / 10;

                // Build a wall of bricks
                numBricks = 0;

                for(int column = 0; column < 8; column ++ ){
                    for(int row = 0; row < 3; row ++ ){
                        bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                        numBricks ++;
                    }
                }

                // Change the brush color for drawing
                paint.setColor(Color.argb(255,  249, 129, 0));

                // Draw the bricks if visible
                for(int i = 0; i < numBricks; i++){
                    if(bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }
                surfaceHolder.unlockCanvasAndPost(canvas);

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });


    }
}
