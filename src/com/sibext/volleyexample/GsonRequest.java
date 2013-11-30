package com.sibext.volleyexample;

import java.io.UnsupportedEncodingException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class GsonRequest<T> extends Request<T>{

	private Listener<T> callback;
	private Class<T>  object_class;
	private Gson gson = new Gson();
	private ValleyRequest req_object;
	
	public GsonRequest(int method, String url, ValleyRequest req,  ErrorListener listener, Listener<T> Callback, Class<T> objectClass) {
		super(method, url, listener);
		callback = Callback;
		object_class = objectClass;
		req_object = req;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response parseNetworkResponse(NetworkResponse response) {
		String jsonString = null;
		try {
			jsonString =
			        new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		T object = (T) gson.fromJson(jsonString, object_class);
		return Response.success(object, this.getCacheEntry());
	}

	@Override
	protected void deliverResponse(Object response) {
		// TODO Auto-generated method stub
		callback.onResponse((T) response);
	}
	
	@Override
	public byte[] getBody() throws AuthFailureError {
		// TODO Auto-generated method stub
		
		//it's ugly but i'm lazy )))
		return gson.toJson(req_object, ValleyRequest.class).getBytes();
		
	}

}
