package tracker;

import java.time.LocalDate;

public class Transaction {
	
	public int Amount;
	public String Category;
	public LocalDate dt = LocalDate.now(); 
	public boolean isIncome ;
	
	public Transaction(int Amount , String Category , LocalDate dt , boolean isIncome ) {
		this.Amount = Amount ;
		this.Category = Category;
		this.dt = dt;
		this.isIncome = isIncome ;
	}
}
