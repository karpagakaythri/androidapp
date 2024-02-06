package com.example.ruralapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private SharedPreferences sp,sn;
	private String IP;
	private EditText userid,pwd;
	private Button submit, reset;
	private TextView v1;
	String web_url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userid = (EditText) findViewById(R.id.editText1);
		pwd = (EditText) findViewById(R.id.editText2);
		submit = (Button) findViewById(R.id.button1);
		reset = (Button) findViewById(R.id.button2);
		v1 = (TextView) findViewById(R.id.textView2);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		IP = sp.getString("ip", "");
		web_url = "http://"+IP+"/RuralServer/loginvalidate.php";
		submit.setOnClickListener(this);
		reset.setOnClickListener(this);
		v1.setOnClickListener(this);
	}
	
	private boolean check() {
		if(userid.getText().toString().trim().equals("")||pwd.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Userid/Password", Toast.LENGTH_LONG).show();
			return false;
		} else
			return true;
	}

	private void cls() {
		userid.setText("");
		pwd.setText("");
	}

	public void checkLogin(String s) {
		String r[] = s.split("~");		
		if(r[0].equalsIgnoreCase("ok")) {
			sn = getSharedPreferences("user", Context.MODE_PRIVATE);
			sn.edit().putString("userid", userid.getText().toString().trim()).commit();
			sn.edit().putString("uname", r[1].trim()).commit();					
			startActivity(new Intent(this, UserHome.class));
			cls();
		} else {
			Toast.makeText(this, r[0], Toast.LENGTH_SHORT).show();
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, CustomPref.class));
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button1:
		if(check()) {
			new LoginValidateTask(this).execute(web_url,userid.getText().toString().trim()+"~"+pwd.getText().toString().trim());
		}	
		break;
		case R.id.button2:
			cls();
			break;
		case R.id.textView2:
			startActivity(new Intent(this, Regn.class));
			break;
		}
	}
}

class LoginValidateTask extends AsyncTask<String, Void, String> {

	MainActivity activity;
	
	public LoginValidateTask(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	protected String doInBackground(String... p) {		
		String s="",s1="";		
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String q[] = p[1].split("~");
            nameValuePairs.add(new BasicNameValuePair("userid", q[0]));
            nameValuePairs.add(new BasicNameValuePair("pwd", q[1]));            
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(p[0]);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            
            s = EntityUtils.toString(entity);
            Log.e("JSON Str", s);
            
		} catch (Exception e) {
			Log.e("Login Task", e.toString());
		}
		return s;
	}
	
	@Override
	protected void onPostExecute(String result) {		
		super.onPostExecute(result);
		activity.checkLogin(result);
	}
}