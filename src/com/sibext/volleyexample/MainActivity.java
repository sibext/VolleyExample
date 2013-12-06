package com.sibext.volleyexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends Activity {
    private int direction = 1;
    private ViewGroup resultView;
    private Button button;
    private Button stop;
    private ViewGroup ball;
    private ViewGroup feild;
    private TextView scoresView;
    private RequestQueue queue;

    private Boolean isStopped = false;
    
    int CenterFeildX = 0;
    int CenterFeildY = 0;
    
    int team_score0 = 0;
    int team_score1 = 0;
   
	Object synchObject = new Object();
	
	@Override
	protected void onDestroy() {
		if (queue != null)
		{
			queue.stop();
			queue.cancelAll(new String("tag"));
		}
		super.onDestroy();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (ViewGroup)findViewById(R.id.result);
        ball = (ViewGroup)findViewById(R.id.ball);
        feild = (ViewGroup)findViewById(R.id.feild);
        scoresView = (TextView)findViewById(R.id.score);
        stop = (Button)findViewById(R.id.button_stop);
        button = (Button)findViewById(R.id.button);
    	CenterFeildX = feild.getLayoutParams().width / 2;
    	CenterFeildY = feild.getLayoutParams().height / 2;
		scoresView.setText(R.string.score1);
		team_score0 = team_score1 = 0;
		
		VolleyHelper.init(this);
        queue = VolleyHelper.getRequestQueue();
        
        stop.setEnabled(false);
        button.setEnabled(true);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resultView.removeAllViews();
				button.setEnabled(false);
				stop.setEnabled(true);
				isStopped = false;
				scoresView.setText(R.string.score1);
				team_score0 = team_score1 = 0;
				TextView textView1 = new TextView(MainActivity.this);
				textView1.setText("-> " + direction + "");
				resultView.addView(textView1);
				ValleyRequest req = new ValleyRequest();
				
				try
				{
					queue.start();
					GsonRequest<ValleyResponse> responce = new GsonRequest<ValleyResponse>(Method.POST,
							"http://volley.sibext.com/", req, createMyReqErrorListener(), GsonReqSuccessListner(), ValleyResponse.class);
					responce.setTag(new String("tag"));
					queue.add(responce);
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
					button.setClickable(true);
					button.setEnabled(true);
				}
				//game = new VolleyGame();
				//game.execute(synchObject);
				/*JSONObject object = new JSONObject();
				try {
					object.put("direction", direction);
					JsonObjectRequest volleyRequest = new JsonObjectRequest(Method.POST,
							"http://volley.sibext.com/",
							object,
							createMyReqSuccessListener(),
							createMyReqErrorListener());
					
					queue.add(volleyRequest);
					TextView textView = new TextView(MainActivity.this);
					textView.setText("-> " + direction + "");
					resultView.addView(textView);
				} catch (JSONException e) {
					e.printStackTrace();
				}*/
				
			}
		});
        
        stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				synchronized (isStopped){
					
				isStopped = true;
				queue.stop();
				queue.cancelAll(new String("tag"));
				button.setEnabled(true);
				stop.setEnabled(false);
				
				Toast t;
				
				if (team_score0 > team_score1)
					t = Toast.makeText(MainActivity.this, "Победила команда 0", Toast.LENGTH_LONG);
				else if (team_score0 < team_score1)
					t = Toast.makeText(MainActivity.this, "Победила команда 1", Toast.LENGTH_LONG);
				else
					t = Toast.makeText(MainActivity.this, "Ничья", Toast.LENGTH_LONG);
				team_score0 = team_score1 = 0;
				t.show();
				}
			}
		});

    }
    private void ResponseAnalyze(String result, String place)
    {

    	int paddingLeft = 0;
    	int paddingTop = 0;
    	
    	int ballHeight = ball.getLayoutParams().height;
    	int ballWidth = ball.getLayoutParams().width;
    	
    	if (direction == 1)
    	{            	
    		paddingLeft += CenterFeildX;
    	}
    	
    	if (place.equalsIgnoreCase("center"))
    	{
    		paddingLeft += CenterFeildX / 2 - ballWidth / 2;
    		paddingTop += CenterFeildY - ballHeight / 2;
    		if (result.equalsIgnoreCase("out"))
    			if (direction == 1)
    				paddingLeft = CenterFeildX * 2 - ballWidth;
    			else
    				paddingLeft = 0;
    		
    	} else if (place.equalsIgnoreCase("right"))
    	{
       		paddingLeft += CenterFeildX / 2 - ballWidth / 2;
    		paddingTop += CenterFeildY - CenterFeildY / 2 - ballHeight / 2;
    		
    		if (result.equalsIgnoreCase("out"))
    			paddingTop = 0;
     	} else if (place.equalsIgnoreCase("left"))
     	{
       		paddingLeft += CenterFeildX / 2 - ballWidth / 2;
    		paddingTop += CenterFeildY  + CenterFeildY / 2 - ballHeight / 2;
    		if (result.equalsIgnoreCase("out"))
    			paddingTop = CenterFeildY * 2 - ballHeight;
     		
     	} else if (place.equalsIgnoreCase("near grid"))
     	{
       		paddingLeft = CenterFeildX + (ballWidth + 2) * (direction == 1 ? 1 : -1);
    		paddingTop += CenterFeildY  - ballHeight / 2;
    		if (result.equalsIgnoreCase("out"))
    			if (direction == 1)
    			paddingLeft -= ballWidth / 2;
    			else
    			paddingLeft += ballWidth / 2;
     	}
    	
    	MarginLayoutParams params = (MarginLayoutParams) ball.getLayoutParams();
    	params.setMargins(paddingLeft, paddingTop, 0, 0);
    	ball.setLayoutParams(params);
    	
    }
    
    private Response.Listener<ValleyResponse> GsonReqSuccessListner()
    {
    	return new Response.Listener<ValleyResponse>() {

			@Override
			public void onResponse(ValleyResponse response) {
              	String result = response.result;
            	String place = response.place;
            	synchronized(isStopped){
            		
            		if (isStopped)
            			return;
            		ResponseAnalyze(result, place);
            	
                	if (!result.equals("goal")) {
                    	if (direction == 1) {
                    		direction = 0;
                    	} else {
                    		direction = 1;
                    	}
                	} else {
                    	if (direction == 1) {
                    		team_score1++;
                    	} else {
                    		team_score0++;
                    	}
                		scoresView.setText("счет " + Integer.valueOf(team_score0).toString() + " : " + Integer.valueOf(team_score1).toString());
                	}
                	
                TextView textView = ((TextView)resultView.getChildAt(resultView.getChildCount() - 1));
                textView.setText(textView.getText() + " (" + result + ")" + "(" + place + ")");
                	
    				if (team_score0 == 20 || team_score1 == 20 )
    				{
    					Toast t;
    					if (team_score0 == 20)
    						t = Toast.makeText(MainActivity.this, "Победила команда 0", Toast.LENGTH_LONG);
    					else
    						t = Toast.makeText(MainActivity.this, "Победила команда 1", Toast.LENGTH_LONG);
    					button.setEnabled(true);
    					button.setClickable(true);
    				
    					stop.setClickable(false);
    					stop.setEnabled(false);
    					t.show();
    					team_score0 = team_score1 = 0;       				//endGame = true;
    				}
    				else
    				{
    					TextView textView1 = new TextView(MainActivity.this);
    					textView1.setText("-> " + direction + "");
    					
    					resultView.addView(textView1);
    					ValleyRequest req = new ValleyRequest();
    				
    					try
    					{
    						GsonRequest<ValleyResponse> responce = new GsonRequest<ValleyResponse>(Method.POST,
    								"http://volley.sibext.com/", req, createMyReqErrorListener(), GsonReqSuccessListner(), ValleyResponse.class);
    						responce.setTag(new String("tag"));
    						queue.add(responce);
    					
    					}
    					catch(Exception e)
    					{
    						e.printStackTrace();
    					//button.setClickable(true);
    					button.setEnabled(true);
    					}
    				}

            	}
        	}
    		
    	};
    }
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                	String result = response.getString("result");
                	String place = response.getString("place");
                	ResponseAnalyze(result, place);
                	
                    if (!result.equals("goal")) {
	                    if (direction == 1) {
	                    	direction = 0;
	                    } else {
	                    	direction = 1;
	                    }
                    } else {
	                    if (direction == 1) {
	                    	team_score1++;
	                    } else {
	                    	team_score0++;
	                    }
	                    
                    	scoresView.setText("счет " + Integer.valueOf(team_score0).toString() + " : " + Integer.valueOf(team_score1).toString());
                    }
                    
                    TextView textView = ((TextView)resultView.getChildAt(resultView.getChildCount() - 1));
                    textView.setText(textView.getText() + " (" + result + ")" + "(" + place + ")");
        			if (team_score0 == 20 || team_score1 == 20 )
        			{
        				Toast t;
        				if (team_score0 == 20)
        					t = Toast.makeText(MainActivity.this, "Победила команда 0", Toast.LENGTH_LONG);
        				else
        					t = Toast.makeText(MainActivity.this, "Победила команда 1", Toast.LENGTH_LONG);
        				button.setEnabled(true);
        				button.setClickable(true);
        				
        				stop.setClickable(false);
        				stop.setEnabled(false);
        				t.show();
        				team_score0 = team_score1 = 0;
        			}
        			else
        			{
        				TextView textView1 = new TextView(MainActivity.this);
        				textView1.setText("-> " + direction + "");
        				resultView.addView(textView1);
        				ValleyRequest req = new ValleyRequest();
        				
        				try
        				{
        					GsonRequest<ValleyResponse> responce = new GsonRequest<ValleyResponse>(Method.POST,
        							"http://volley.sibext.com/", req, createMyReqErrorListener(), GsonReqSuccessListner(), ValleyResponse.class);
        					queue.add(responce);
        					
        				}
        				catch(Exception e)
        				{
        					e.printStackTrace();
        					button.setEnabled(true);
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
            }
        };
    }
}
