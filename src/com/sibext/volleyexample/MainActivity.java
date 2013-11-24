package com.sibext.volleyexample;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sibext.volleyexample.GamePost.OnDrawGamePost;

public class MainActivity extends Activity implements OnClickListener, OnDrawGamePost {
    // private int direction = 1;
    private Button butStart;    
    private TextView resGame;
    private Button butStartFull;
    private EditText edNumPause;
    
    /** Результат игры  */
    private Integer resGame0 = 0;
    private Integer resGame1 = 0;
    
    /** Текущее игровое поле */
    private ImageView curField = null;
    
    /** Предыдущий рисунок на текущем поле */
    private Bitmap bacBitmap = null;
    
    // Ировые поля описывающие положение для каждой играющей стороны
    private ArrayList<ImageView> feild_place_0;
    private ArrayList<ImageView> feild_place_1;
    
    /** До скальки идет игра */
    final int MAX_SCORE = 10; 

    
    private void SetFeildPlace(){
    	this.feild_place_0 = new ArrayList<ImageView>(5);  
    	this.feild_place_0.add(GamePost.PLACE_OUT,	(ImageView)findViewById(R.id.out0_center));
    	this.feild_place_0.add(GamePost.PLACE_LEFT,	(ImageView)findViewById(R.id.f0_left));
    	this.feild_place_0.add(GamePost.PLACE_CENTER,(ImageView)findViewById(R.id.f0_centr));
    	this.feild_place_0.add(GamePost.PLACE_RIGHT,	(ImageView)findViewById(R.id.f0_right));
    	this.feild_place_0.add(GamePost.PLACE_NEAR,	(ImageView)findViewById(R.id.greed21));
    	
    	this.feild_place_1 = new ArrayList<ImageView>(5);  
    	this.feild_place_1.add(GamePost.PLACE_OUT,	(ImageView)findViewById(R.id.out1_centr));
    	this.feild_place_1.add(GamePost.PLACE_LEFT,	(ImageView)findViewById(R.id.f1_left));
    	this.feild_place_1.add(GamePost.PLACE_CENTER,(ImageView)findViewById(R.id.f1_centr));
    	this.feild_place_1.add(GamePost.PLACE_RIGHT,	(ImageView)findViewById(R.id.f1_right));
    	this.feild_place_1.add(GamePost.PLACE_NEAR,	(ImageView)findViewById(R.id.greed21));    	
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
        
        butStartFull = (Button)findViewById(R.id.butStartFull);
        butStartFull.setOnClickListener(this);
        
        edNumPause = (EditText)findViewById(R.id.edNumPause);
        
        new Timer();
        
        SetFeildPlace();                                     
    }     

    
    /**
     * Единичный шаг игры
     */
    private void StartOneStep(){
    	butStart.setClickable(false);
    	butStartFull.setClickable(false);
    	
    	GamePost gPost = new GamePost(this);
    	gPost.SetDrawGamePost(this);
    	gPost.execute();
    }
    
    /**
     * Начала полной игры
     */
    private void clickButStartFull()
    {   
    	long pause = 1;
    	
    	resGame0 = 0;
    	resGame1 = 0;
    	
    	CharSequence value = edNumPause.getText();
    	if(!TextUtils.isEmpty(value)){    		
    		pause = Integer.valueOf(value.toString());
    	}
    	pause = pause * 1000;
    			
    	GamePost gPost = new GamePost(this);	
		gPost.SetDrawGamePost(this);
		
		TaskStartGame pt = new TaskStartGame();		
		pt.setPause(pause);
		pt.SetMaxGoal(MAX_SCORE);
		pt.execute(gPost); 		
    	
    }
    
	@Override
	public void onClick(View v) {
		int cutId = v.getId();
		
		if(cutId == R.id.butStart){
			StartOneStep();
		}else
		if(cutId == R.id.butStartFull){
			clickButStartFull();
		}		
	}


	@Override
	public void onSuccessGamePost(int Place, int Result, int direction) {
    	if(curField != null){
    		curField.setImageBitmap(bacBitmap);
    	}
    	
		ArrayList<ImageView> feild_place = null;
    	
    	// --- Выесняем игровые области 

    	if(direction == 0){
    		feild_place = this.feild_place_0;
    		
    		if(Result == GamePost.RESULT_GOAL){
    			this.resGame0++;
    		}   
    	}
    	else 
    	if(direction == 1){
    		feild_place = this.feild_place_1;
    		if(Result == GamePost.RESULT_GOAL){
    			this.resGame1++;    	
    		}
    	}
    		   	
    	curField = feild_place.get(Place); 
    	bacBitmap = ((BitmapDrawable)curField.getDrawable()).getBitmap();
			
        int	iImgResult = R.drawable.ball;
    	
        // --- Берем нужную иконку под результат удара
    	switch (Result) {
			case GamePost.RESULT_OUT:
				iImgResult = R.drawable.is_out;
				break;
			case GamePost.RESULT_GOAL:
				iImgResult = R.drawable.is_goal;
				break;
			default:
				iImgResult = R.drawable.ball;
		}
    	
    	curField.setImageResource(iImgResult);
    	
    	if(Result == GamePost.RESULT_GOAL){
    		String str = "Счет " + String.valueOf(resGame0) + ":" + String.valueOf(resGame1);
    		resGame.setText(str);
    	}	
    	
    	butStart.setClickable(true);
    	butStartFull.setClickable(true);    	
	}
}
