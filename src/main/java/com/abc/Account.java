package com.abc;

import java.util.*;

public class Account {

    public static final int CHECKING = 0;
    public static final int SAVINGS = 1;
    public static final int MAXI_SAVINGS = 2;
    private static int account_number=0;
    public int acc_num;

    private final int accountType;
    public List<Transaction> transactions;

    public Account(int accountType) {

        if(accountType<0 || accountType>2)
            throw new IllegalArgumentException("Account type "+accountType+" does not exist.");

        acc_num=account_number;
        account_number++;
        this.accountType = accountType;
        this.transactions = new ArrayList<Transaction>();
    }

    public void insertTransaction(Transaction t) //For test purposes only
    {
        transactions.add(t);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
            transactions.add(new Transaction(amount));
        }
    }

    public void withdraw(double amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("amount must be greater than zero");
    } else {
        transactions.add(new Transaction(-amount));
    }
}
    public void transactionTo(int target,double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
            transactions.add(new Transaction(-amount, 0,target));
        }
    }
    public void transactionFrom(int source,double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
            transactions.add(new Transaction(amount, 1,source));
        }
    }

    public double interestEarned() {

        double amount = sumTransactions();
        switch(accountType){
            case SAVINGS:
                if (amount <= 1000)
                    return amount * 0.001;
                else
                    return 1 + (amount-1000) * 0.002;
//            case SUPER_SAVINGS:
//                if (amount <= 4000)
//                    return 20;
            case MAXI_SAVINGS:
                amount = sumTransactionsMaxi();
                return amount;
            default:
                return amount * 0.001;
        }
    }

    public double interestPerDay(double amount,double interest)
    {
        return (amount*interest)/365;
    }

    public int calculateDayDiff(Date d1, Date d2)
    {
        //d2 is sooner than d1. Thus d2 always bigger.
        int d1_day=d1.getDate();
        int d1_month=d1.getMonth();
        int d1_year=d1.getYear();

        int d2_day=d2.getDate();
        int d2_month=d2.getMonth();
        int d2_year=d2.getYear();

        int day_count=0;

        if(d2_year-d1_year>1)
        {
            day_count+=365*(d2_year-d1_year);
            d1_year=d2_year+1;
        }
        day_count+=monthlyDifference(d1_year,d1_month,d2_year,d2_month) +d2_day-d1_day;


        if(day_count==10)
        {
            if((d2.getHours()*3600+d2.getMinutes()*60+d2.getSeconds())>(d1.getHours()*3600+d1.getMinutes()*60+d1.getSeconds()))
            {
                day_count+=1;
            }
        }

        return day_count;
    }

    public double sumTransactionsMaxi()
    {
        double amount = 0.0;
        Date pointer;
        pointer=transactions.get(0).getTransactionDate();
        int last_diff=-1;
        int penaltyDays=0;
        double earnedInterest=0.0;

        if (transactions.get(0).amount<0.0)
        {
            penaltyDays=10;
            pointer=transactions.get(0).getTransactionDate();
        }

        for (Transaction t: transactions)
        {

            last_diff=calculateDayDiff(pointer,t.getTransactionDate());

            if(penaltyDays>=last_diff)
            {
                earnedInterest+=interestPerDay(amount,0.001)*last_diff;
                penaltyDays-=last_diff;
            }
            else if(penaltyDays<last_diff)
            {
                if(penaltyDays!=0)
                {
                    earnedInterest+=interestPerDay(amount,0.001)*penaltyDays;
                }

                earnedInterest+=interestPerDay(amount,0.05)*(last_diff-penaltyDays);
                penaltyDays=0;
            }
            else if(penaltyDays==0)
            {
                earnedInterest+=interestPerDay(amount,0.05)*(last_diff);
            }

            if (t.amount<0.0)
            {
                penaltyDays=10;
            }

            amount+=t.amount;

            pointer=t.getTransactionDate();

        }

        return earnedInterest;
    }

    public double sumTransactions() {
       return checkIfTransactionsExist(true);
    }

    private double checkIfTransactionsExist(boolean checkAll) {
        double amount = 0.0;
        for (Transaction t: transactions)
            amount += t.amount;
        return amount;
    }

    private int monthlyDifference(int year1, int month1,int year2, int month2)
    {
        //Month 2 is sooner than month 1 always

        if(month1==13)
        {
            month1=1;
            year1++;
        }
        if(year1==year2 && month1==month2)
            return 0;



        return monthToDays(year1,month1)+monthlyDifference( year1, month1+1,year2, month2);
    }

    private int monthToDays(int year, int month)
    {
        Calendar mycal=new GregorianCalendar(year,month,1);
        int days=mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    public int getAccountType() {
        return accountType;
    }

}
