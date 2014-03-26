package com.toprecur.android.buddychat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BuddyChatReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCustomReceiver";

	Handler handler;

	public BuddyChatReceiver(){
		super();
	}
	
	public BuddyChatReceiver(Handler handler) {
		this();
		this.handler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			JSONObject json = new JSONObject(intent.getExtras().getString(
					"com.parse.Data"));
			String messageText = json.getString("title");
					
			Log.d(TAG, "got action " + action + " on channel " + channel
					+ " with:" + messageText);
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("message", messageText);
			msg.setData(b);
			if(handler.sendMessage(msg)){
				Log.d(TAG, "send message successfully");
			}else{
				Log.d(TAG, "unable to send message.");
			}

		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}
