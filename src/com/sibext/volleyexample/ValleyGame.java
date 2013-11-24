package com.sibext.volleyexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class ValleyGame extends View{
    Context context;
    private boolean initFlag = false;
    private int height;
    private int width;
    private static int padding = 5;
    private int picSide = 50;
    private int outWidth = picSide+padding;//(int) convertPixelsToDp(picSide)+padding;

    private BallPosition startBallPosition;// = new BallPosition("out_center_1", width, height/2);
    private BallPosition curBallPosition;

    ArrayList<BallPosition> ballPositionList = new ArrayList<BallPosition>();
    private Paint paint = new Paint();

    public int getContentWidth(){
        return width;
    }
    public int getContentHeight(){
        return height;
    }
    public ValleyGame(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        //init();
        addPositions();
    }

    public ValleyGame(Context context){
        this(context, null);
        this.context = context;
        addPositions();
        //init();
        //curBallPosition = startBallPosition;
    }

    private void addPositions(){
        //Out
        ballPositionList.add(new BallPosition("out_left_0", width/4, 0));
        ballPositionList.add(new BallPosition("out_center_0", 0, height/2));
        ballPositionList.add(new BallPosition("out_right_0", width/4, height));
        ballPositionList.add(new BallPosition("out_left_1", width*3/4, 0));
        ballPositionList.add(new BallPosition("out_center_1", width, height/2));
        ballPositionList.add(new BallPosition("out_right_1", width*3/4, height));
        ballPositionList.add(new BallPosition("out_near grid_1", width/2+outWidth, height));

        //Goal
        /*ballPosition = new BallPosition("goal_left_0", width/4, outWidth);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_center_0", outWidth, height/2-outWidth/2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_right_0", width/4, height-outWidth*2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_near grid_0", width/2-outWidth, outWidth);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_left_1", width*3/4, outWidth);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_center_1", width-outWidth*2, height/2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_right_1", width*3/4, height-outWidth*2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("goal_near grid_1", width/2+outWidth, height-outWidth*2);
        ballPositionList.add(ballPosition);

        //Caught
        ballPosition = new BallPosition("caught_left_0", width/4, outWidth);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_center_0", outWidth, height/2-outWidth/2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_right_0", width/4, height-outWidth*2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_near grid_0", width/2-outWidth, outWidth);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_left_1", width*3/4, outWidth);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_center_1", width-outWidth*2, height/2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_right_1", width*3/4, height-outWidth*2);
        ballPositionList.add(ballPosition);
        ballPosition = new BallPosition("caught_near grid_1", width/2+outWidth, height-outWidth*2);
        ballPositionList.add(ballPosition);*/
    }

    private void init(){
        startBallPosition = new BallPosition("out_center_1", width, height/2);
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
        Toast.makeText(context, "invalidate!!!", Toast.LENGTH_LONG).show();
        if(!initFlag){
            init();
            addPositions();
            initFlag = true;
        }
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        canvas.drawLine(0, 0, width, height, paint);
        canvas.drawLine(width, 0, 0, height, paint);

        paint.setColor(Color.RED);
        canvas.drawLine(width/2, 0, width/2, height, paint);
        canvas.drawLine(width, height/2, 0, height/2, paint);

        paint.setColor(Color.GREEN);
        paint.setTextSize(height/10);

       // Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        //Canvas over = new Canvas(bitmap);

        canvas.drawText((curBallPosition.getPosX()-picSide) + " | " + (curBallPosition.getPosY()-picSide/2), width/4, height/8, paint);

        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.valleyball), curBallPosition.getPosX()-picSide, curBallPosition.getPosY()-picSide/2, null);

        //over.drawBitmap(bitmap, 0, 0, null);
        //.draw(over);
        //paint.setColor(Color.CYAN);
        //canvas.drawCircle(width/2, height/2, width/4, paint);

    }

    public void setCurBallPosition(BallPosition bp){
        this.curBallPosition = bp;
    }

    public BallPosition getCurBallPosition(){
        return this.curBallPosition;
    }

    public BallPosition findPosition(String responseBP){
        for(BallPosition bp : ballPositionList){
            if(bp.getName() == responseBP){
                this.curBallPosition = bp;
                Toast.makeText(context, "bpname = "+bp.getName()+" | responseBP = "+responseBP, Toast.LENGTH_LONG).show();
            }
        }
        return this.curBallPosition;
    }

    public float convertPixelsToDp(float px){
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}
