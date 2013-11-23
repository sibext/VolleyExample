package com.sibext.volleyexample;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends Activity implements OnClickListener {
    private int direction = 1;
    private Button butStart;    
    private TextView resGame;
    
    private Integer resGame0 = 0;
    private Integer resGame1 = 0;
    
    /** Текущее игровое поле */
    private ImageView curField = null;
    
    /** Предыдущий рисунок на текущем поле */
    private Bitmap bacBitmap = null;
    
    // Ировые поля описывающие положение для каждой играющей стороны
    private ArrayList<ImageView> feild_place_0;
    private ArrayList<ImageView> feild_place_1;
    
    // Положения
    final int PLACE_OUT		= 0;
    final int PLACE_LEFT	= 1;
    final int PLACE_CENTER	= 2;
    final int PLACE_RIGHT	= 3;
    final int PLACE_NEAR	= 4;
    
    // Результат удара
    final int RESULT_CAUGHT = 0;
    final int RESULT_GOAL	= 1;
    final int RESULT_OUT	= 2;
    
    private void SetFeildPlace(){
    	this.feild_place_0 = new ArrayList<ImageView>(5);  
    	this.feild_place_0.add(PLACE_OUT,	(ImageView)findViewById(R.id.out0_center));
    	this.feild_place_0.add(PLACE_LEFT,	(ImageView)findViewById(R.id.f0_left));
    	this.feild_place_0.add(PLACE_CENTER,(ImageView)findViewById(R.id.f0_centr));
    	this.feild_place_0.add(PLACE_RIGHT,	(ImageView)findViewById(R.id.f0_right));
    	this.feild_place_0.add(PLACE_NEAR,	(ImageView)findViewById(R.id.greed21));
    	
    	this.feild_place_1 = new ArrayList<ImageView>(5);  
    	this.feild_place_1.add(PLACE_OUT,	(ImageView)findViewById(R.id.out1_centr));
    	this.feild_place_1.add(PLACE_LEFT,	(ImageView)findViewById(R.id.f1_left));
    	this.feild_place_1.add(PLACE_CENTER,(ImageView)findViewById(R.id.f1_centr));
    	this.feild_place_1.add(PLACE_RIGHT,	(ImageView)findViewById(R.id.f1_right));
    	this.feild_place_1.add(PLACE_NEAR,	(ImageView)findViewById(R.id.greed21));    	
    }
    
    private int GetIntPlace(String strPlace)
    {
    	int result = 0;
    	
    	if(strPlace.equals("out"))
    		result = PLACE_OUT;
    	else
    	if(strPlace.equals("left"))
    		result = PLACE_LEFT;
    	else
    	if(strPlace.equals("center"))
    		result = PLACE_CENTER;
    	else
    	if(strPlace.equals("right"))
    		result = PLACE_RIGHT;
    	else
    	if(strPlace.equals("near grid"))
    		result = PLACE_NEAR;
    	    	
		return result;    	
    }
    
    private int GetIntResult(String strResult)
    {
    	int result = 0;
    	
    	if(strResult.equals("caught"))
    		result = RESULT_CAUGHT;
    	else
    	if(strResult.equals("goal"))
    		result = RESULT_GOAL;
    	else
    	if(strResult.equals("out"))
    		result = RESULT_OUT;   
    	
    	return result;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VolleyHelper.init(this);
        
        resGame = (TextView)findViewById(R.id.resGame);        
        resGame.setText("Счет 0:0");
        
        butStart = (Button)findViewById(R.id.butStart); 
        butStart.setOnClickListener(this);
        
        SetFeildPlace();
                     
    }
    
    private void drawIco(String result, String place) 
    {   
    	if(curField != null){
    		curField.setImageBitmap(bacBitmap);
    	}
    	
    	int iPlace = GetIntPlace(place);    
    	int iResult = GetIntResult(result);
    	
    	if(iResult == RESULT_OUT)
    		iPlace = PLACE_OUT;
    	
    	ArrayList<ImageView> feild_place = null;
    	
    	if(direction == 0){
    		feild_place = this.feild_place_0;
    		
    		if(iResult == RESULT_GOAL){
    			this.resGame0++;
    		}   
    	}
    	else 
    	if(direction == 1){
    		feild_place = this.feild_place_1;
    		if(iResult == RESULT_GOAL){
    			this.resGame1++;    	
    		}
    	}
    		
    	ImageView iv = feild_place.get(iPlace);    	
    	curField = iv;
    	bacBitmap = ((BitmapDrawable)curField.getDrawable()).getBitmap();
			
        int	iImgResult = R.drawable.ball;
    	
    	switch (iResult) {
			case RESULT_OUT:
				iImgResult = R.drawable.is_out;
				break;
			case RESULT_GOAL:
				iImgResult = R.drawable.is_goal;
				break;
			default:
				iImgResult = R.drawable.ball;
		}
    	
    	iv.setImageResource(iImgResult);
    	
    	if(iResult == RESULT_GOAL){
    		String str = "Счет " + String.valueOf(resGame0) + ":" + String.valueOf(resGame1);
    		resGame.setText(str);
    	}
    		
    }
    
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                	String result = response.getString("result");
                	String place = response.getString("place");
                	
                    if (!result.equals("goal")) {
	                    if (direction == 1) {
	                    	direction = 0;
	                    } else {
	                    	direction = 1;
	                    }
                    }
                   
                    String msg = String.valueOf(direction) + " (" + result + ") " + place;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();                                       
                    
                    drawIco(result, place);
                    
                    butStart.setClickable(true);
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
    
    private void clickButStart(){
    	butStart.setClickable(false);
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
    
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.butStart){
			clickButStart();
		}
		
	}

}
