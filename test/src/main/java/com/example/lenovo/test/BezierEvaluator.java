package com.example.lenovo.test;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * BezierEvaluator
 *
 * @author: lenovo
 * @time: 2016/9/16 16:27
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF point1;//控制点1
    private PointF point2;//控制点2

    public BezierEvaluator(PointF point1, PointF point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        final float t = fraction;
        float minusT = 1.0f - t;
        PointF point = new PointF();

        PointF point0 = startValue;

        PointF point3 = endValue;

        point.x = minusT * minusT * minusT * (point0.x)
                + 3 * minusT * minusT * t * (point1.x)
                + 3 * minusT * t * t * (point2.x)
                + t * t * t * (point3.x);

        point.y = minusT * minusT * minusT * (point0.y)
                + 3 * minusT * minusT * t * (point1.y)
                + 3 * minusT * t * t * (point2.y)
                + t * t * t * (point3.y);

        return point;
    }
}
