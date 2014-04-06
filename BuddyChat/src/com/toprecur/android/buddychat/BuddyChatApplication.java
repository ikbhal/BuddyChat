package com.toprecur.android.buddychat;

import android.app.Application;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class BuddyChatApplication extends Application {
	
	public static final String TAG = BuddyChatApplication.class.getName();
	
	static final String APP_ID = "LkrUffG6SuSw82ZEVCCMDzzIdyupj8BFbjsz7PAD";
	static final String CLIENT_ID = "VAwp1BXZHcWAAfCo96q0Bqn0ZzrX5LgFgmLkk5xU";

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Register Pares sub classes.
		ParseObject.registerSubclass(ChatMessage.class);
		ParseObject.registerSubclass(Profile.class);
		ParseObject.registerSubclass(Contact.class);
		
		// Initialize parse.
		Parse.initialize(this, APP_ID, CLIENT_ID);

		PushService.setDefaultPushCallback(this, ChatActivity.class);

		// Save the current Installation to Parse.
		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		installation.saveInBackground();
		
		/*
		// Anonymous user.
		ParseUser.enableAutomaticUser();
		ParseUser.getCurrentUser().increment("RunCount");
		ParseUser.getCurrentUser().saveInBackground();
		*/
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	
	}

}
