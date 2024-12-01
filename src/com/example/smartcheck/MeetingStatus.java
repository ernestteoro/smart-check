package com.example.smartcheck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import db_package.DB_Manager;

public class MeetingStatus extends Activity{
	private Button saveSettings,dropMeetings;
	private EditText customeMessage,dateStatus,startTime,endTime;
	private Spinner status;
	private DatePickerDialog startDateDialog;
	private TimePickerDialog timeDialog;
	private SimpleDateFormat dateFormater;
	private static Context context;
	private static Intent intent;
	private ListView meetingList;
	private ArrayList<String> listMeeting;
	DB_Manager dbManager;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_status_layout);
		initializeComponents();
		addClickListenersToButtons();
		dbManager=new DB_Manager(this,null,null,0);
		meetingList.setAdapter(this.getArrayAdapter(getMeetingsListFromDB(dbManager.getAllMettings())));
		context=getApplicationContext();
		intent=this.getIntent();
		//startService(new Intent(MeetingStatus.this,ServiceJob.class));
	}
	
	public void initializeComponents(){
		saveSettings=(Button)findViewById(R.id.save_meeting_settings);
		customeMessage=(EditText)findViewById(R.id.status_text_view);
		status=(Spinner)findViewById(R.id.status_spinner);
		String[] statusValues=new String[]{"Status","Busy","Meeting","At school","Working"};
		ArrayAdapter<String> statusAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,statusValues);
		status.setAdapter(statusAdapter);
		status.setFocusable(true);
		dateStatus=(EditText)findViewById(R.id.date_status);

		// show DatePickerDialog when there is a key pressed on dateStatus EditText
		dateStatus.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((KeyEvent.ACTION_DOWN==event.getAction())&&((keyCode>=KeyEvent.KEYCODE_0&&keyCode<=KeyEvent.KEYCODE_9)||(keyCode==KeyEvent.KEYCODE_A&&keyCode==KeyEvent.KEYCODE_Z))){
					 showDatePickerDialog();
				}
				return false;
			}
			
		});
		
		startTime=(EditText)findViewById(R.id.start_time_status);
		endTime=(EditText)findViewById(R.id.end_time_status);
		meetingList=(ListView)findViewById(R.id.planned_meetings);
		meetingList.setBackgroundColor(Color.WHITE);
		meetingList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		meetingList.setItemsCanFocus(true);
		meetingList.setClickable(false);
		meetingList.setFocusable(false);
		meetingList.setFocusableInTouchMode(false);
		listMeeting=new ArrayList<String>();
		meetingList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(listMeeting.isEmpty()){
					listMeeting.add(meetingList.getItemAtPosition(position).toString());
				}else if(!listMeeting.contains(meetingList.getItemAtPosition(position))){
					listMeeting.add(meetingList.getItemAtPosition(position).toString());
				}else{
					listMeeting.remove(meetingList.getItemAtPosition(position).toString());
				}
			}
		});
		
		dropMeetings=(Button)findViewById(R.id.drop_meeting);	
	}
	
	public static Context returnApplicationContext(){
		return context;
	}
	
	public static Intent returnApplicationIntent(){
		return intent;
	}
	
	public void addClickListenersToButtons(){
		// set click event of the saveSettings button
		saveSettings.setOnClickListener(new OnClickListener (){
			public void onClick(View v) {
				if(status.getSelectedItem().toString().equals("Status")){
					new DialogBox(MeetingStatus.this, "Status", "Please selct your status !");
					startTime.setText("");
					endTime.setText("");
					dateStatus.setText("");
				}
				else if(customeMessage.getText().toString().isEmpty()){
					new DialogBox(MeetingStatus.this, "Message", "Please enter your personalized message !");
					startTime.setText("");
					endTime.setText("");
					dateStatus.setText("");
					customeMessage.setText("");
				}
				else{
					AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
					String audioMode="";
					if(audioManager.getMode()==AudioManager.RINGER_MODE_NORMAL){
						audioMode="normal";
					}else if(audioManager.getMode()==AudioManager.RINGER_MODE_SILENT){
						audioMode="silent";
					}else{
						audioMode="vibrate";
					}
					dbManager.addMettingSettings(dateStatus.getText().toString(), customeMessage.getText().toString(), startTime.getText().toString(), endTime.getText().toString(),audioMode.toString());
					meetingList.setAdapter(null);
					meetingList.setAdapter(getArrayAdapter(getMeetingsListFromDB(dbManager.getAllMettings())));
					startTime.setText("");
					endTime.setText("");
					dateStatus.setText("");
					customeMessage.setText("");
				}
			}
		});
		
		// setting the value of dateStatus EditText to the selected date from the DatePickerDialog
		dateStatus.setOnFocusChangeListener(new OnFocusChangeListener(){
			public void onFocusChange(View v, boolean hasFocus) {
				if((dateStatus.getText().toString().isEmpty())&&(hasFocus))
					showDatePickerDialog();	
			}
		});
		
		// setting the value of startTime EditText to the selected time from the TimePickerDialog
		startTime.setOnFocusChangeListener(new OnFocusChangeListener(){
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					showTimePickerDialog(true);
				}
			}
		});
		
		// setting the value of endTime EditText to the selected time from the TimePickerDialog	
		endTime.setOnFocusChangeListener(new OnFocusChangeListener(){
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					showTimePickerDialog(false);
				}
			}
		});

		dropMeetings.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(!listMeeting.isEmpty()){
					for(int through=0;through<listMeeting.size();through++){
						dbManager.deleteMettingSettings(listMeeting.get(through).toString());
					}
					meetingList.setAdapter(getArrayAdapter(getMeetingsListFromDB(dbManager.getAllMettings())));
					listMeeting.clear();
				}
			}
		});
		
		
	}
	
	// Method to pop up a Date Picker Dialog 
	public void showDatePickerDialog(){
		Calendar newCalendar=Calendar.getInstance();
		dateFormater=new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
		startDateDialog=new DatePickerDialog(this, new OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
				Calendar cd=Calendar.getInstance();
				cd.set(year, monthOfYear, dayOfMonth);
				dateStatus.setText(dateFormater.format(cd.getTime()));
			}
		},newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
		startDateDialog.show();
	}
	
	// Method to pop up a Time Picker Dialog 
	public void showTimePickerDialog(final boolean startEnd){
		final Calendar timeCalendar=Calendar.getInstance();
		timeDialog=new TimePickerDialog(this, new OnTimeSetListener(){
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				if(startEnd){
					startTime.setText(""+hourOfDay+":"+minute);
				}else{
					endTime.setText(""+hourOfDay+":"+minute);
				}
			}
		}, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), false);
		timeDialog.show();
	}
	
	public ArrayAdapter<String> getArrayAdapter(String[] meetingList){
		ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(MeetingStatus.this,android.R.layout.simple_list_item_multiple_choice,meetingList){
			public View getView(int position,View convertView,ViewGroup parent){
				TextView tv=(TextView)super.getView(position, convertView, parent);
				tv.setTextColor(Color.BLACK);
				return tv;
			}
		};
		return myAdapter;
	}
	
	public String[] getMeetingsListFromDB(String[][] mLists){
			if(mLists!=null){
				String[] listOfMeetings=new String[mLists.length];
				for(int i=0;i<mLists.length;i++){
					listOfMeetings[i]=mLists[i][0];
					Log.v("Meeting "+(i+1), " == "+mLists[i][0]+" | "+mLists[i][1]+" | "+mLists[i][2]+" | "+mLists[i][3]);
				}
			return listOfMeetings;
		}
		return new String[]{"No Meeting planned"};
	}
}
