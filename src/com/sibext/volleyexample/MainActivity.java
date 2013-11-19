package com.sibext.volleyexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.os.AsyncTask;
import android.widget.EditText;

public class MainActivity extends Activity {
    private int direction = 1;
//    private ViewGroup resultView;
    private Button button;
     TextView result_text;
    int count0,count1; TextView game_count_text; ImageView ball0,ball1,out_ball0,out_ball1;
    EditText edit_text_delay;
    RelativeLayout field0,field1,out0,out1; LayoutParams field0_laypar,field1_laypar,out0_laypar,out1_laypar;
    final static int MAX_COUNT=99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setTheme(android.R.style.Theme_Black_NoTitleBar);
        setContentView(R.layout.game_field);
        //resultView = (ViewGroup)findViewById(R.id.result);
        result_text=(TextView)findViewById(R.id.request_result);
        game_count_text=(TextView)findViewById(R.id.game_count);
        edit_text_delay=(EditText)findViewById(R.id.delay_time);
        //count0=count1=0;
        field0=(RelativeLayout)findViewById(R.id.field0);
        field1=(RelativeLayout)findViewById(R.id.field1);
        out0=(RelativeLayout)findViewById(R.id.out0);
        out1=(RelativeLayout)findViewById(R.id.out1);
        ball0=new ImageView(this); ball0.setImageResource(R.drawable.ic_launcher); ball0.setVisibility(View.GONE);
        ball1=new ImageView(this); ball1.setImageResource(R.drawable.ic_launcher); ball1.setVisibility(View.GONE);
        out_ball0=new ImageView(this); out_ball0.setImageResource(R.drawable.ic_launcher);
        out_ball0.setVisibility(View.GONE);
        out_ball1=new ImageView(this); out_ball1.setImageResource(R.drawable.ic_launcher);
        out_ball1.setVisibility(View.GONE);
        field0.addView(ball0); field1.addView(ball1);
        out0.addView(out_ball0); out1.addView(out_ball1);
        VolleyHelper.init(this);
        button = (Button)findViewById(R.id.start_button);
        button.setText("Start");
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
             if(button.getText()=="Start"){
			     count0=count1=0; game_count_text.setText("0:0");
                 button.setText("Stop");
                 new Runner().execute();
             }else
             {button.setText("Start"); count0=count1=0; game_count_text.setText("0:0");}
    } });
    }
    public void ShowToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
private void MakeTransaction(){
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
        //TextView textView = new TextView(MainActivity.this);
        result_text.setText("-> " + direction + "");
        //resultView.addView(textView);
    } catch (JSONException e) {
        e.printStackTrace();
    }

}
    class Runner extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... args){
        while(true){
            String delay_string=edit_text_delay.getText().toString();
            if(Math.max(count0,count1)>=MAX_COUNT){
                /*button.post(new Runnable(){
                public void run(){ button.setText("Start");}  });*/
                return "OK"; }
            if(button.getText()!="Start"){ publishProgress(); }
            if(button.getText()=="Start"){return "Game stopped";}
            long delay_msec=1000;
            try{Double double_value=new Double(delay_string); delay_msec=(long)(double_value*1000);
                Thread.sleep(delay_msec);
            }catch(Exception e){}
         }
        }
        @Override
        protected void onProgressUpdate(Void... vd){ MakeTransaction(); }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
           if(result=="OK"){
            int winner; if(count0>count1){winner=0;}else{winner=1;}
            if(count0!=count1){ShowToast("Winner is "+winner+".");}
            else{ShowToast("Have no winners.");}
           }
           else{ShowToast(result);}
            button.setText("Start");
    }
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            if(button.getText().equals("Stop")){
                try {
                	String result = response.getString("result");
                    String place=response.getString("place");
                    if(direction==0){
                        if(place.equals("near grid")){
                            field0_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                            if(result.equals("out")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                            if(result.equals("goal")){field0_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                            if(result.equals("caught")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                            field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            ball0.setLayoutParams(field0_laypar);
                            out_ball0.setVisibility(View.GONE);
                            out_ball1.setVisibility(View.GONE);
                            ball0.setVisibility(View.VISIBLE);
                            ball1.setVisibility(View.GONE);
                        }
                        else{
                     if(result.equals("out")){
                        out0_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                       if(place.equals("right")){ out0_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                       if(place.equals("center")){ out0_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                       if(place.equals("left")){ out0_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                       out_ball0.setLayoutParams(out0_laypar);
                       out_ball0.setVisibility(View.VISIBLE);
                       out_ball1.setVisibility(View.GONE);
                       ball0.setVisibility(View.GONE);
                       ball1.setVisibility(View.GONE);
                     }
                     if(result.equals("goal")){
                       field0_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                      if(place.equals("right")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                      if(place.equals("center")){field0_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                      if(place.equals("left")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                      field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
  //                    if(place.equals("near grid")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);}
                      ball0.setLayoutParams(field0_laypar);
                      out_ball0.setVisibility(View.GONE);
                      out_ball1.setVisibility(View.GONE);
                      ball0.setVisibility(View.VISIBLE);
                      ball1.setVisibility(View.GONE);
                     }
                     if(result.equals("caught")){
                          field0_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                         if(place.equals("right")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                         if(place.equals("center")){field0_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                         if(place.equals("left")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                         field0_laypar.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                         if(place.equals("near grid")){field0_laypar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);}
                         ball0.setLayoutParams(field0_laypar);
                         out_ball0.setVisibility(View.GONE);
                         out_ball1.setVisibility(View.GONE);
                         ball0.setVisibility(View.VISIBLE);
                         ball1.setVisibility(View.GONE);
                     }
                    }
                    }
//------------------------------direction1-----------------------------------------------------
                else{
                    if(place.equals("near grid")){
                        field1_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                        if(result.equals("out")){field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                        if(result.equals("goal")){field1_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                        if(result.equals("caught")){field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                        field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        ball1.setLayoutParams(field1_laypar);
                        out_ball0.setVisibility(View.GONE);
                        out_ball1.setVisibility(View.GONE);
                        ball0.setVisibility(View.GONE);
                        ball1.setVisibility(View.VISIBLE);
                    }
                    else{
                    if(result.equals("out")){
                        out1_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                        if(place.equals("right")){ out1_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                        if(place.equals("center")){ out1_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                        if(place.equals("left")){ out1_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                        out_ball1.setLayoutParams(out1_laypar);
                        out_ball0.setVisibility(View.GONE);
                        out_ball1.setVisibility(View.VISIBLE);
                        ball0.setVisibility(View.GONE);
                        ball1.setVisibility(View.GONE);
                    }
                    if(result.equals("goal")){
                        field1_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                        if(place.equals("right")){field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                        if(place.equals("center")){field1_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                        if(place.equals("left")){field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                        field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        ball1.setLayoutParams(field1_laypar);
                        out_ball0.setVisibility(View.GONE);
                        out_ball1.setVisibility(View.GONE);
                        ball0.setVisibility(View.GONE);
                        ball1.setVisibility(View.VISIBLE);
                    }
                    if(result.equals("caught")){
                        field1_laypar=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                        if(place.equals("right")){field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_TOP);}
                        if(place.equals("center")){field1_laypar.addRule(RelativeLayout.CENTER_VERTICAL);}
                        if(place.equals("left")){field1_laypar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);}
                        field1_laypar.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        ball1.setLayoutParams(field1_laypar);
                        out_ball0.setVisibility(View.GONE);
                        out_ball1.setVisibility(View.GONE);
                        ball0.setVisibility(View.GONE);
                        ball1.setVisibility(View.VISIBLE);
                    }
                   }
                }



                    if (!result.equals("goal")) {
	                    if (direction == 1) {
	                    	direction = 0;
	                    } else {
	                    	direction = 1;
	                    }
                    }  else{if(direction==0){count1++;}else{count0++;}
                    game_count_text.setText(count0+":"+count1);}
                    //TextView textView = ((TextView)resultView.getChildAt(resultView.getChildCount() - 1));
                    result_text.setText(result_text.getText() + "["+place+"]"+" (" + result + ")");
                    button.setClickable(true);
                } catch (JSONException e) {
                	Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_LONG).show();
                }
            }
        }
    };}
    
    
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Toast.makeText(MainActivity.this, "Error = " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

}
