package com.example.smartcheck;
import db_package.DB_Manager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver{
	private final String ACTION="android.provider.Telephony.SMS_RECEIVED";
	private DB_Manager dbM;
	public void onReceive(Context context, Intent intent) {
		dbM=new DB_Manager(context,null,null,0);
		if(intent.getAction().equals(ACTION)){
			final Bundle bundle=intent.getExtras();
			if(bundle!=null){
				try{
					boolean check=false;
					Object[] pdu=(Object[])bundle.get("pdus");
					SmsMessage[] msge=new SmsMessage[pdu.length];
					for(int i=0;i<pdu.length;i++){
						msge[i]=SmsMessage.createFromPdu((byte[])pdu[i]);
						String msgBody=msge[i].getDisplayMessageBody();
						for(int j=0;j<dbM.getListOfCode().length;j++){
							if(dbM.getListOfCode()[j].toString().equals(msgBody)){
								AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
								if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_SILENT||audioManager.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE){
									audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
									audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
									Log.v(" Message body"," 4 == "+msgBody);
								}
								check=true;
								break;
							}
						}
						if(check){
							break;
						}
					}
					
				}catch(Exception e){
					Log.v("Error ","There is an error");
				}
			}
		}
	}
}
