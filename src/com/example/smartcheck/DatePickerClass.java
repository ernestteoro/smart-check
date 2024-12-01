package com.example.smartcheck;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

	String selectedDate="";
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		final Calendar c=Calendar.getInstance();
		int year=c.get(Calendar.YEAR);
		int month=c.get(Calendar.MONTH);
		int day=c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dpd=new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);
		dpd.setTitle("Select the date");		
		return dpd;
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		selectedDate=""+year+"/"+monthOfYear+"/"+dayOfMonth;
	}
	
	public String getSetDate(){
		return selectedDate;
	}

}
