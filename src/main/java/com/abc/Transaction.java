package com.abc;

import java.util.Date;

public class Transaction {
    public final double amount;
    public  int type=-1; // -1 normal transaction, 0 sending to another account, 1 receiving from and account
    public int accountNum;
    private Date transactionDate;

    public Transaction(double amount) {
        this.amount = amount;
        this.transactionDate = DateProvider.getInstance().now();
    }

    public void setDate(int year, int month, int day, int hour, int minute, int second) { // Test purpose only
       this.transactionDate.setYear(year);
       this.transactionDate.setMonth(month);
       this.transactionDate.setDate(day);
       this.transactionDate.setHours(hour);
       this.transactionDate.setMinutes(minute);
       this.transactionDate.setSeconds(second);
    }

    public Transaction(double amount,int type, int accountNum) {
        this.type=type;
        this.accountNum=accountNum;
        this.amount = amount;
        this.transactionDate = DateProvider.getInstance().now();
    }

    public Date getTransactionDate() {
        return transactionDate;
    }
}
