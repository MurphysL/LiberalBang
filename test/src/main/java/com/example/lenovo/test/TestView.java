package com.example.lenovo.test;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * TestView
 *
 * @author: lenovo
 * @time: 2016/9/16 21:56
 */

public class TestView extends View implements View.OnTouchListener{

    private Paint bPaint;
    private Paint pPaint;
    private Paint lPaint;
    private PointF pointF;

    int temp = 0;
    private List<PointF> list = new ArrayList<>();

    public TestView(Context context) {
        super(context);
        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        bPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bPaint.setColor(Color.BLUE);

        pPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        pPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pPaint.setColor(Color.GRAY);

        lPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
        lPaint.setStyle(Paint.Style.STROKE);
        lPaint.setStrokeWidth(1);
        lPaint.setColor(Color.GRAY);
        lPaint.setTextSize(20);

        this.setOnTouchListener(this);
    }

    public void startAnimator() {
        PointF p0 = list.get(0);
        PointF p1 = list.get(1);
        PointF p2 = list.get(2);
        PointF p3 = list.get(3);

        ValueAnimator animator = ValueAnimator.ofObject(
                new BezierEvaluator(new PointF(p1.x , p1.y), new PointF(p2.x , p2.y))
                ,new PointF(p0.x , p0.y), new PointF(p3.x , p3.y));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pointF = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(5000);
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0 ; i < list.size() ; i ++){
            PointF p = list.get(i);
            canvas.drawCircle(p.x , p.y , 5 ,pPaint);
            canvas.drawText("[" + (int)p.x + "," + (int)p.y + "]" , 50 , 50 + i * 30 , lPaint);
        }

        temp ++;
        Path path = new Path();
        switch (list.size()){
            case 2:
                canvas.drawLine(list.get(0).x , list.get(0).y , list.get(1).x , list.get(1).y , lPaint);
                break;
            case 3:
                path.moveTo(list.get(0).x , list.get(0).y);
                path.quadTo(list.get(1).x , list.get(1).y , list.get(2).x , list.get(2).y);
                canvas.drawPath(path , lPaint);
                break;
            case 4:
                path.moveTo(list.get(0).x , list.get(0).y);
                path.cubicTo(list.get(1).x , list.get(1).y , list.get(2).x , list.get(2).y , list.get(3).x , list.get(3).y);
                canvas.drawPath(path , lPaint);
                if(pointF == null){
                    canvas.drawCircle(list.get(0).x , list.get(0).y , 40 ,bPaint);
                    startAnimator();
                }else{
                    canvas.drawCircle(pointF.x , pointF.y , 40 , bPaint);
                }
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(list.size() < 4){
            PointF pointF = new PointF();
            pointF.x = event.getX();
            pointF.y = event.getY();

            list.add(pointF);
        }else if(list.size() == 4){
            list.clear();
            temp = 0;
            pointF = null;
        }
        invalidate();
        return false;
    }

}
