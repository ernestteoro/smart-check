package com.example.smartcheck;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import db_package.DB_Manager;

public class SmsMode extends Activity{
	private Button saveSecret,deleteSecret;
	private EditText secretField;
	private DB_Manager dbManager;
	private ListView addCodeList;
	private ArrayList<String> listCods;
	@SuppressWarnings("unused")
	private String[] listSecretCodes;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_mode_layout);
		initialiseComponents();
		dbManager=new DB_Manager(this,null,null,0);
		Log.v("db object", " Database Object == "+dbManager);
		addCodeList.setAdapter(getArrayAdapter(dbManager.getListOfCode()));
		addClickListenerToButtons();
		
		// List of secret codes for changing the Ring mode on sms received
		listCods=new ArrayList<String>();
		listSecretCodes=dbManager.getListOfCode();
		addCodeList.setBackgroundColor(Color.WHITE);
		addCodeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		addCodeList.setItemsCanFocus(false);
		addCodeList.setClickable(false);
		addCodeList.setFocusable(false);
		addCodeList.setFocusableInTouchMode(false);
		addCodeList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(listCods.isEmpty()){
						listCods.add(addCodeList.getItemAtPosition(position).toString());
				}
				else if(!listCods.contains(addCodeList.getItemAtPosition(position).toString())){
					listCods.add(addCodeList.getItemAtPosition(position).toString());
				}else{
					listCods.remove(addCodeList.getItemAtPosition(position).toString());
				}
			}
		});
	}
	
	public void initialiseComponents(){
		saveSecret=(Button)findViewById(R.id.save_secret);
		deleteSecret=(Button)findViewById(R.id.delete_secret);
		secretField=(EditText)findViewById(R.id.secret_code);
		addCodeList=(ListView)findViewById(R.id.codeList);
		
	}
	
    public void addClickListenerToButtons(){
    	// Button to add secret codes from the db
		saveSecret.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!secretField.getText().equals("")){
					dbManager.addSecretKey(secretField.getText().toString().trim());
					secretField.setText("");
					addCodeList.setAdapter(getArrayAdapter(dbManager.getListOfCode()));
				}
			}
		});
		// Button to delete secret codes from the db
		deleteSecret.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(!listCods.isEmpty()){
					for(int iterator=0;iterator<listCods.size();iterator++){
						dbManager.deleteSmsCodes(listCods.get(iterator));
						Log.v("Code == ",""+listCods.get(iterator));
					}
					addCodeList.setAdapter(getArrayAdapter(dbManager.getListOfCode()));
					listCods.clear();
				}
			}
		});
	}
	
	public ArrayAdapter<String> getArrayAdapter(String[] conList){
		ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(SmsMode.this,android.R.layout.simple_list_item_multiple_choice,conList){
			public View getView(int position,View convertView,ViewGroup parent){
				TextView tv=(TextView)super.getView(position, convertView, parent);
				tv.setTextColor(Color.BLACK);
				return tv;
			}
		};
		return myAdapter;
	}
}
