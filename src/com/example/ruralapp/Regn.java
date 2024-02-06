package com.example.ruralapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class Regn extends Activity implements OnClickListener {

	private SharedPreferences sp,sn;
	private String IP;
	EditText name,addr,city,occ,phone,userid,pwd;
	Button b1;
	String web_url="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regn);
		name = (EditText) findViewById(R.id.editText1);
		addr = (EditText) findViewById(R.id.editText2);		
		city = (EditText) findViewById(R.id.editText3);
		occ = (EditText) findViewById(R.id.editText4);		
		phone = (EditText) findViewById(R.id.editText5);
		userid = (EditText) findViewById(R.id.editText6);
		pwd = (EditText) findViewById(R.id.editText7);
		b1 = (Button) findViewById(R.id.button1);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		IP = sp.getString("ip", "");
		web_url = "http://"+IP+"/RuralServer/uregn.php";
		b1.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(check()) {
			String sname = name.getText().toString().trim();
			String saddr = addr.getText().toString().trim();						
			String scity = city.getText().toString().trim();
			String socc = occ.getText().toString().trim();
			String sphone = phone.getText().toString().trim();
			String suserid = userid.getText().toString().trim();
			String spwd = pwd.getText().toString().trim();			
			new RegnTask(this).execute(web_url,sname,saddr,scity,socc,sphone,suserid,spwd);
		}
	}
	
	private boolean check() {
		if(name.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Name", Toast.LENGTH_LONG).show();
			return false;
		} else if(addr.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Address", Toast.LENGTH_LONG).show();
			return false;
		} else if(city.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter City", Toast.LENGTH_LONG).show();
			return false;
		} else if(occ.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Occupation", Toast.LENGTH_LONG).show();
			return false;
		} else if(phone.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Phone", Toast.LENGTH_LONG).show();
			return false;
		} else if(userid.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter User Id", Toast.LENGTH_LONG).show();
			return false;
		} else if(pwd.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
			return false;
		} 
			return true;
	}

	private void cls() {
		name.setText(""); addr.setText(""); city.setText(""); occ.setText("");
		phone.setText(""); userid.setText(""); pwd.setText("");
	}
	
	public void showMsg(String s) {
		if(s.equalsIgnoreCase("ok")) {
			Toast.makeText(this, "Registered Successfully...!", Toast.LENGTH_SHORT).show();
			cls();
			finish();
		} else {
			Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		}
	}
}

class RegnTask extends AsyncTask<String, Void, String> {

	private Regn activity;
	ProgressDialog pdialog;
	HttpEntity entity=null;
	
	public RegnTask(Regn activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pdialog = ProgressDialog.show(activity,"Please Wait","Uploading is in Process...!",false,false);
	}

	@Override
	protected String doInBackground(String... p) {		
		String s="";		
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", p[1]));
            nameValuePairs.add(new BasicNameValuePair("addr", p[2]));
            nameValuePairs.add(new BasicNameValuePair("city", p[3]));
            nameValuePairs.add(new BasicNameValuePair("occ", p[4]));                        
            nameValuePairs.add(new BasicNameValuePair("mobile", p[5]));
            nameValuePairs.add(new BasicNameValuePair("userid", p[6]));
            nameValuePairs.add(new BasicNameValuePair("pwd", p[7]));            

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(p[0]);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);

                entity = response.getEntity();               
      
                s = EntityUtils.toString(entity,"UTF-8");                
            } catch (ClientProtocolException e) {		                	
            	Log.e("ClientProtocol","Log_tag");
				e.printStackTrace();
            } catch (IOException e1) {		                	
            	Log.e("Log_tag", "IOException");
				 e1.printStackTrace();
            }

		} catch (Exception e) {
			Log.e("RegnTask", e.getLocalizedMessage());
		} finally {
			try {				
			} catch (Exception e) {
			}
		}
		return s.trim();
	}
	
	@Override
	protected void onPostExecute(String result) {		
		super.onPostExecute(result);
		pdialog.dismiss();
		activity.showMsg(result);
	}
}
