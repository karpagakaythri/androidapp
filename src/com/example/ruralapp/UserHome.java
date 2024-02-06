package com.example.ruralapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UserHome extends Activity implements OnClickListener {

	Button b1,b2;
	TextView v1;
	SharedPreferences sn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userhome);
		b1 = (Button) findViewById(R.id.button1);
		b2 = (Button) findViewById(R.id.button2);
		v1 = (TextView) findViewById(R.id.textView1);
		sn = getSharedPreferences("user", Context.MODE_PRIVATE);
		v1.setText("Welcome, "+sn.getString("uname", ""));
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {	
		super.onDestroy();
		sn.edit().remove("userid").commit();
		sn.edit().remove("uname").commit();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button1:
				startActivity(new Intent(this, Complaints.class));
				break;
			case R.id.button2:
				startActivity(new Intent(this, ViewComp.class));
				break;
		}
	}
}
