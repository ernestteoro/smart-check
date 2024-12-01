package com.example.smartcheck;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import db_package.DB_Manager;

public class OnCallActivity extends Activity 
{
	static final int PICK_CONTACT=0;
	private AudioManager audioManager;
	String change_mode_num1="",ringMod="",contactList[],modeList[],listContacts[],contactDelete[];
	int check=0,delIndex=-1,currentPos=-1;
	private EditText modeChangeContact;
	private ImageButton addContacts;
	private Spinner chose_mode;
	private Button applyChanges,deleteContact;
	private ListView addedContacts;
	DB_Manager dbManager;
	private ArrayList<String> listOfcontacts;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_layout);
		initializeComponents();
		dbManager=new DB_Manager(this, null, null, 0);
		listContacts=dbManager.getListOfContacts();
		contactDelete=new String[listContacts.length];
		addedContacts.setAdapter(getArrayAdapter(listContacts));
		setListenersToImageButtons();
		
		applyChanges=(Button)findViewById(R.id.apply_to);
		applyChanges.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String phoneNumber="";
				//EditText mode_num1=(EditText)findViewById(R.id.mode_change_num_txt);
				if(modeChangeContact.getText().toString().isEmpty())
				{
					Toast.makeText(getApplicationContext(),"Please Enter at least one number",Toast.LENGTH_SHORT).show();
					return;
				}
				phoneNumber=modeChangeContact.getText().toString().trim();
				if(chose_mode.getSelectedItem().toString() !="Mode"){
					ringMod=chose_mode.getSelectedItem().toString();
				}
				else{
					ringMod="silent";
				}
				
					dbManager.addNewNumber(phoneNumber.trim(),ringMod);
					modeChangeContact.setText("");
					//listContacts=dbManager.getListOfContacts();
					/*String listV="";
					for(int i=0;i<listContacts.length;i++)
						listV=listV+" "+listContacts[i];*/
					//Toast.makeText(getApplicationContext(),listV, Toast.LENGTH_LONG).show();
					if(dbManager.getListOfContacts().length>0){
						addedContacts.setAdapter(getArrayAdapter(dbManager.getListOfContacts()));
					}
					Toast.makeText(getApplicationContext()," Contact added",Toast.LENGTH_SHORT).show();				
			}
			
		});

		TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener callTelephonyListener=new PhoneStateListener(){
			public void onCallStateChanged(int state,String incommingNumber){
				String currentState="normal";
				if(state==TelephonyManager.CALL_STATE_RINGING)
				{
					if(dbManager.getPhoneNumber(incommingNumber)){
						audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
						if( audioManager.getRingerMode()== AudioManager.RINGER_MODE_NORMAL){
							if(dbManager.getRingMode(incommingNumber).equalsIgnoreCase("silent")){
								audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
								Toast.makeText(getApplicationContext()," Ring mode changed to silent", Toast.LENGTH_LONG).show();
							}
							else if(dbManager.getRingMode(incommingNumber).equalsIgnoreCase("vibrate")){
								audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
								Toast.makeText(getApplicationContext()," Ring mode changed vibrate", Toast.LENGTH_LONG).show();
							}
							else{
								audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
								Toast.makeText(getApplicationContext()," Ring mode changed to silent", Toast.LENGTH_LONG).show();
							}
						}
						if( audioManager.getRingerMode()== AudioManager.RINGER_MODE_SILENT&&dbManager.getRingMode(incommingNumber).equalsIgnoreCase("normal")){
							audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
							currentState="silent";
						}
						else if( audioManager.getRingerMode()== AudioManager.RINGER_MODE_VIBRATE&&dbManager.getRingMode(incommingNumber).equalsIgnoreCase("normal")){
							audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
							currentState="vibrate";
						}
					}
				}
				else if(state==TelephonyManager.CALL_STATE_IDLE){
					audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
					if(currentState.equals("silent")){
						audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						currentState="normal";
					}
					else if(currentState.equals("vibrate")){
						audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
						currentState="normal";
					}
					else{
						audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						currentState="normal";
					}
					//if( audioManager.getRingerMode()== AudioManager.RINGER_MODE_VIBRATE || audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT)
				}
			}
		};
		telephonyManager.listen(callTelephonyListener, PhoneStateListener.LISTEN_CALL_STATE);	
	}
	
	public static void applyListener(){}
	
	public void onActivityResult(int reqCode,int resCode,Intent data){
		super.onActivityResult(reqCode, resCode, data);
		if(resCode==RESULT_OK)
		{
			String phoneNo="";
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
		    int phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		    phoneNo = cursor.getString(phoneIndex);
		    phoneNo=phoneNo.replace(")", "");
		    phoneNo=phoneNo.replace("(", "");
		    phoneNo=phoneNo.replace("-", "");
		    phoneNo=phoneNo.replace(" ","");
			//Toast.makeText(getApplicationContext(),phoneNo, Toast.LENGTH_LONG).show();
		    modeChangeContact.setText(phoneNo);
		}
		else
		{
			Toast.makeText(getApplicationContext(), " No contacts were selected ", Toast.LENGTH_LONG).show();
		}
	} 
	
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_menu) {
            new DialogBox(OnCallActivity.this,"Alert"," About Menu Select");
        }
        if (id == R.id.feed_back_menu) {
        	new DialogBox(OnCallActivity.this,"Alert","FeedBack Menu Select");
        }
        if (id == R.id.sugestion_menu) {
        	new DialogBox(OnCallActivity.this,"Alert","Suggestion Menu Select");
        }
        return super.onOptionsItemSelected(item);
    }
	
  // Initialization method to initialize components( Objects)
	public void initializeComponents(){ 
		listOfcontacts=new ArrayList<String>();
		modeChangeContact = (EditText)findViewById(R.id.mode_change_num_txt);
		chose_mode=(Spinner)findViewById(R.id.mode_spinner);
		String[] spinnercomponent = new String[]{"Mode","normal","silent","vibrate"};
		ArrayAdapter<String> arr_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnercomponent);
		chose_mode.setAdapter(arr_adapter);
		chose_mode.setClickable(true);
		
	  //Button to select contact from the address book.
		addContacts=(ImageButton) findViewById(R.id.add_from_contact);
		addContacts.setClickable(true);
		
	  //Buttons to delete contacts from the textboxes
		addedContacts=(ListView)findViewById(R.id.contactList);
		addedContacts.setBackgroundColor(Color.WHITE);
		addedContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		addedContacts.setItemsCanFocus(false);
		addedContacts.setClickable(false);
		addedContacts.setFocusable(false);
		addedContacts.setFocusableInTouchMode(false);
		addedContacts.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(listOfcontacts.isEmpty()){
					listOfcontacts.add(addedContacts.getItemAtPosition(position).toString());
				}
				else if(!listOfcontacts.contains(addedContacts.getItemAtPosition(position).toString())){
					listOfcontacts.add(addedContacts.getItemAtPosition(position).toString());
				}
				else{
					listOfcontacts.remove(addedContacts.getItemAtPosition(position).toString());
				}
			}
		});
		deleteContact=(Button)findViewById(R.id.deleteContacts);
		deleteContact.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!listOfcontacts.isEmpty()){
					for(int iterator=0;iterator<listOfcontacts.size();iterator++){
						Log.v("Contact to delete "," "+listOfcontacts.get(iterator));
						dbManager.deleteNumber(listOfcontacts.get(iterator));
					}
					addedContacts.setAdapter(getArrayAdapter(dbManager.getListOfContacts()));
					listOfcontacts.clear();
				}
				/*else{
					new DialogBox(OnCallActivity.this ,"Alert"," Please Select at least one number");
				}*/
			}
		});
	}
	
	// Method to set a click listener to all the add contact buttons	
	public void setListenersToImageButtons(){
		addContacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				check=1;
				Intent pick_contact=new Intent(Intent.ACTION_PICK,Phone.CONTENT_URI);
				startActivityForResult(pick_contact, PICK_CONTACT);
			}
		});
	}
	
	
	public ArrayAdapter<String> getArrayAdapter(String[] conList){
		ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(OnCallActivity.this,android.R.layout.simple_list_item_multiple_choice,conList){
			public View getView(int position,View convertView,ViewGroup parent){
				TextView tv=(TextView)super.getView(position, convertView, parent);
				tv.setTextColor(Color.BLACK);
				return tv;
			}
		};
		return myAdapter;
	}
	
}
