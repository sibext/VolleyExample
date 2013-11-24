package com.sibext.volleyexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

public class ValleyGame extends View{
    Context context;
    private boolean initFlag = false;
    private int height;
    private int width;
    private Bitmap myBall = BitmapFactory.decodeResource(getResources(), R.drawable.valleyball);
    private int padding = 5;
    private int picSide = myBall.getHeight();
    private int outWidth = picSide+padding*2;
    private String ballStatus = "out";

    private BallPosition startBallPosition;
    BallPosition curBallPosition;

    ArrayList<BallPosition> ballPositionList = new ArrayList<BallPosition>();
    private Paint paint = new Paint();

    public ValleyGame(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        addPositions();
    }

    public ValleyGame(Context context){
        this(context, null);
        this.context = context;
        addPositions();
    }

    public void setInitFlag(boolean flag){
        this.initFlag = flag;
    }

    private void addPositions(){
        //Out
        ballPositionList.add(new BallPosition("out_left_0", width/4-outWidth, padding));
        ballPositionList.add(new BallPosition("out_center_0", padding, height/2-outWidth/2));
        ballPositionList.add(new BallPosition("out_right_0", width/4-outWidth, height-outWidth));
        ballPositionList.add(new BallPosition("out_near grid_1", width/2-outWidth, padding));
        ballPositionList.add(new BallPosition("out_left_1", width*3/4, padding));
        ballPositionList.add(new BallPosition("out_center_1", width-outWidth, height/2-outWidth/2));
        ballPositionList.add(new BallPosition("out_right_1", width*3/4, height-outWidth));
        ballPositionList.add(new BallPosition("out_near grid_1", width/2+padding, height-outWidth));

        //Goal
        ballPositionList.add(new BallPosition("goal_left_0", width/4, outWidth*2));
        ballPositionList.add(new BallPosition("goal_center_0", outWidth*2, height/2-outWidth/2));
        ballPositionList.add(new BallPosition("goal_right_0", width/4, height-outWidth*2));
        ballPositionList.add(new BallPosition("goal_near grid_1", width/2-outWidth, outWidth));
        ballPositionList.add(new BallPosition("goal_left_1", width*3/4+outWidth, outWidth*2));
        ballPositionList.add(new BallPosition("goal_center_1", width-outWidth*2, height/2-outWidth/2));
        ballPositionList.add(new BallPosition("goal_right_1", width*3/4+outWidth, height-outWidth*2));
        ballPositionList.add(new BallPosition("goal_near grid_1", width/2+padding, height-outWidth*2));

        //Caught
        ballPositionList.add(new BallPosition("caught_left_0", width/4, outWidth*2));
        ballPositionList.add(new BallPosition("caught_center_0", outWidth*2, height/2-outWidth/2));
        ballPositionList.add(new BallPosition("caught_right_0", width/4, height-outWidth*2));
        ballPositionList.add(new BallPosition("caught_near grid_1", width/2-outWidth, outWidth));
        ballPositionList.add(new BallPosition("caught_left_1", width*3/4+outWidth, outWidth*2));
        ballPositionList.add(new BallPosition("caught_center_1", width-outWidth*2, height/2-outWidth/2));
        ballPositionList.add(new BallPosition("caught_right_1", width*3/4+outWidth, height-outWidth*2));
        ballPositionList.add(new BallPosition("caught_near grid_1", width/2+padding, height-outWidth*2));
    }

    private void init(){
        startBallPosition = new BallPosition("out_center_1", width-outWidth, height/2-outWidth/2);
        curBallPosition = startBallPosition;
    }

    @Override
    protected void onMeasure(int widthSpecId, int heightSpecId){
        this.height = View.MeasureSpec.getSize(heightSpecId);
        this.width = View.MeasureSpec.getSize(widthSpecId);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //Toast.makeText(context, "invalidate!!!", Toast.LENGTH_LONG).show();
        if(!initFlag){
            init();
            addPositions();
            initFlag = true;
        }
        paint.setColor(Color.GREEN);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(outWidth, outWidth, width-outWidth, height-outWidth, paint);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        canvas.drawLine(width/2-5, 0, width/2-5, height, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(height/14);

        canvas.drawText(ballStatus, 20, height/14+10, paint);

        canvas.drawBitmap(myBall, curBallPosition.getPosX(), curBallPosition.getPosY(), null);
    }

    public void setCurBallPosition(BallPosition bp){
        this.curBallPosition = bp;
    }

    public void setBallStatus(String ballStatus) {
        this.ballStatus = ballStatus;
    }
}
