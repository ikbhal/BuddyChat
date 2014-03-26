package com.toprecur.android.buddychat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<ChatMessage> {
	private Context mContext;
	private List<ChatMessage> mTasks;
	public MessageAdapter(Context context, List<ChatMessage> objects) {
		super(context, R.layout.display_list, objects);
		this.mContext = context;
		this.mTasks = objects;
	
		// TODO Auto-generated constructor stub
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
			convertView = mLayoutInflater.inflate(R.layout.display_list, null);
		}

		ChatMessage task = mTasks.get(position);

		TextView descriptionView = (TextView) convertView
				.findViewById(R.id.task_description);

		descriptionView.setText(task.getMessage());
	
		return convertView;
	}

}