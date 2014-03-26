package com.toprecur.android.buddychat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class ChatActivity extends Activity implements OnItemClickListener {
	private static final String TAG = ChatActivity.class.getName();
	public static final String BUDDY_CHAT_PUSH_ACTION = "com.toprecur.android.buddychat.UPDATE_STATUS";
	EditText chatInput;
	Button sendButton;
	ListView chatList;
	MessageAdapter mAdapter;
	String currentUserId;
	String otherUserId;
	String otherChannelName;
	String userIds[];
	BuddyChatReceiver receiver;

	// MessageAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ParseAnalytics.trackAppOpened(getIntent());

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
//			Intent intent = new Intent(this, LoginActivity.class);
//			startActivity(intent);
			Log.d(TAG, "User should not be null as it is anonymous user.");
			finish();
		} else {

			setContentView(R.layout.activity_chat);

			// Make channel name out of other user id.
			Intent currentIntent = getIntent();
			String action = currentIntent.getAction();

			otherUserId = currentIntent.getExtras()
					.getString("otherUserId");
			// Open with Parse Push Notification
			if (otherUserId == null) {
				String parseData = currentIntent.getExtras().getString(
						"com.parse.Data");
				try {
					JSONObject jsonObj = new JSONObject(parseData);
					otherUserId = jsonObj.getString("from");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
			setOtherUser(otherUserId);

			// Set the current UserId
			currentUserId = ParseUser.getCurrentUser().getObjectId();

			// set the user ids array
			userIds = new String[] { currentUserId, otherUserId };
		
			chatInput = (EditText) findViewById(R.id.chat_input);
			chatList = (ListView) findViewById(R.id.chat_list);
			mAdapter = new MessageAdapter(this, new ArrayList<ChatMessage>());
			chatList.setAdapter(mAdapter);
			chatList.setOnItemClickListener(this);

		}
		updateData();

	}

	/**
	 * Helper method to set other chat user id.
	 * 
	 * @param otherUserId
	 */
	private void setOtherUser(String otherUserId) {
		this.otherUserId = otherUserId;
		otherChannelName = "channel" + otherUserId;
	}

	public void registerReceiver() {
		// Registering receiver
		receiver = new BuddyChatReceiver(new MyHandler());
		registerReceiver(receiver, new IntentFilter(BUDDY_CHAT_PUSH_ACTION)); // Register

	}

	public void unRegisterReceiver() {
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unRegisterReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unRegisterReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	public void sendMessage(View v) {
		sendMessage();
	}

	void sendMessage() {
		if (chatInput.getText().length() > 0) {
			ChatMessage t = new ChatMessage();
			String message = chatInput.getText().toString();

			// Save chat message
			t.setMessage(message);
			t.setFrom(currentUserId);
			t.setTo(otherUserId);
			t.saveEventually();

			Log.d(TAG, message + "message");

			// Insert into adapter
			mAdapter.insert(t, 0);

			// send push notfication
			ParsePush push = new ParsePush();
			push.setChannel(otherChannelName);
			push.setMessage(message);

			JSONObject data = null;
			try {
				data = new JSONObject("{\"action\": \""
						+ BUDDY_CHAT_PUSH_ACTION + "\"" + ",\"from\": \""
						+ currentUserId + "\"" + ",\"to\": \"" + otherUserId
						+ "\"" + ",\"alert\" : \"" + message + "\""
						+ ",\"title\" : \"" + message + "\"" + "}");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			push.setData(data);
			push.sendInBackground();

			Log.d(TAG, "push sent");

			chatInput.setText("");
		}
	}

	public void updateData() {
		ParseQuery<ChatMessage> query = ParseQuery.getQuery(ChatMessage.class);
		query.whereContainedIn("to", Arrays.asList(userIds));
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ChatMessage>() {
			@Override
			public void done(List<ChatMessage> tasks,
					com.parse.ParseException error) {
				if (tasks != null) {
					mAdapter.clear();
					mAdapter.addAll(tasks);
				}
			}
		});
	}

	public void logout() {
		ParseUser.getCurrentUser().logOut();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout:
			logout();
			break;
		case R.id.action_contacts:
			goToContacts();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void goToContacts() {
		Intent contactIntent = new Intent(this, MainActivity.class);
		startActivity(contactIntent);
		finish();
	}

	public class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String message = msg.getData().getString("message");

			ChatMessage t = new ChatMessage();
			t.setMessage(message);
			mAdapter.insert(t, 0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

}
