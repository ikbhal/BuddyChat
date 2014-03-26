package com.toprecur.android.buddychat;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.parse.ParseAnalytics;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MainActivity extends ListActivity {

	private ParseQueryAdapter<ParseUser> mainAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ParseAnalytics.trackAppOpened(getIntent());

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}else {
			/*
			//Subscribe to the channel.
			String channelName = "channel" + currentUser.getObjectId();
			PushService.subscribe(this, channelName, ChatActivity.class);
			*/
			
			mainAdapter = new ParseQueryAdapter<ParseUser>(this, ParseUser.class);
			mainAdapter.setTextKey("username");
			mainAdapter.setImageKey("photo");
			
			setListAdapter(mainAdapter);
		}
		
	}

	public void logout(){
		ParseUser.getCurrentUser().logOut();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseUser otherUser = mainAdapter.getItem(position);
		String otherUserId = otherUser.getObjectId();
		
		Intent chatIntent = new Intent(this, ChatActivity.class);
		chatIntent.putExtra("otherUserId", otherUserId);
		startActivity(chatIntent);
		finish();
	}
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout: {
			logout();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}


}
