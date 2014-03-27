package com.toprecur.android.buddychat;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class BuddyChatApplication extends Application {
	static final String APP_ID = "SfuK8lMGH8wTwEWj05kHxu2S1B3xfdUgcPYiHMBY";
	static final String CLIENT_ID = "6xMA6XXzUuYSA8zxiLOEbyJvVc1iPPwrSNrLw9rb";

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Register Pares sub classes.
		ParseObject.registerSubclass(ChatMessage.class);
		ParseObject.registerSubclass(Profile.class);
		
		// Initialize parse.
		Parse.initialize(this, APP_ID, CLIENT_ID);

		PushService.setDefaultPushCallback(this, ChatActivity.class);

		// Save the current Installation to Parse.
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.saveInBackground();
		
		// Anonymous user.
		ParseUser.enableAutomaticUser();
//		if (ParseUser.getCurrentUser().getObjectId() == null) {
//			ParseUser.getCurrentUser().increment("RunCount");
//			ParseUser.getCurrentUser().saveInBackground();
//		}
		
		ParseACL defaultACL = new ParseACL();
		ParseACL.setDefaultACL(defaultACL, true);
	
	}

}
