package bankexcercise;
public class BankAccount {
	
	private String surname;
	private String firstName;
	private BankAccountData data = new BankAccountData();
	public static int count = 0;
	
	public BankAccount(int accountID, String accountNumber, String surname, String firstName, String accountType, double balance, double overdraft){
		this.data.accountID = accountID;
		this.data.accountNumber = accountNumber;
		this.surname = surname;
		this.firstName = firstName;
		this.data.accountType = accountType;
		this.data.balance = balance;
		this.data.overdraft = overdraft;
	}
	
	public BankAccount(){
		this(0, "", "", "", "", 0.0, 0.0);
	}
	
	
	
	public int getAccountID() {
		return data.accountID;
	}
	
	public void setAccountID(int accountID) {
		this.data.accountID = accountID;
	}
	
	public String getAccountNumber() {
		return data.accountNumber;
	}
	
	public void setAccountNumber(String accountNumber) {
		this.data.accountNumber = accountNumber;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getAccountType() {
		return data.accountType;
	}
	
	public void setAccountType(String accountType) {
		this.data.accountType = accountType;
	}
	
	public String getAccType(){
		return data.accountType;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public double getBalance() {
		return data.balance;
	}
	
	public void setBalance(double balance) {
		this.data.balance = balance;
	}
	
	public double getOverdraft() {
		return data.overdraft;
	}
	
	public void setOverdraft(double overdraft) {
		this.data.overdraft = overdraft;
	}
	
	public String toString(){
		return "\nAccount id: " + data.accountID +  "Account Num: " + data.accountNumber + "\nName: " + surname + " " + firstName+"\n";
	}

}
