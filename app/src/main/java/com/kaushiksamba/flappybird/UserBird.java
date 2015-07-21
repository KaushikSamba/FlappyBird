package com.kaushiksamba.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class UserBird
{
    private int x, y;
    private int dx, dy;
    public float accY=0;
    private int width, height;

    public UserBird(int y)
    {
        height = 80;
        width = 80;
        this.y = y;
        x = 60;
    }
    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(x,y,x+width,y+height,paint);
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public void upthrust(){accY+=4;}
    public void decAccY(){accY-=0.6;}
    public void incAccY(){accY+=2.2;}
    public void update()
    {
        dy = (int) accY;
        y+=dy/2;
//        x+=3;
    }

    public Rect getRect()
    {
        return new Rect(x,y,x+width,y+height);
    }

    public void resetAccY(){accY=0;}
}
