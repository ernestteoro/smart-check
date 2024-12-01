package db_package;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB_Manager extends SQLiteOpenHelper {
	private static final int DB_VERSION=3;
	//private static final String DB_NAME="contactlist.db";
	private static final String TABLE_PHONE="list_number_tbl";
	//private static final String PHONE_ID="id"; 
	private static final String NUMBER_LIST="phone_number";
	SQLiteDatabase dbm;

	public DB_Manager(Context context, String name, CursorFactory factory,int version) {
		super(context, "contactlist.db", null, DB_VERSION);
		Log.d(null," Database created successfully");
		//dbm=this.getWritableDatabase();
	}

	@Override 
	public void onCreate(SQLiteDatabase db) {
		String sql_query="CREATE TABLE list_number_tbl(id INTEGER PRIMARY KEY AUTOINCREMENT,phone_number TEXT,ring_mode TEXT);";
		db.execSQL(sql_query);
		//creating auto mode settings table
		String sql_query_meetings="CREATE TABLE meeting_tbl(id INTEGER PRIMARY KEY AUTOINCREMENT,start_date VARCHAR(25),msg_meeting VARCHAR(255),start_time VARCHAR(20),end_time VARCHAR(20),audio_mode VARCHAR(25));";
		db.execSQL(sql_query_meetings);	
		//creating sms mode settings table
		String sql_query_sms="CREATE TABLE sms_mode_tbl(id INTEGER PRIMARY KEY AUTOINCREMENT,secret_code VARCHAR(35));";
		db.execSQL(sql_query_sms);
		dbm=this.getWritableDatabase();
		Log.d(null, " All the tables are created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  // Dropping all the tables whose structures were changed and recreating them.
		db.execSQL("DROP TABLE IF EXISTS list_number_tbl ");
		db.execSQL("DROP TABLE IF EXISTS meeting_tbl ");
		db.execSQL("DROP TABLE IF EXISTS auto_mode_tbl ");
		db.execSQL("DROP TABLE IF EXISTS sms_mode_tbl ");
		onCreate(db);
	}
	
	// adding new phone number to the database
	public void addNewNumber(String num,String ringMode){
		SQLiteDatabase db=this.getWritableDatabase();
		String query="SELECT phone_number FROM list_number_tbl WHERE phone_number ="+num+" LIMIT 1;";
		Cursor cursor=db.rawQuery(query, null);
		//cursor.moveToFirst();
		if(cursor.getCount()==0){
			ContentValues values= new ContentValues();
			values.put("phone_number", num);
			values.put("ring_mode", ringMode);
			db.insert("list_number_tbl",null, values);
		}
		db.close();
	}
	
	// add new settings for auto mode to the database
	public void addMettingSettings(String startDate, String msgMeeting, String startTime, String endTime,String audioMode){
		SQLiteDatabase db=this.getWritableDatabase();
			ContentValues values= new ContentValues();
			values.put("start_date", startDate);
			values.put("msg_meeting", msgMeeting);
			values.put("start_time", startTime);
			values.put("end_time", endTime);
			values.put("audio_mode", audioMode);
			db.insert("meeting_tbl",null, values);
		db.close();
		//Log.v(" DATABASE HOUR OF THE DAY", " Date == "+startDate.toString()+" Time == "+startTime);
	}
	
	// Getting the meeting settings that was set
	public String[][] getScheduledMetting(String meetingDate,String timeNow){
		
		if(dbm!=null){
			//SQLiteDatabase dbm=this.getWritableDatabase();
			String[][] meetingParams=null;
			String sql="SELECT msg_meeting,start_time ,end_time,audio_mode FROM meeting_tbl WHERE start_date=\""+meetingDate+"\" AND start_time<=\""+timeNow+"\";";
			Cursor csr=dbm.rawQuery(sql, null);
			csr.moveToFirst();
			if(csr.getCount()>0){
				int i=0;
				meetingParams=new String[csr.getCount()][csr.getColumnCount()];
				//Log.v(" DATABASE HOUR OF THE DAY 2", " There is a planning for now ");
				do{
						meetingParams[i][0]=csr.getString(csr.getColumnIndex("msg_meeting"));
						meetingParams[i][1]=csr.getString(csr.getColumnIndex("start_time"));
						meetingParams[i][2]=csr.getString(csr.getColumnIndex("end_time"));
						meetingParams[i][3]=csr.getString(csr.getColumnIndex("audio_mode"));
						//Log.v(" DATABASE HOUR OF THE DAY 3" , " There is a planning for now "+meetingParams[i][1]);
					i+=1;
				}while(csr.moveToNext());
				dbm.close();	
				return meetingParams;
			}
			dbm.close();
		}
			
			return null;
	}
	
	public String[][] getAllMettings(){
		String[][] allPlannedMeetings=null;
		SQLiteDatabase dbm=this.getWritableDatabase();
		//Log.v(" database ", " Database object === "+dbm.toString()+" Object  === "+dbm);
		String sql="SELECT start_date,msg_meeting,start_time ,end_time FROM meeting_tbl";
		Cursor csr=dbm.rawQuery(sql, null);
		csr.moveToFirst();
		if(csr.getCount()>0){
			int i=0;
			allPlannedMeetings=new String[csr.getCount()][csr.getColumnCount()];
			do{
				allPlannedMeetings[i][0]=csr.getString(csr.getColumnIndex("start_date"));
				allPlannedMeetings[i][1]=csr.getString(csr.getColumnIndex("msg_meeting"));
				allPlannedMeetings[i][2]=csr.getString(csr.getColumnIndex("start_time"));
				allPlannedMeetings[i][3]=csr.getString(csr.getColumnIndex("end_time"));
				i+=1;
			}while(csr.moveToNext());
			dbm.close();	
			return allPlannedMeetings;
		}
		dbm.close();
		return allPlannedMeetings;
	}
	
	public void deleteNumber(String num){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM list_number_tbl WHERE phone_number = '"+num+"'");
		Log.v(" Number "," deleted num === "+num);
		db.close();

	}
	
	public boolean getPhoneNumber(String num){
		SQLiteDatabase dbhandler=this.getWritableDatabase();
		String query="SELECT "+NUMBER_LIST+" FROM "+TABLE_PHONE+" WHERE "+NUMBER_LIST+" =\""+num+"\" LIMIT 1;";
		Cursor cursor=dbhandler.rawQuery(query, null);
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			dbhandler.close();
			return true;
		}
		dbhandler.close();
		return false;
	}
	
	public String getRingMode(String num){
		SQLiteDatabase dbhandler=this.getWritableDatabase();
		String rMode="";
		String query="SELECT ring_mode FROM "+TABLE_PHONE+" WHERE "+NUMBER_LIST+" =\""+num+"\" LIMIT 1;";
		Cursor cursor=dbhandler.rawQuery(query, null);
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			rMode=cursor.getString(cursor.getColumnIndex("ring_mode"));
		}
		dbhandler.close();
		return rMode;
	}
	
	public String[] getListOfContacts(){
		SQLiteDatabase dbhandler=this.getWritableDatabase();
		String query="SELECT phone_number FROM list_number_tbl;";
		Cursor cursor=dbhandler.rawQuery(query, null);
		String[] listPhoneNum;
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			int i=0;
			listPhoneNum=new String[cursor.getCount()];
			do{
				listPhoneNum[i]=cursor.getString(cursor.getColumnIndex("phone_number"));
				i++;
			}while(cursor.moveToNext());
			dbhandler.close();
			return listPhoneNum;
		}
		dbhandler.close();
		return new String[]{"No Contacts"};
	}
	
	// setting the secret code for sms mode
	public void addSecretKey(String num){
		SQLiteDatabase db=this.getWritableDatabase();
			ContentValues values= new ContentValues();
			values.put("secret_code", num);
			db.insert("sms_mode_tbl",null, values);
		db.close();
	}
	
	// getting the secret key on message received
	public String getSecretKey(){
		String secretKey=null;
		SQLiteDatabase dbm=this.getWritableDatabase();
		Log.v(" database ", " Database object === "+dbm.toString()+" Object  === "+dbm);
		String sql="SELECT secret_code FROM sms_mode_tbl;";
		Cursor csr=dbm.rawQuery(sql, null);
		csr.moveToPrevious();
		if(csr.getCount()>0){
			secretKey=csr.getString(csr.getColumnIndex("secret_code"));
		}
		dbm.close();
		return secretKey;
	}
	// the secret code list for sms 
	public String[] getListOfCode(){
		SQLiteDatabase dbhandler=this.getWritableDatabase();
		String query="SELECT secret_code FROM sms_mode_tbl;";
		Cursor cursor=dbhandler.rawQuery(query, null);
		String[] listSecretCode;
		cursor.moveToFirst();
		if(cursor.getCount()>0){
			int i=0;
			listSecretCode=new String[cursor.getCount()];
			do{
				listSecretCode[i]=cursor.getString(cursor.getColumnIndex("secret_code"));
				i++;
			}while(cursor.moveToNext());
			dbhandler.close();
			return listSecretCode;
		}
			dbhandler.close();
			return new String[]{"No codes"};
	}
	
	// Delete sms codes 
	public void deleteSmsCodes(String code){
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM sms_mode_tbl WHERE secret_code = "+code+";");
		db.close();
	}
	
	// Delete meetings for scheduled meetings from the database
	public void deleteMettingSettings(String startDate){
		SQLiteDatabase db=this.getWritableDatabase();
		Log.v("Delete", "Deleting one or more meetings");
		db.execSQL("DELETE FROM meeting_tbl WHERE start_date='"+startDate+"';");
		db.close();
	}
		

}
