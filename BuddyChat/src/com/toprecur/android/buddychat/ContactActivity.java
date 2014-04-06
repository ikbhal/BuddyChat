package com.toprecur.android.buddychat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

public class ContactActivity extends Activity implements OnItemClickListener {

	public static final String TAG = ContactActivity.class.getName();
	ListView lvConatct;
	private List<Contact> lstContact = new ArrayList<Contact>();
	private Map<String, Contact> mapContact = new HashMap<String, Contact>();
	private Map<String, String> mapPhoneNoToContactName = new HashMap<String, String>();
	private List<String> lstPhoneNos;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		lvConatct = (ListView) findViewById(R.id.list);
		lvConatct.setOnItemClickListener(this);

		
		// Get the contacts.
		Cursor contactCursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		Profile profile = null;
		// Iterate through contacts cursor.
		while (contactCursor.moveToNext()) {
			// Get contact name.
			String name = contactCursor
					.getString(contactCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			// Get contact phone number.
			String phoneNumber = contactCursor
					.getString(contactCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			
			Log.d(TAG, "phone number:" + phoneNumber);
			
			phoneNumber = getCorrectPhoneNo(phoneNumber);
			Log.d(TAG, "correct phone number:" + phoneNumber);
			
			
			// Create map key and pair of name and phone numbers.
			mapPhoneNoToContactName.put(phoneNumber, name);
			
			/*
			// Get profile for given phone number if exist.
			profile = getProfileForPhoneNo(phoneNumber);

			// Add profile to to lstContact if the both contact and installed buddy chat.
			if (profile != null) {
				// Create contact object.
				Contact objContact = new Contact();
				objContact.setName(name);
				objContact.setPhoneNo(phoneNumber);

				// Add contact object to contactList.
				lstContact.add(objContact);
			}else{
				Log.d(TAG, "Buddy installed for contact (name: " + name + ", phoneNo: " + phoneNumber + " )");
			}*/

		}

		// Close the cursor.s
		contactCursor.close();
		
		// Get the phone numbers.
		//lstPhoneNos = new ArrayList<String>(mapPhoneNoToContactName.keySet());
		
		lstContact = getProfilesForPhoneNos(mapPhoneNoToContactName);
		// Create Contact Adapter object.
		ContactAdapter contactAdapter = new ContactAdapter(
				ContactActivity.this, R.layout.alluser_row, lstContact);

		// Set the adapter for contact list view.
		lvConatct.setAdapter(contactAdapter);

		if (null != lstContact && lstContact.size() != 0) {
			Collections.sort(lstContact, new Comparator<Contact>() {

				@Override
				public int compare(Contact lhs, Contact rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			AlertDialog alert = new AlertDialog.Builder(ContactActivity.this)
					.create();
			alert.setTitle("");

			alert.setMessage(lstContact.size() + " Contact Found!!!");

			alert.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();

		} else {
			showToast("No Contact Found!!!");
		}
	}
	
	
	/**
	 * Helper function to remove space - ( ) + character, give only 10 digit number.
	 * Small hack as of now. which supports only india.
	 * 
	 * @param phoneNumber
	 * @return
	 */
	private String getCorrectPhoneNo(String phoneNumber){
		
		if(phoneNumber == null)
			return null;
		
		phoneNumber = phoneNumber.replace("(", "");
        phoneNumber = phoneNumber.replace(")", "");
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = phoneNumber.replace(" ", "");
        
        phoneNumber = phoneNumber.replace("+", "");
        
        if(phoneNumber != null && phoneNumber.length()>=10){
        	phoneNumber =phoneNumber .substring(phoneNumber .length()-10);
        }
		return phoneNumber;
	}
	/**
	 * Fetch profiles for list of phone numbers.
	 * 
	 * @param lstPhoneNo the list of phone numbers.
	 * @return the list of profiles.
	 */
	private List<Contact> getProfilesForPhoneNos(Map<String, String> mapPhoneNoToContactName) {
		List<Profile> profiles = null;
		List<Contact> lstContacts = null;
		Contact objContact = null;
		String phoneNo = null;
		String contactName = null;
		
		List<String> lstPhoneNo = new ArrayList(mapPhoneNoToContactName.keySet());
		
		ParseQuery<Profile> query = ParseQuery.getQuery(Profile.class);
		//query.whereEqualTo("phoneNo", phoneNo);
		query.whereContainedIn("phoneNo", lstPhoneNo);
		// Get prfile.
		try {
			profiles = query.find();
			if(profiles != null ) {
				lstContacts = new ArrayList<Contact>();
				for(Profile objProfile : profiles) {
					// Get the profile's phone number.
					phoneNo = objProfile.getPhoneNo();
					
					// Get the contact name for phone number from map.
					contactName = mapPhoneNoToContactName.get(phoneNo);
					
					// Create contact object from contact name and profile object.
					objContact = new Contact(contactName, objProfile);
					
					// Add contact object to list of conatcts.
					lstContacts.add(objContact);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return lstContacts;
	}

	/**
	 * Return Profile for the given mobile no.
	 * 
	 * @param phoneNo
	 *            the phone number
	 * @return the profile object.
	 */
	private Profile getProfileForPhoneNo(final String phoneNo) {
		Profile profile = null;
		ParseQuery<Profile> query = ParseQuery.getQuery(Profile.class);
		query.whereEqualTo("phoneNo", phoneNo);
		// Get prfile.
		try {
			List<Profile> profiles = query.find();
			if (profiles != null && profiles.size() > 0) {
				profile = profiles.get(0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return profile;
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		Contact bean = (Contact) listview.getItemAtPosition(position);
		
		String message = "Name:" + bean.getName() + ", user id:" + bean.getContactId();
		showCallDialog(message, bean.getPhoneNo());
	}

	
	
	
	private void showCallDialog(String name, final String phoneNo) {
		AlertDialog alert = new AlertDialog.Builder(ContactActivity.this)
				.create();
		alert.setTitle("Call?");

		alert.setMessage("Are you sure want to call " + name + " ?");

		alert.setButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setButton2("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String phoneNumber = "tel:" + phoneNo;
				Intent intent = new Intent(Intent.ACTION_CALL, Uri
						.parse(phoneNumber));
				startActivity(intent);
			}
		});
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact, menu);
		return true;
	}

}
