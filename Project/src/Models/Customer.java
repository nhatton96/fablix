package Models;

public class Customer {
	String email;
	String password;
	
	public Customer (){
		this.email = "";
		this.password = "";
	}
	
	public Customer(String email, String password){
		this.email = email;
		this.password = password;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPasswor() {
		return this.password;
	}
}
