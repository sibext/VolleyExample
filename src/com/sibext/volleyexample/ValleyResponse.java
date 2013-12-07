package com.sibext.volleyexample;

import com.google.gson.annotations.SerializedName;

public class ValleyResponse {
	@SerializedName("direction")
	public int direction;
	
	@SerializedName("place")
	public String place;
	
	@SerializedName("result")
	public String result;
}
