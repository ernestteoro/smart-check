package db_package;

public class AutoModeSettings {
	private String _startDate;
	private String _endDate;
	
	public AutoModeSettings(String startDate,String endDate){
		this._startDate=startDate;
		this._endDate=endDate;
	}
	
	public void setStartDate(String startDate){
		this._startDate=startDate;
	}
	
	public void setEndDate(String endDate){
		this._endDate=endDate;
	}
	
	public String getStartDate(){
		
		return this._startDate;
	}
	
	public String getEndDate(){
		
		return this._endDate;
	}

}
