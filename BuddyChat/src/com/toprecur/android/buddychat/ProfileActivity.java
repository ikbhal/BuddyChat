package com.toprecur.android.buddychat;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;

public class ProfileActivity extends Activity {

	public static final String TAG = ProfileActivity.class.getName();
	EditText editName;
	EditText editPhoneNo;
	EditText editEmail;
	Button btnSave;
	Profile currentProfile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		// Retrieve edit text fields for name, phone no and email.
		editName = (EditText) findViewById(R.id.edit_name);
		editPhoneNo = (EditText) findViewById(R.id.edit_phone_no);
		editEmail = (EditText) findViewById(R.id.edit_email);

		// Retrieve Button.
		btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handleProfileSave();
			}
		});
		
		// load profile.
		loadProfile();
	}

	/**
	 * Helper function to save Profile.
	 */
	private void handleProfileSave() {
		// if there is no profile => save it
		// if exist , retrieve it, and save it.
		if(currentProfile != null) {
			updateProfile(currentProfile);
		} else {
			createProfile();
		}
	
	}
	
	/**
	 * Helper function to load profile
	 */
	private void loadProfile() {
		
		ParseQuery<Profile> query = ParseQuery.getQuery(Profile.class);
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
		// Get prfile.
		query.findInBackground(new FindCallback<Profile>() {
			@Override
			public void done(final List<Profile> profiles,
					com.parse.ParseException error) {

				Toast.makeText(ProfileActivity.this, "Loaded profile", Toast.LENGTH_SHORT);
				Log.d(TAG, "inside retrieve profile" + profiles);
				if (profiles != null && profiles.size() > 0) {
					currentProfile = profiles.get(0);
					updateProfileForm();
				}
			}
		});
	}
	
	/**
	 * Helper function to update profile form from current profile.
	 */
	private void updateProfileForm() {
		if(currentProfile == null){
			Log.e(TAG, "No current profile");
			return;
		}
		
		// Update profile form fields from current profile.
		editName.setText(currentProfile.getName());
		editPhoneNo.setText(currentProfile.getPhoneNo());
		editEmail.setText(currentProfile.getEmail());
		
		Log.d(TAG, "Updated profile form.");
	}

	/**
	 * Update the existing profile from the profile form fields.
	 * 
	 * @param profile
	 */
	private void updateProfile(Profile profile) {

		// Set profile fields
		profile.setName(editName.getText().toString());
		profile.setPhoneNo(editPhoneNo.getText().toString());
		profile.setEmail(editEmail.getText().toString());

		// Save the profile in background.
		Log.d(TAG, "Updating profile.");
		profile.saveInBackground();
	}

	/**
	 * Helper methods for creating profile from the profile form.
	 */
	private void createProfile() {
		Log.d(TAG, "Creating profile.");
		// Create profile and save it.
		currentProfile = new Profile();

		// Set the porse user in profile.
		currentProfile.setUser(ParseUser.getCurrentUser());

		updateProfile(currentProfile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

}
