package com.textusurfa.camara.simaski.pruebatextusurfa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF14BBFF;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //brush sizes
    private float brushSize, lastBrushSize;
    //erase flag
    private boolean erase=false;

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();



    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private Map<Path, Integer> colorsMap = new HashMap<Path, Integer>();

    //setup drawing
    private void setupDrawing(){

        //prepare for drawing and setup paint stroke properties
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //canvas.drawPath(drawPath, drawPaint);
        for (Path p : paths){
            drawPaint.setColor(colorsMap.get(p));
            canvas.drawPath(p, drawPaint);
        }
        drawPaint.setColor(paintColor);
        canvas.drawPath(drawPath, drawPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {

        drawPath.lineTo(mX, mY);
        // commit the path to our offscreen
        //drawCanvas.drawPath(drawPath, drawPaint);
        // kill this so we don't double draw
        paths.add(drawPath);
        drawPath = new Path();


    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //drawPath.moveTo(touchX, touchY);
                touch_start(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //drawPath.lineTo(touchX, touchY);
                touch_move(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //drawPath.lineTo(touchX, touchY);
                //drawCanvas.drawPath(drawPath, drawPaint);
                //drawPath.reset();
                //paths.add(drawPath);
                colorsMap.put(drawPath,paintColor);
                touch_up();
                invalidate();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;

    }

    //update color
    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    //set brush size
    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    //get and set last brush size
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    //set erase true or false
    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    //start new drawing
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void undo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
        else
        {
            Toast.makeText(getContext(), "noooooooo", Toast.LENGTH_SHORT).show();
        }
        //toast the user
    }

}