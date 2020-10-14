package com.mp.basekit.util.toast.anim;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class AnimPath {

    static List<Path> getWarningPaths(float width, float height) {
        List<Path> paths = new ArrayList<>();
        Path circle = new Path();
        circle.addArc(new RectF(0 + width / 20f, 0 + width / 20f, width - width / 20f, height - width / 20f)
                , 0f, 360f);
        paths.add(circle);
        Path longLine = new Path();
        longLine.moveTo(width / 2f, height * 0.2f);
        longLine.lineTo(width / 2f, height * 0.6f);
        paths.add(longLine);
        Path shortLine = new Path();
        shortLine.moveTo(width / 2f, height * 0.7f);
        shortLine.lineTo(width / 2f, height * 0.8f);
        paths.add(shortLine);
        return paths;
    }

    static void setWarningPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FFA900"));
    }

    static List<Path> getSucPaths(float width, float height) {
        float x1 = width * 0.2f, y1 = height * 0.53f;
        float x2 = width * 0.36f, y2 = height * 0.7f;
        float x3 = width * 0.77f, y3 = height * 0.23f;
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        List<Path> paths = new ArrayList<>();
        paths.add(path);
        return paths;
    }

    static void setSucPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FF2BC93E"));
    }
}
