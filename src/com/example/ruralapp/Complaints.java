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
import android.content.Context;
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

public class Complaints extends Activity implements OnClickListener {

	private SharedPreferences sp,sn;
	private String IP;
	EditText subj,comp;
	Button b1;
	Spinner sp1;
	ArrayAdapter<String> adapter;
	String web_url="";
	String data[] = {"EB","TWAD","PWD","POLLUTION","CRIME"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complaints);
		subj = (EditText) findViewById(R.id.editText1);		
		comp = (EditText) findViewById(R.id.editText2);					
		b1 = (Button) findViewById(R.id.button1);
		sp1 = (Spinner) findViewById(R.id.spinner1);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp1.setAdapter(adapter);
		sn = getSharedPreferences("user", Context.MODE_PRIVATE);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		IP = sp.getString("ip", "");
		web_url = "http://"+IP+"/RuralServer/complaint.php";
		b1.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(check()) {
			String ssubj = subj.getText().toString().trim();
			String scomp = comp.getText().toString().trim();			
						
			new ComplaintTask(this).execute(web_url,sp1.getSelectedItem().toString().trim(),ssubj,scomp,sn.getString("userid", ""));
		}
	}
	
	private boolean check() {
		if(subj.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Subject", Toast.LENGTH_LONG).show();
			return false;
		} else if(comp.getText().toString().trim().equals("")) {
			Toast.makeText(this, "Enter Complaint", Toast.LENGTH_LONG).show();
			return false;
		} 
			return true;
	}

	private void cls() {
		sp1.setSelection(0);
		subj.setText(""); comp.setText(""); 
	}
	
	public void showMsg(String s) {
		if(s.equalsIgnoreCase("ok")) {
			Toast.makeText(this, "Send Successfully...!", Toast.LENGTH_SHORT).show();
			cls();
		} else {
			Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		}
	}
}

class ComplaintTask extends AsyncTask<String, Void, String> {

	private Complaints activity;
	ProgressDialog pdialog;
	HttpEntity entity=null;
	
	public ComplaintTask(Complaints activity) {
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
			nameValuePairs.add(new BasicNameValuePair("cdept", p[1]));
            nameValuePairs.add(new BasicNameValuePair("subj", p[2]));
            nameValuePairs.add(new BasicNameValuePair("comp", p[3]));            
            nameValuePairs.add(new BasicNameValuePair("userid", p[4]));

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
			Log.e("BirthCertTask", e.getLocalizedMessage());
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
