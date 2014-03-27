package com.toprecur.android.buddychat;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Profile")
public class Profile extends ParseObject {
	public Profile() {

	}

	/**
	 * Get the parse user.
	 * @return the parse user.
	 */
	public ParseUser getUser(){
		return getParseUser("user");
	}
	
	/**
	 * Set the parse user.
	 * @param user the parse user.
	 */
	public void setUser(ParseUser user) {
		put("user", user);
	}
	
	/**
	 *  Sets the phone no.
	 * @return the phone no.
	 */
	public String getPhoneNo() {
		return getString("phoneNo");
	}

	/**
	 * Gets the phone no.
	 * @param phoneNo the phone number.
	 */
	public void setPhoneNo(String phoneNo) {
		put("phoneNo", phoneNo);
	}
	
	/**
	 * Sets the name.
	 * @return name the user name.
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 * Get the name.
	 * @param name the user name.
	 */
	public void setName(String name) {
		put("name", name);
	}
	
	/**
	 * Sets the email.
	 * @return email the user email.
	 */
	public String getEmail() {
		return getString("email");
	}

	/**
	 * Get the email.
	 * @param email the user email.
	 */
	public void setEmail(String email) {
		put("email", email);
	}
}
