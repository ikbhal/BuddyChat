package com.toprecur.android.buddychat;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class BuddyChatApplication extends Application {
	static final String APP_ID = "SfuK8lMGH8wTwEWj05kHxu2S1B3xfdUgcPYiHMBY";
	static final String CLIENT_ID = "6xMA6XXzUuYSA8zxiLOEbyJvVc1iPPwrSNrLw9rb";

	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(ChatMessage.class);
		
		Parse.initialize(this, APP_ID, CLIENT_ID);
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
		
		PushService.setDefaultPushCallback(this, MainActivity.class);

		// Save the current Installation to Parse.
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.saveInBackground();
		
		String channelName = null;
		channelName ="channel";
		PushService.subscribe(this, channelName, MainActivity.class);
		
	}

}
