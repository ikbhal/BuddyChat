package com.toprecur.android.buddychat;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Contact")
public class Contact extends ParseObject  {
	
	public Contact(){
		
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
	
	public void setFriend(ParseUser user){
		
	}
	
	public ParseUser getFriend(){
		return null;
	}
}
