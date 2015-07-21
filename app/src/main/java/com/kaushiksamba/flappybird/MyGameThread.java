package com.kaushiksamba.flappybird;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MyGameThread extends Thread
{
    private int FPS = 30;
    private SurfaceHolder surfaceHolder;
    private MainActivity.Screen screen;
    private boolean running;
    public static Canvas canvas;

    public MyGameThread(SurfaceHolder surfaceHolder, MainActivity.Screen screen)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.screen = screen;
    }

    public void setRunning(boolean x)
    {
        running = x;
    }

    @Override
    public void run()
    {
        long startTime;
        long timeMillis;
        long waitTime;
        long targetTime = 1000/FPS;

        while(running)
        {
            startTime = System.nanoTime();
            canvas = null;
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    this.screen.update();
                    this.screen.draw(canvas);
                }
            }
            catch (Exception e){e.printStackTrace();}
            finally
            {
                if(canvas!=null)
                {
                    try
                    {
                        this.surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e) { e.printStackTrace();}
                }
            }

            timeMillis = (System.nanoTime() - startTime)/1000000;
            waitTime = targetTime - timeMillis;
            try {
                if(waitTime>0) this.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
