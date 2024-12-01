package com.example.smartcheck;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;

import db_package.DB_Manager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class JobTimerSchedule extends TimerTask{

	Handler handler=new Handler();
	private Context cntxt = HomeDashBoardActivity.returnApplicationContext() ;
	private DB_Manager dbManager=new DB_Manager(cntxt,null,null,2);
	private String[][] paramMeeting=null,planningHours=null;
	private String timeOfDay, meetingDate;
	private Calendar c;
	
	@Override
	public void run() {
		//timeOfDay=new String(""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
		handler.post(new Runnable(){
			public void run() {
				c=Calendar.getInstance(Locale.getDefault());
				timeOfDay=new String(""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
				meetingDate= new SimpleDateFormat("dd-MM-yyyy").format(new Date());
				paramMeeting=dbManager.getScheduledMetting(meetingDate,timeOfDay);
				// Executing a planning if date and time match.
				Log.v("JOBTIMERSCHEDULE TIME ", " Time == "+timeOfDay.toString());
				if(paramMeeting!=null){
					Log.v(" MESSAGE AND TIME ", " Time == "+paramMeeting[0][0]+" Start time == "+paramMeeting[0][1]+" End time== "+paramMeeting[0][2]);
					
					if((timeOfDay.equals(paramMeeting[0][1].toString())||(timeOfDay.compareTo(paramMeeting[0][1].toString()))>0)&&(paramMeeting[0][1].compareTo(timeOfDay))>0){
						Log.v("Schedule", " Planning scheduled");
						new ScheduleMeetings(paramMeeting[0][0].toString());
					}else{
						Toast.makeText(cntxt, " No planning for now ", Toast.LENGTH_LONG).show();
					}
				}
				Log.v("JOBTIMERSCHEDULE TIME 2 ", " Time == "+timeOfDay.toString());
			}
		});
	}
	
	// method for getting the planned meetings
	public void getPlannedMeetings(){
		if(paramMeeting!=null){
			if(paramMeeting.length>0){
				for(int i=0;i<paramMeeting.length;i++){
					String mList=new String("");
					planningHours[i][0]= paramMeeting[i][1];
					planningHours[i][1]= paramMeeting[i][2];
					planningHours[i][2]= paramMeeting[i][0];
					mList+=" | "+paramMeeting[i][1]+" | "+paramMeeting[i][2]+" | "+paramMeeting[i][0]+" | ";
					Log.v(" Meeting "+(i+1), " "+mList);
				}
			}
		}
	}
	
}
