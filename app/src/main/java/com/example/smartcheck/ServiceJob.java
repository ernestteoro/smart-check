package com.example.smartcheck;

import java.util.Timer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import db_package.DB_Manager;

public class ServiceJob extends Service{

	private Timer jobTimer=null;
	private static final long NOTIFYTIME=1000;
	@SuppressWarnings("unused")
	private DB_Manager dbManager;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void onCreate(){
		Log.v(" ServiceJob == ", " Service started ");
				
			if(jobTimer!=null){
				jobTimer.cancel();
			}else{
				jobTimer=new Timer();
			}
			Log.v(" ServiceJob == ", " Service started ");
			jobTimer.schedule(new JobTimerSchedule(),NOTIFYTIME);
			Log.v("SERVICE", " This service is running with a job scheduled");

	}
}