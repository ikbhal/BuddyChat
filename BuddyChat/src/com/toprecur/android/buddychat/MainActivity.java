package com.toprecur.android.buddychat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();

	ListView lvContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ParseAnalytics.trackAppOpened(getIntent());

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			Log.d(TAG, "User should not be null as it is anonymous user.");
			finish();
		} else {

			// Subscribe to the channel.
			String channelName = "channel"
					+ ParseUser.getCurrentUser().getObjectId();
			PushService.subscribe(this, channelName, ChatActivity.class);

			// Set the layout.
			setContentView(R.layout.activity_main);
			
			// Get the list view.
			lvContact = (ListView) findViewById(R.id.list);
			lvContact.setOnItemClickListener(this);
		
			// Get the Buddy Chat profiles which are matching with phone contacts.
			BuddyChatProfileRetriever  profileRetriever = new BuddyChatProfileRetriever();
			profileRetriever.execute();
			
		}
	}

	class BuddyChatProfileRetriever extends AsyncTask<String, Void, Void> {
		private List<Contact> lstContact = null;

		protected Void doInBackground(String... args) {
			Cursor contactCursor = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);
			// if no contact there is no use to continue further.
			if (contactCursor == null) {
				return null;
			}

			String contactName = null;
			String phoneNum = null;
			Integer nameColIndex = contactCursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			Integer phoneNumColIndex = contactCursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

			Map<String, String> mapPhoneNumToName = new HashMap<String, String>();

			while (contactCursor.moveToNext()) {
				// Get the contact name and phone number.
				contactName = contactCursor.getString(nameColIndex);
				phoneNum = contactCursor.getString(phoneNumColIndex);

				// Correct the phone number.
				phoneNum = getCorrectedPhoneNumber(phoneNum);

				// Add to phone number to name map.
				mapPhoneNumToName.put(phoneNum, contactName);
			}

			// Close the contact cursor.
			contactCursor.close();

			// Get the list of contacts from phone number to contact name map.
			lstContact = getProfilesForPhoneNos(mapPhoneNumToName);

			return null;
		}

		/**
		 * Fetch profiles for list of phone numbers.
		 * 
		 * @param lstPhoneNo
		 *            the list of phone numbers.
		 * @return the list of profiles.
		 */
		private List<Contact> getProfilesForPhoneNos(
				Map<String, String> mapPhoneNumToName) {
			List<Profile> profiles = null;
			List<Contact> lstContacts = null;
			Contact objContact = null;
			String phoneNo = null;
			String contactName = null;

			List<String> lstPhoneNum = new ArrayList<String>(mapPhoneNumToName.keySet());

			// Create parse query to fetch profiles which match with one of the
			// contact phone numbers.
			ParseQuery<Profile> query = ParseQuery.getQuery(Profile.class);
			query.whereContainedIn("phoneNo", lstPhoneNum);

			// Get prfile.
			try {
				profiles = query.find();
				if (profiles != null) {
					lstContacts = new ArrayList<Contact>();
					for (Profile objProfile : profiles) {
						// Get the profile's phone number.
						phoneNo = objProfile.getPhoneNo();

						// Get the contact name for phone number from map.
						contactName = mapPhoneNumToName.get(phoneNo);

						// Create contact object from contact name and profile
						// object.
						objContact = new Contact(contactName, objProfile);

						// Add contact object to list of contacts.
						lstContacts.add(objContact);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return lstContacts;
		}

		protected void onProgressUpdate(Void... args) {

		}

		protected void onPostExecute(Void result) {
			if (lstContact != null) {
				// Create Contact Adapter object.
				ContactAdapter contactAdapter = new ContactAdapter(
						MainActivity.this, R.layout.alluser_row, lstContact);

				// Set the adapter for contact list view.
				lvContact.setAdapter(contactAdapter);
			}
		}
	}

	/**
	 * Correct the given phone number by removing spaces, ( and + symobol.
	 * 
	 * @param phoneNum
	 * @return
	 */
	public static String getCorrectedPhoneNumber(String phoneNum) {

		if (phoneNum == null || phoneNum.equals(""))
			return null;

		phoneNum = phoneNum.replace("(", "");
		phoneNum = phoneNum.replace(")", "");
		phoneNum = phoneNum.replace("-", "");
		phoneNum = phoneNum.replace(" ", "");
		phoneNum = phoneNum.replace("+", "");

		if (phoneNum != null && phoneNum.length() >= 10) {
			phoneNum = phoneNum.substring(phoneNum.length() - 10);
		}

		return phoneNum;
	}

	public void logout() {
		ParseUser.logOut();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
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
		case R.id.action_contacts: {
			goToContacts();
			break;
		}
		case R.id.action_profile: {
			goToProfile();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Helper function to open contact activity.
	 */
	private void goToContacts() {
		Intent contactIntent = new Intent(this, ContactActivity.class);
		startActivity(contactIntent);
	}

	/**
	 * Helper function to open profile activity.
	 */
	private void goToProfile() {
		Intent contactIntent = new Intent(this, ProfileActivity.class);
		startActivity(contactIntent);
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {

		Contact contact = (Contact) listview.getItemAtPosition(position);
		if (contact != null) {
			String otherUserId = contact.getContactId();

			Intent chatIntent = new Intent(this, ChatActivity.class);
			chatIntent.putExtra("otherUserId", otherUserId);
			startActivity(chatIntent);
			finish();
		}

	}

}
