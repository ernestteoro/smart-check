package com.example.smartcheck;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import db_package.DB_Manager;

public class JobAlarmReceiver extends BroadcastReceiver{
	private Context cntxt;
	private DB_Manager dbManager;
	private String[][] paramMeeting=null;//,planningHours=null;
	private String timeOfDay, meetingDate;
	private Calendar c;

	public void onReceive(Context arg0, Intent arg1) {
		cntxt = MeetingStatus.returnApplicationContext() ;
		c=Calendar.getInstance(Locale.getDefault());
		Calendar cal=Calendar.getInstance(Locale.getDefault());
		timeOfDay=new String(""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE));
		meetingDate= new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		dbManager=new DB_Manager(cntxt,null,null,0);
		paramMeeting=new String[][]{{}};
		String oldMode="";
		paramMeeting=dbManager.getScheduledMetting(meetingDate,timeOfDay);
		String currentPlanningTime=null;
		int i=0;
		
		// Executing a planning if date and time match.
		Log.v(" PARAM MEETINGS ", "== "+paramMeeting);
		if(paramMeeting!=null){
			Log.v(" Inside if parameeting not null ", "== "+paramMeeting);
			for(i=0;i<paramMeeting.length;i++){
				if(timeOfDay.toString().equals(paramMeeting[i][1].toString())){
					//currentPlanningTime=new String(paramMeeting[i][1].toString());
					cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Integer.parseInt(timeOfDay.split(":")[0].toString()), Integer.parseInt(timeOfDay.split(":")[1].toString()));
					long triggerPlanning=cal.getTimeInMillis(); 
					int t=Integer.parseInt(paramMeeting[i][1].toString().split(":")[0].toString());
					int m=Integer.parseInt(paramMeeting[i][1].toString().split(":")[1].toString());
					cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, t, m);
					long startPlanning=cal.getTimeInMillis();
					cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Integer.parseInt(paramMeeting[i][2].split(":")[0].toString()), Integer.parseInt(paramMeeting[i][2].split(":")[1].toString()));
					long endPlanning=cal.getTimeInMillis();
					Log.v(" JOB ALARM MESSAGE AND TIME ", " JOB ALARM MESSAGE AND TIME");
					Log.v(" JOB ALARM MESSAGE AND TIME ", " Start time == "+paramMeeting[i][1]+" End time== "+paramMeeting[i][2]);
					Log.v(" JOB ALARM MESSAGE AND TIME ", " TIME OF DAY == "+timeOfDay.toString());
					
					if((triggerPlanning>=startPlanning)&&(triggerPlanning<=endPlanning)){
						Log.v(" Planning ", " Planning scheduled "+timeOfDay.toString());
						oldMode=paramMeeting[i][3].toString();
						new ScheduleMeetings(paramMeeting[i][0].toString());
					}
					if(triggerPlanning>=endPlanning){
						Log.v(" Planning ", " No Planning scheduled "+timeOfDay.toString());
						new ScheduleMeetings(null).changeRingerMode(oldMode);
						Toast.makeText(cntxt, " No planning for now "+timeOfDay.toString(), Toast.LENGTH_LONG).show();
					}
					
					break;
				}
			}
		}else{
			Log.v(" Inside the else parameeting null ", "== "+paramMeeting);
			new ScheduleMeetings(null).changeRingerMode(oldMode);
		}
	}

}
