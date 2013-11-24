package com.sibext.volleyexample;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnClickListener{
    private static int endGameScore = 20;
    private static int leftTeam = 0, rightTeam = 1;

    private int ballPosition = 1;
    private String gameStatus = "stop";
    private int leftTeamScore = 0, rightTeamScore = 0;

    private Button ppButton, stopButton, closeButton;
    private LinearLayout canvasView;
    private TextView leftTeamScoreView, rightTeamScoreView;

    private ValleyAsyncPlay task;

    private ValleyGame vg;

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
        init();
        task = new ValleyAsyncPlay();
        task.execute();
        gameStatus = "play";
        ppButton.setClickable(false);
    }

    public void stopValleyPlay() {
        if (task == null) return;
        task.cancel(true);
        ppButton.setClickable(true);
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
                            if(rightTeamScore != endGameScore) rightTeamScore++;
                            rightTeamScoreView.setText("R: "+ rightTeamScore);
                        } else {
                            if(leftTeamScore != endGameScore) leftTeamScore++;
                            leftTeamScoreView.setText("L: "+ leftTeamScore);
                        }
                        if(leftTeamScore == endGameScore || rightTeamScore == endGameScore){
                            gameStatus = "end";
                            ppButton.setClickable(true);
                        }
                    }

                    if(vg.curBallPosition.getName() != posName){
                        for(BallPosition bp : vg.ballPositionList){
                            if(bp.getName().equalsIgnoreCase(posName)){
                                vg.setCurBallPosition(bp);
                                vg.setBallStatus(result);
                                vg.invalidate();
                            }
                        }
                    }
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
        leftTeamScore = rightTeamScore = 0;
        rightTeamScoreView.setText("R: "+ rightTeamScore);
        leftTeamScoreView.setText("L: "+ leftTeamScore);
        vg.setInitFlag(false);
        vg.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.closeButton :
                finish();
                break;
            case R.id.stopButton :
                stopValleyPlay();
                break;
            case R.id.ppButton :
                startValleyPlay();
                break;
        }
    }
}
