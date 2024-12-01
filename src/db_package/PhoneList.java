package db_package;

public class PhoneList {
	private int _id;
	private String _phoneNum;
	
	public PhoneList(){
		
	}
	public PhoneList(String phoneNum){
		this._phoneNum=phoneNum;
	}
	
	public int getId(){
		return this._id;
	}
	
	public String getPhoneNum(){
		return this._phoneNum;
	}
	
	public void setId(int id){
		this._id=id;
	}
	
	public void setPhoneNum(String phoneNum){
		this._phoneNum=phoneNum;
	}
	
}
