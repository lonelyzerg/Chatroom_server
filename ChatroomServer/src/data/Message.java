package data;

public class Message {
	private String username;
	private String messeage;
	
	public Message(String username, String messeage) {
		this.username = username;
		this.messeage = messeage;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMesseage() {
		return messeage;
	}
	public void setMesseage(String messeage) {
		this.messeage = messeage;
	}
	

}
