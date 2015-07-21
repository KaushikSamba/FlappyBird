package com.kaushiksamba.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Pipe
{
    int width = 120;
    boolean updated = false;
    int score = 0;
    Rect topRect, bottomRect;
    public Pipe(int x, int tot_height,int topPipeHeight)
    {
        topRect = new Rect(); //left top right bottom
        bottomRect = new Rect();
        bottomRect.bottom = tot_height;
        topRect.top = 0;
        topRect.bottom = topPipeHeight;
        bottomRect.top = topPipeHeight + 250;
        topRect.left = bottomRect.left = x;
        topRect.right = bottomRect.right = x+width;
    }

    public int getScore()
    {
        return score;
    }

    public void update()
    {
        topRect.left-=7;
        topRect.right-=7;
        bottomRect.left-=7;
        bottomRect.right-=7;
        if(!updated)
        {
            if(topRect.centerX()<60)
            {
                score=10;
                updated = true;
            }
        }
    }
    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(topRect,paint);
        canvas.drawRect(bottomRect, paint);
    }

    public Rect getTopRect()
    {
        return topRect;
    }

    public Rect getBottomRect()
    {
        return bottomRect;
    }
}
