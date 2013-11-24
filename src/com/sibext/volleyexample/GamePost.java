package com.sibext.volleyexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;


public class GamePost {	
    public interface OnDrawGamePost {    
        void onSuccessGamePost(int Place, int Result, int direction);           
    }  	
    
    public interface OnGoalGamePost{
    	void onGoalGame(int direction);
    }
	
    private OnDrawGamePost iDrawGamePost = null; 
    private OnGoalGamePost iGoalGamePost = null;
    
	private Context curContext;
	private int direction = 1;
	    
    /** До скальки идет игра */
    static final int MAX_SCORE = 10; 
    
    /** Положения */
    static final int PLACE_OUT		= 0;
    static final int PLACE_LEFT	= 1;
    static final int PLACE_CENTER	= 2;
    static final int PLACE_RIGHT	= 3;
    static final int PLACE_NEAR	= 4;
    
    /** Результат удара */
    static final int RESULT_CAUGHT = 0;
    static final int RESULT_GOAL	= 1;
    static final int RESULT_OUT	= 2;
        
    public void SetDrawGamePost(OnDrawGamePost l){
    	this.iDrawGamePost = l;
    }
    
    public void SetGoalGamePost(OnGoalGamePost l){
    	this.iGoalGamePost = l;
    }
    
    public GamePost(Context con)
    {
    	this.curContext = con;
    }
    
    /**
     * Разбор положени
     * @param strPlace
     * @return
     */
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
    
    /**
     * Разбор результата удара
     * @param strResult
     * @return
     */
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
                    Toast.makeText(curContext, msg, Toast.LENGTH_LONG).show();                                       
                    
                    NotifyResult(place, result, direction);                    
                    
                } catch (JSONException e) {
                	Toast.makeText(curContext, "Parse error", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    
    /**
     * Оповестить о результатте
     * @param place
     * @param result
     * @param direction
     */
    private void NotifyResult(String place, String result, int direction){
    	int iPlace = GetIntPlace(place);    
    	int iResult = GetIntResult(result);

    	if(iResult == RESULT_OUT)
    		iPlace = PLACE_OUT;  
    	
    	
    	if(iGoalGamePost != null || iResult == RESULT_GOAL){
    		iGoalGamePost.onGoalGame(direction);
    	}
    	
    	if(iDrawGamePost != null){
    		iDrawGamePost.onSuccessGamePost(iPlace, iResult, direction);
    	}    	    	  	
    	
    }
    
    
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Toast.makeText(curContext, "Error = " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }
    
    public void execute(){
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
	
}
