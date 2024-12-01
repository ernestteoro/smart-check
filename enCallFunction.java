public Class ManageCall{
	
	//Method to end the call

		public void endCall(Context context){
			TelephonyManager tm=new (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			try{
				Class cl=Class.forName(tm.getClass.getName());
				Method m=c.getdeclaredMethod("getITelephony");
				m.setAccessible(true);
				Object telephonyService=m.invoke(tm);
				c=Class.forName(telephonyService.getClass().getName());
				m=c.getdeclaredMethod("endCall");
				m.setAccessible(true);
				m.invoke(telephonyService);
			}catch(Expection ex){
			}
		}

	//Method to start the call
	public void startCall(int milis,String phoneNum){
		Intent intent= new Intent(this,MyService.class);
		PendingIntent pi= PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
		AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+millis,pi);
	}

}// end of call