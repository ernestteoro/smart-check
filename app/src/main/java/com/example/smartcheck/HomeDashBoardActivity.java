package com.example.smartcheck;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeDashBoardActivity extends Activity implements OnItemClickListener
{
	private static Context context_service;
	private static Intent intent_service;
	static final String EXTRA_MAP="map";
	private AlarmManager jobAlarmManager;
	final int interval=5000;
	static final LauncherIcon[] ICONS={
		new LauncherIcon(R.drawable.calltune," ON CALL  ","callsetting.png"),
		new LauncherIcon(R.drawable.message,"ON MESSAGE","message.png"),
		new LauncherIcon(R.drawable.automode,"MY STATUS","automode.png"),
		new LauncherIcon(R.drawable.about,"APP HELP ","about.jpg")
	};
	
	static final LauncherIcon[] ICONS1={
		new LauncherIcon(R.drawable.calltune," OnCallActivity  ","callsetting.png"),
		new LauncherIcon(R.drawable.message,"ON_MESSAGE","message.png"),
		new LauncherIcon(R.drawable.automode,"MY STATUS","automode.png"),
		new LauncherIcon(R.drawable.about,"APP_HELP ","about.jpg")
	};
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);
		context_service=getApplicationContext();
		intent_service=this.getIntent();
		
		// Starting the service for planned meetings
			jobAlarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
			startScheduledPlanning();
		// End of the scheduling 
		//startService(new Intent(HomeDashBoardActivity.this,ServiceJob.class));
		
		// Dash board 
		GridView gridView=(GridView) findViewById(R.id.homedash_board);
			gridView.setAdapter(new ImageAdapter(this));
			gridView.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View v, MotionEvent event) {
					return false;
				}
				
			});
			gridView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id){
					
					Intent intent=null;
					// = null;
					if(position==0)
					{
						intent=new Intent(getApplicationContext(),OnCallActivity.class);
						startActivity(intent);
					}
					if(position==1)
					{
						intent=new Intent(getApplicationContext(),SmsMode.class);
						startActivity(intent);
					}
					if(position==2)
					{
						intent=new Intent(getApplicationContext(),MeetingStatus.class);
						startActivity(intent);
					}
					if(position==3)
					{
						
					}
				}
			});
			//context_service=getApplicationContext();
			
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_menu) {
            new DialogBox(HomeDashBoardActivity.this,"Alert","Suggestion Menu Select");
        }
        if (id == R.id.feed_back_menu) {
        	new DialogBox(HomeDashBoardActivity.this,"Alert","Suggestion Menu Select");
        }
        if (id == R.id.sugestion_menu) {
        	new DialogBox(HomeDashBoardActivity.this,"Alert","Suggestion Menu Select");
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void onItemClick(AdapterView<?> parent, View view, int position,long id)
	{
		Intent intent=new Intent(this,OnCallActivity.class);
		intent.putExtra(EXTRA_MAP,ICONS1[position].map);
		startActivity(intent);
	}
	
	static class LauncherIcon{
		final String text;
		final String map;
		final int imgId;
		public LauncherIcon(int imgId,String text,String map)
		{
			super();
			this.imgId=imgId;
			this.text=text;
			this.map=map;
		}
	}
	
	static class ImageAdapter extends BaseAdapter{

		private Context myContext;
		
		public ImageAdapter(Context c)
		{
			myContext=c;
		}
		@Override
		public int getCount() {
			return ICONS.length;
		}

		@Override
		public LauncherIcon getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		static class ViewHolder {
            public ImageView icon;
            public TextView text;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			 View v = convertView;
	            ViewHolder holder;
	            if (v == null) {
	                LayoutInflater vi = (LayoutInflater) myContext.getSystemService(
	                    Context.LAYOUT_INFLATER_SERVICE);
	 
	                v = vi.inflate(R.layout.homedashboard_icon, null);
	                holder = new ViewHolder();
	                holder.text = (TextView) v.findViewById(R.id.icon_text);
	                holder.icon = (ImageView) v.findViewById(R.id.icon_img);
	                v.setTag(holder);
	            } else {
	                holder = (ViewHolder) v.getTag();
	            }
	 
	            holder.icon.setImageResource(ICONS[position].imgId);
	            holder.text.setText(ICONS[position].text);
	            return v;
		}
	}
	
	public static Context returnApplicationContext(){
		return context_service;
	}
	
	public static Intent returnApplicationIntent(){
		return intent_service;
	}
	
	/*public static Context returnApplicationContext(){
		return context;
	}*/
	
	public void startScheduledPlanning(){
		Intent planningIntent=new Intent(this,JobAlarmReceiver.class);
		PendingIntent planningPendingIntent=PendingIntent.getBroadcast(this, 0, planningIntent, 0);
		jobAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, planningPendingIntent);
		
	}
	
	
	public void onResume(){
		/*Intent planningIntent=new Intent(this,JobAlarmReceiver.class);
		PendingIntent planningPending=PendingIntent.getBroadcast(this,0 , planningIntent, 0);
		jobAlarmManager.cancel(planningPending);*/
		super.onResume();
	}
}