package com.sibext.volleyexample;

import com.sibext.volleyexample.GamePost.OnDrawGamePost;
import com.sibext.volleyexample.GamePost.OnGoalGamePost;

import android.os.AsyncTask;
import android.os.SystemClock;

public class TaskStartGame extends AsyncTask<GamePost, Void, GamePost> implements OnGoalGamePost {
    public interface OnStartFinish {    
        void onStartGame();    
        void onFinishGame();
    }
    
    private OnStartFinish iStartFinish = null;
    
    
	/** Пауза между ударами */
	private long pause = 0;	
	private Integer max_goal;
	
	private Integer res0 = 0;
	private Integer res1 = 0;
	
	public void setOnStartFinish(OnStartFinish l)
	{
		this.iStartFinish = l;
	}
	
	public void setPause(long valuePause){
		this.pause = valuePause;
	}
	
	public void SetMaxGoal(Integer _max_goal){
		this.max_goal = _max_goal;
	}

	public TaskStartGame()
	{
		super();	
	}
	
	@Override
	protected GamePost doInBackground(GamePost... params) {
		if(this.iStartFinish != null)
			this.iStartFinish.onStartGame();
		
		GamePost gPost = params[0];
		gPost.SetGoalGamePost(this);
		
		while (res0 <= max_goal && res1 <= max_goal){
			gPost.execute();	
			SystemClock.sleep(this.pause);
		}
	
		if(this.iStartFinish != null)
			this.iStartFinish.onFinishGame();
		
		return gPost;
	}
	
	protected void onPostExecute(GamePost result)
	{
		super.onPostExecute(result);			
	}

	@Override
	public void onGoalGame(int direction) {
		switch (direction) {
		case 0:
			res0++;
			break;
		case 1:
			res1++;
			break;
		}
		
	}
}
