package com.toprecur.android.buddychat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ContactActivity extends Activity implements OnItemClickListener {

	ListView lvConatct;
	private List<Contact> lstContact = new ArrayList<Contact>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		lvConatct = (ListView) findViewById(R.id.list);
		lvConatct.setOnItemClickListener(this);

		// Get the contacts.
		Cursor contactCursor = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

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

			// Create contact object.
			Contact objContact = new Contact();
			objContact.setName(name);
			objContact.setPhoneNo(phoneNumber);

			// Add contact object to contactList.
			lstContact.add(objContact);
		}

		// Close the cursor.s
		contactCursor.close();
		
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
			AlertDialog alert = new AlertDialog.Builder(
					ContactActivity.this).create();
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

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		Contact bean = (Contact) listview.getItemAtPosition(position);
		showCallDialog(bean.getName(), bean.getPhoneNo());
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
