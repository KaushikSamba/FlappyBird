package com.kaushiksamba.flappybird;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Removing title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Opening Gamepanel and starting the game
        setContentView(new Screen(this));
    }

    public class Screen extends SurfaceView implements SurfaceHolder.Callback
    {
        private MyGameThread thread;
        private boolean isPlaying = true;
        private boolean yetToStart = true;
        private UserBird bird;
        private int score;
        private long pipeStartTime = 0;
        private long lostTime = 0;
        private ArrayList<Pipe> pipeList = new ArrayList<>();
        Random rand = new Random();

        public Screen(Context context)
        {
            super(context);
            //Add callbacks to surfaceholder to "intercept" events
            getHolder().addCallback(this);
            //Make Gamepanel focusable
            setFocusable(true);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {
            bird = new UserBird(getHeight()/2);
            score = 0;
            pipeList.add(new Pipe(4*getWidth()/5, getHeight(), getHeight() / 6));
            //Start the game loop
            thread = new MyGameThread(getHolder(),MainActivity.Screen.this);
            thread.setRunning(true);
            thread.start();
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {

        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            boolean retry = true;
            int counter = 0;
            while(retry && counter <1000)
            {
                counter++;
                try
                {
                    thread.setRunning(false);
                    thread.join();
                    retry = false;
                    thread = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            if(yetToStart)
            {
                isPlaying = true;
                yetToStart = false;
            }
            else if(isPlaying)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bird.accY = -24;
                }
            }
            else
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if((System.nanoTime() - lostTime)/1000000>500) resetGame();
                }
            }
            return super.onTouchEvent(event);
        }

        private void resetGame()
        {
            bird = new UserBird(getHeight()/2);
            score = 0;
            pipeList.clear();
            pipeList.add(new Pipe(getWidth() / 2, getHeight(), getHeight() / 6));
            pipeStartTime=0;
            yetToStart = true;
            isPlaying = true;
            lostTime = 0;
        }
        private int getRandomHeight()
        {
            return 2+rand.nextInt(7);
        }
        public void update()
        {
            if(isPlaying && !yetToStart)
            {
                bird.incAccY();
                bird.update();
//                pipe.update();
                if(pipeStartTime==0)
                {
                    pipeStartTime = System.nanoTime();
                }
                if((System.nanoTime()-pipeStartTime)/1000000>1600-score)
                {
                    pipeList.add(new Pipe(getWidth(),getHeight(),getHeight()/getRandomHeight()));
                    pipeStartTime = System.nanoTime();
                }
                for(int i=0;i<pipeList.size(); i++)
                {
                    pipeList.get(i).update();
                    if(pipeList.get(i).topRect.right<=0)
                    {
                        score += pipeList.get(i).getScore();
                        pipeList.remove(i);
                    }
                    checkCollision(i);
                }
                if(bird.getY() + bird.getHeight()>getHeight() || bird.getY()<0)
                {
                    isPlaying = false;
                    System.out.println(score);
                    lostTime = System.nanoTime();
                }
            }
        }

        private void checkCollision(int i)
        {
            Rect birdRect = bird.getRect(), topRect = pipeList.get(i).getTopRect(), bottomRect = pipeList.get(i).getBottomRect();
            if(Rect.intersects(birdRect,topRect) || Rect.intersects(birdRect,bottomRect))
            {
                isPlaying = false;
                System.out.println(score);
                lostTime = System.nanoTime();
            }
        }

        @Override
        public void draw(Canvas canvas)
        {
            if(canvas!=null)
            {
                //Call canvas draw functions here
                if(isPlaying)
                {
                    canvas.drawColor(Color.WHITE);
                    bird.draw(canvas);
//                pipe.draw(canvas);
                    for (int i = 0; i < pipeList.size(); i++) {
                        pipeList.get(i).draw(canvas);
                    }
                    //Printing the score on the top right of the screen
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(100);
                    String score_string = Integer.toString(score);
                    canvas.drawText(score_string, 6 * getWidth() / 7 - 20 * (score_string.length() - 1), getHeight() / 8, paint);
                }
                else
                {
                    canvas.drawColor(Color.BLACK);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(200);
                    canvas.drawText("YOU LOST", getWidth() / 7, getHeight() / 2, paint);
                    paint.setTextSize(75);
                    canvas.drawText("Press to retry", 3*getWidth()/10,2*getHeight()/3,paint);
                    paint.setTextSize(60);
                    paint.setColor(Color.YELLOW);
                    canvas.drawText("Score: " + Integer.toString(score),3*getWidth()/8,5*getHeight()/6,paint);
                }
                if(yetToStart)
                {
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(200);
                    canvas.drawText("Tap to start", getWidth()/9, getHeight()/2, paint);
                }
            }
        }
    }
}
