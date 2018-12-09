package com.abc;

import java.util.Collection;
import java.util.HashMap;

import static java.lang.Math.abs;

public class Customer {
    private String name;
    //private List<Account> accounts;
    private HashMap<Integer,Account> accounts;
    private static int customer_number=0;
    public int cus_number;

    public Customer(String name) {
        this.name = name;
        //this.accounts = new ArrayList<Account>();
        this.accounts= new HashMap<Integer, Account>();
        cus_number=customer_number;
        this.customer_number++;
    }

    public String getName() {
        return name;
    }

    public Customer openAccount(Account account) {
        accounts.put(account.acc_num,account);
        return this;
    }
    public void openAccount(int[] account_types) {

        for (int account_type:account_types) {
            Account account = new Account(account_type);
            accounts.put(account.acc_num,account);

        }
    }

    public int[] getAccountNumbers(){

        int[] numbers=new int[getNumberOfAccounts()];
       Object[] obj= accounts.keySet().toArray();
        for(int i=0;i<obj.length;i++){
            numbers[i]=(Integer) obj[i];

        }

        return numbers;
    }

    public void deposit(HashMap<Integer,Double> map){

        for (Integer key : map.keySet()) {
            if (!accounts.containsKey(key))
                throw new IllegalArgumentException("Bank account "+ key+" not found.");
        }

        for (Integer key : map.keySet()) {

            Account target=accounts.get(key);
                Double value = map.get(key);
                target.deposit(value);

        }
    }

    public void deposit(int key, double value){


        Account target=accounts.get(key);
        if(target==null){

            throw new IllegalArgumentException("Bank account "+ key+" not found. Deposit to account "+ key+" is cancelled.");
        }
        else
        {
            target.deposit(value);
        }
    }

    public void depositBetween(int current,int target, double value)
    {
       if( accounts.containsKey(current)&accounts.containsKey(target))
       {
           accounts.get(current).transactionTo(target,value);
           accounts.get(target).transactionFrom(current,value);
       }
      else
          throw new IllegalArgumentException("Account number(s) are not valid");
    }

    public boolean withdraw(int number,double value){

        Account target=accounts.get(number);

        if (target==null)
            return false;

        target.withdraw(value);

        return true;
    }


    public int getNumberOfAccounts() {
        return accounts.size();
    }


    public double totalInterestEarned() {
        Collection<Account> vals=accounts.values();
        double total = 0;
        for (Account a : vals)
            total += a.interestEarned();
        return total;
    }

    public String getStatement() {
        Collection<Account> vals=accounts.values();
        String statement = null;
        statement = "Statement for " + name + "\n";
        double total = 0.0;
        for (Account a : vals) {
            statement += "\n" + statementForAccount(a) + "\n";
            total += a.sumTransactions();
        }
        statement += "\nTotal In All Accounts " + toDollars(total);
        return statement;
    }

    private String statementForAccount(Account a) {
        String s = "";

       //Translate to pretty account type
        switch(a.getAccountType()){
            case Account.CHECKING:
                s += "Checking Account\n";
                break;
            case Account.SAVINGS:
                s += "Savings Account\n";
                break;
            case Account.MAXI_SAVINGS:
                s += "Maxi Savings Account\n";
                break;
        }

        //Now total up all the transactions
        double total = 0.0;
        for (Transaction t : a.transactions) {
            s += "  " + (t.type<0 ? (t.amount < 0 ? "withdrawal" : "deposit"):(t.type==1 ? "transaction received from account: "+t.accountNum+", amount:":"transacted to account: "+t.accountNum+", amount:")) + " " + toDollars(t.amount) + "\n";
            total += t.amount;
        }
        s += "Total " + toDollars(total);
        return s;
    }

    public void insertTransactionToAccount(Transaction t,int accountNumber)// Test only
    {
        accounts.get(accountNumber).transactions.add(t);
    }


    private String toDollars(double d){
        return String.format("$%,.2f", abs(d));
    }
}
