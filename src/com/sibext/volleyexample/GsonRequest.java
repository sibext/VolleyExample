package com.sibext.volleyexample;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;

public class GsonRequest<T> extends Request<T>{

	private Listener<T> callback;
	private Class<?>  object_class;
	private Gson gson = new Gson();
	public GsonRequest(int method, String url, ErrorListener listener, Listener<T> Callback, Class<?> objectClass) {
		super(method, url, listener);
		callback = Callback;
		object_class = objectClass;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response parseNetworkResponse(NetworkResponse response) {

		T object = (T) gson.fromJson(response.toString(), object_class);
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
		
		gson.toJson(src)
		return super.getBody();
	}

}
