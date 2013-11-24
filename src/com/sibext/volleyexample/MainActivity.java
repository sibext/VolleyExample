package com.sibext.volleyexample;

import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import static android.os.SystemClock.sleep;

public class MainActivity extends Activity {
    private int direction = 1, count0 = 0, count1 = 0, score = 5, delay = 1000;
    private int gravity, margin_l, margin_t, margin_r;
    private Button button;
    private LinearLayout ballLayout;
    private enum Places {center,left,right,neargrid};
    boolean flag = false;
    boolean winner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ballLayout = (LinearLayout) findViewById(R.id.ball_layout);
        VolleyHelper.init(this);
        button = (Button)findViewById(R.id.button); 
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    new myAsyncTask().execute();
                } else {
                    flag = false;
                }
			}
		});

    }

    class myAsyncTask extends AsyncTask<Void, Integer, Integer> {

        protected Integer doInBackground(Void... params) {
            while (flag) {
                serverQueue();
                sleep(delay);
                publishProgress((int) 0);
            }

            return direction;
        }

        protected void onProgressUpdate(Integer... value) {
        }

        protected void onPostExecute(Integer value) {
            if (winner) {
                Toast toast = Toast.makeText(MainActivity.this, "Player " + value + " win!", Toast.LENGTH_LONG);
                toast.show();
                winner = false;
            }
        }

    }

    private void serverQueue() {
        button.setClickable(false);
        RequestQueue queue = VolleyHelper.getRequestQueue();
        JSONObject object = new JSONObject();
        try {
            object.put("direction", direction);
            JsonObjectRequest volleyRequest = new JsonObjectRequest(Method.POST,
                    "http://volley.sibext.com/",
                    object,
                    createMyReqSuccessListener(),
                    createMyReqErrorListener());

            queue.add(volleyRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                	String result = response.getString("result");
                    String place  = response.getString("place").replaceAll("\\s+","");

                    Places currentPlace = Places.valueOf(place);

                    gravity = 16 + 3 + 2*direction;
                    margin_l = -100*(direction-1);
                    margin_r = 100*direction;
                    margin_t = 0;

                    switch (currentPlace) {
                        case center:
                            break;
                        case right:
                            margin_t = 100 - 200*direction;
                            break;
                        case left:
                            margin_t = -100 + 200*direction;
                            break;
                        case neargrid:
                            gravity = 17;
                            margin_l = -50+100*direction;
                            margin_r = 0;
                            break;
                        default:
                            break;
                    }

                    if (result.equals("out")) {
                        if (currentPlace == Places.neargrid){
                            gravity = 81;
                        } else {
                            margin_l = 0;
                            margin_r = 0;
                        }

                    }

                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) ballLayout.getLayoutParams();
                    lParams.gravity = gravity;
                    ballLayout.setLayoutParams(lParams);

                    LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) ballLayout.getLayoutParams();
                    params.setMargins(margin_l,margin_t,margin_r,0);
                    ballLayout.setLayoutParams(params);

                    TextView textView = (TextView) findViewById(R.id.t_view);
                    textView.setText("-> " + direction + " (" + result + ") ("+place+")");

                    if (!result.equals("goal")) {
	                    direction = 1 - direction;
                    } else {
                        count0 = count0 + direction;
                        count1 = count1 + (1 - direction);
                    }

                    TextView countText = (TextView) findViewById(R.id.count);
                    countText.setText(" 0: " + count0 + " | 1: " + count1);

                    if (count0 == score) {
                        winner = true;
                        flag = false;
                        direction = 0;
                        count0 = 0;
                        count1 = 0;
                    }

                    if (count1 == score) {
                        winner = true;
                        flag = false;
                        direction = 1;
                        count0 = 0;
                        count1 = 0;
                    }

                    button.setClickable(true);
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
