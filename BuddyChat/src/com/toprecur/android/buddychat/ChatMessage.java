package com.toprecur.android.buddychat;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("ChatMessage")
public class ChatMessage extends ParseObject {
	public ChatMessage() {

	}

	public String getFrom(){
		return getString("from");
	}
	
	public void setFrom(String from){
		put("from", from);
	}
	
	public String getTo(){
		return getString("to");
	}
	
	public void setTo(String to){
		put("to", to);
	}
	
	public String getMessage() {
		return getString("message");
	}

	public void setMessage(String message) {
		put("message", message);
	}
}