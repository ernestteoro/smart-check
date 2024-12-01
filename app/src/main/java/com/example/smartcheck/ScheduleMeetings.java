package com.example.smartcheck;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import android.util.Log;
import android.widget.Toast;
import db_package.DB_Manager;

public class ScheduleMeetings {
	private Context cntxt = HomeDashBoardActivity.returnApplicationContext();
	//HomeDashBoardActivity.returnApplicationContext() ;
	private Intent intent;
	DB_Manager dbManager;
	private AudioManager audioManager;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;
	PhoneStateListener callTelephonyListener;
	
	public ScheduleMeetings(final String msgToSend){
		telephonyManager=(TelephonyManager)cntxt.getSystemService(Context.TELEPHONY_SERVICE);
		audioManager=(AudioManager)cntxt.getSystemService(Context.AUDIO_SERVICE);
		intent=HomeDashBoardActivity.returnApplicationIntent();
		changeRingModeOnSms();
		callTelephonyListener=new PhoneStateListener(){
			public void onCallStateChanged(int state,String incommingNumber){
				if(state==TelephonyManager.CALL_STATE_RINGING){
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					ScheduleMeetings.this.endCalls();
					try {
						telephonyService.endCall();
					}catch (RemoteException e) {
						e.printStackTrace();
					}
					Toast.makeText(cntxt, " Incomming number ==  "+incommingNumber, Toast.LENGTH_LONG).show();
					Log.v(" Incomming number", " Incomming number ==  "+incommingNumber);
					sendMessage(incommingNumber, msgToSend);
					// Unregister the phone listener 
					telephonyManager.listen(callTelephonyListener, PhoneStateListener.LISTEN_NONE);
					//audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
		};
		telephonyManager.listen(callTelephonyListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	//end of the scheduling metting method
	
   //Method to end the call
	@SuppressWarnings("unchecked")
	public void endCalls(){
		telephonyManager = (TelephonyManager)cntxt.getSystemService(Context.TELEPHONY_SERVICE);
		 //Java Reflections
		 Method m = null;
		 @SuppressWarnings("rawtypes")
		Class c = null;
			try {
				c = Class.forName(telephonyManager.getClass().getName());
				m = c.getDeclaredMethod("getITelephony");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			 
		 	m.setAccessible(true);
			try {
				telephonyService = (ITelephony)m.invoke(telephonyManager);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	// Method to send sms when the call is dropped 
	public void sendMessage(String phoneNumber,String msgBody){
		SmsManager smsManager= SmsManager.getDefault();
		if(!isEmulator()){
		List<String> messages=smsManager.divideMessage(msgBody);
			for(String message:messages){
				smsManager.sendTextMessage(phoneNumber, null, message, null, null);
			}
			Toast.makeText(cntxt.getApplicationContext()," Message sent", Toast.LENGTH_LONG).show();
		}
	}
	
	public void changeRingModeOnSms(){
		final String ACTION="android.provider.Telephony.SMS_RECEIVED";
		if(intent.getAction()!=null){
			if(intent.getAction().equals(ACTION)){
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
				Toast.makeText(cntxt, " RINGER_MODE_SILENT ", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void changeRingerMode(String modeToBechanged){
		AudioManager audManager=(AudioManager)cntxt.getSystemService(Context.AUDIO_SERVICE);
		if(modeToBechanged.equals("normal")){
			audManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
		}
		if(modeToBechanged.equals("silent")){
			audManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
		if(modeToBechanged.equals("vibrate")){
			audManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}	
	}
	
	// Method to check for real device or emulator
	public static boolean isEmulator(){
		return Build.FINGERPRINT.contains("google_sdk")
				||Build.FINGERPRINT.contains("Emulator")
				||Build.FINGERPRINT.contains("Genymotion")
				||Build.FINGERPRINT.contains("Android SDK built for x86")
				||Build.FINGERPRINT.startsWith("generic")
				||Build.BRAND.startsWith("generic")&&Build.DEVICE.startsWith("generic")
				||"google_sdk".equals(Build.PRODUCT);
	}
}
