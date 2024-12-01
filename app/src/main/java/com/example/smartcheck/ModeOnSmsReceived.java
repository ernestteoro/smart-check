package com.example.smartcheck;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

public class ModeOnSmsReceived extends Activity{
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		changePhoneRingMode();
		
	}
	
   // method to change the ring mode of the phone on sms received 
	public void changePhoneRingMode(){
		Log.v("ModeOnSmsReceived", " inside ModeOnSmsReceived");
		AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		 Log.v("ModeOnSmsReceived", " after etting the audio services");
		if( audioManager.getRingerMode()== AudioManager.RINGER_MODE_VIBRATE || audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT){
			Log.v("ModeOnSmsReceived", " inside if of silent and vibrate mode");
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			Log.v("ModeOnSmsReceived", " changing the mode from silent or vibrate to normal");
		}
	}
}
