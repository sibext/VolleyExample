package com.sibext.volleyexample;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnClickListener{
    private static int endGameScore = 20;
    private static int leftTeam = 0, rightTeam = 1;

    private int ballPosition = 1;
    private String lastResponse;
    private String gameStatus = "stop";
    private int leftTeamScore = 0, rightTeamScore = 0;

    private int height;
    private int width;
    private int picSide = 50;
    private int outWidth = picSide+5;
    private BallPosition startBallPosition;// = new BallPosition("out_center_1", width, height/2);
    private BallPosition curBallPosition;

    private Button ppButton, stopButton, closeButton;
    private LinearLayout canvasView;
    private TextView leftTeamScoreView, rightTeamScoreView;

    private ValleyAsyncPlay task;

    private ValleyGame vg;
    private BallPosition curBP;
    ArrayList<BallPosition> ballPositionList = new ArrayList<BallPosition>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_valley_playground);

        ppButton = (Button)findViewById(R.id.ppButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        closeButton = (Button)findViewById(R.id.closeButton);

        ppButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);

        canvasView = (LinearLayout)findViewById(R.id.canvasWrap);

        vg = new ValleyGame(this);
        canvasView.addView(vg);

        startBallPosition = new BallPosition("out_center_1", width, height/2);
        curBallPosition = startBallPosition;

        addPositions();

        leftTeamScoreView = (TextView)findViewById(R.id.leftTeamScore);
        rightTeamScoreView = (TextView)findViewById(R.id.rightTeamScore);

        VolleyHelper.init(this);
    }

    public class ValleyAsyncPlay extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                do{
                    if(isCancelled() == true) break;
                    TimeUnit.MILLISECONDS.sleep(500);
                    RequestQueue queue = VolleyHelper.getRequestQueue();
                    JSONObject object = new JSONObject();
                    try {
                        object.put("direction", ballPosition);
                        JsonObjectRequest volleyRequest = new JsonObjectRequest(Method.POST,
                                "http://volley.sibext.com/",
                                object,
                                createMyReqSuccessListener(),
                                createMyReqErrorListener());

                        queue.add(volleyRequest);
                        //TextView textView = new TextView(MainActivity.this);
                        // textView.setText("-> " + direction + "");
                        // resultView.addView(textView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }while(gameStatus != "end");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            Toast.makeText(MainActivity.this, "End game! " + whoWin() + " win this match!", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(MainActivity.this, "Game cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

    private String whoWin(){
        String whoWin;

        if(leftTeamScore > rightTeamScore){
            whoWin = "Left Team";
        }else{
            whoWin = "Right Team";
        }

        return whoWin;
    }

    public void startValleyPlay() {
        task = new ValleyAsyncPlay();
        task.execute();
    }

    public void stopValleyPlay() {
        if (task == null) return;
        task.cancel(true);
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                	String result = response.getString("result");
                    String place = response.getString("place");
                    String posName = result+"_"+place+"_"+ballPosition;
                    if (!result.equals("goal")) {
                        if (ballPosition == rightTeam) {
                            ballPosition = leftTeam;
                        } else {
                            ballPosition = rightTeam;
                        }
                    }else{
                        if (ballPosition == rightTeam) {
                            rightTeamScore++;
                            rightTeamScoreView.setText("R: "+ rightTeamScore);
                        } else {
                            leftTeamScore++;
                            leftTeamScoreView.setText("L: "+ leftTeamScore);
                        }
                        if(leftTeamScore == endGameScore || rightTeamScore == endGameScore){
                            gameStatus = "end";
                        }
                    }

                    curBP = curBallPosition;

                    if(curBP.getName() != posName){
                        //curBP = vg.findPosition(posName);
                        //Toast.makeText(MainActivity.this, "input name = " + posName + " | curName = " +curBP.getName(), Toast.LENGTH_LONG).show();

                        for(BallPosition bp : ballPositionList){
                            if(bp.getName() == posName){
                                //Toast.makeText(MainActivity.this, "input name = " + posName + " | curName = " +curBP.getName(), Toast.LENGTH_LONG).show();
                                vg.setCurBallPosition(bp);
                                curBallPosition = bp;
                                canvasView.invalidate();
                            }
                        }
                        //canvasView.removeAllViews();
                        //canvasView.addView(vg);
                        //vg.invalidate();
                        //canvasView.invalidate();
                        //vg.update();
                        //canvasView.invalidate();
                    }
                    //canvasView.invalidate();

                    //TextView textView = ((TextView)resultView.getChildAt(resultView.getChildCount() - 1));
                    //textView.setText(textView.getText() + " (" + result + " | " + place + ")");
                    //Toast.makeText(MainActivity.this, "-> " + ballPosition + " (" + result + " | " + place + ")", Toast.LENGTH_LONG).show();
                    //invalidate();
                    //ppButton.setClickable(true);
                    /*if(leftTeamScore == endGameScore || rightTeamScore == endGameScore){
                        ppButton.setClickable(false);
                    } */
                } catch (JSONException e) {
                	Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Toast.makeText(MainActivity.this, "Error = " + error.getMessage(), Toast.LENGTH_LONG).show();
                ppButton.setClickable(true);
            }
        };
    }

    public void init(){
        gameStatus = "stop";
        leftTeamScore = rightTeamScore = 0;
        rightTeamScoreView.setText("R: "+ rightTeamScore);
        leftTeamScoreView.setText("L: "+ leftTeamScore);
        ballPosition = 1;
        canvasView.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.closeButton :
                finish();
                break;
            case R.id.stopButton :
                stopValleyPlay();
                ppButton.setClickable(true);

                break;
            case R.id.ppButton :
                init();
                gameStatus = "play";
                startValleyPlay();
                ppButton.setClickable(false);

                break;
        }
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
}
