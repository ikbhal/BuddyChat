package com.toprecur.android.buddychat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Contact")
public class Contact extends ParseObject  {
	
	public Contact(){
		
	}
	
	public Contact(String contactName, Profile objProfile) {
		setName(contactName);
		setPhoneNo(objProfile.getPhoneNo());
		setEmail(objProfile.getEmail());
		setContactId(objProfile.getUser().getObjectId());
	}
	
	public String getName() {
		return getString("name");
	}
	
	public void setName(String name) {
		put("name", name);
	}

	public String getPhoneNo() {
		return getString("phoneNo");
	}
	
	public void setPhoneNo(String phoneNo) {
		put("phoneNo", phoneNo);
	}
	
	public String getEmail() {
		return getString("email");
	}
	
	public void setEmail(String email) {
		put("email", email);
	}
	
	public String getContactId() {
		return getString("contactId");
	}
	
	public void setContactId(String contactId) {
		put("contactId", contactId);
	}
}
