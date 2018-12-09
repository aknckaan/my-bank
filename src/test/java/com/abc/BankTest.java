package com.abc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BankTest {
    private static final double DOUBLE_DELTA = 1e-15;

    @Test
    public void customerSummary() {
        Bank bank = new Bank();
        Customer john = new Customer("John");
        john.openAccount(new Account(Account.CHECKING));
        bank.addCustomer(john);

        assertEquals("Customer Summary\n - John (1 account)", bank.customerSummary());
        john.openAccount(new Account(Account.MAXI_SAVINGS));
        assertEquals("Customer Summary\n - John (2 accounts)", bank.customerSummary());

        Customer wick = new Customer("Wick");
        wick.openAccount(new Account(Account.CHECKING));
        bank.addCustomer(wick);

        assertEquals("Customer Summary\n - John (2 accounts)\n - Wick (1 account)", bank.customerSummary());

        Customer laura = new Customer("Laura");
        bank.addCustomer(laura);
        assertEquals("Customer Summary\n - John (2 accounts)\n - Wick (1 account)\n - Laura (0 accounts)", bank.customerSummary());


        //
        Bank bank2 = new Bank();
        bank2.addCustomer(laura);
        laura.openAccount(new Account(Account.CHECKING));
        assertEquals("Customer Summary\n - John (2 accounts)\n - Wick (1 account)\n - Laura (1 account)", bank.customerSummary());
        assertEquals("Customer Summary\n - Laura (1 account)", bank2.customerSummary());
    }
    @Test(expected= IllegalArgumentException.class)
    public void customerSummaryDuplicate() {
        Bank bank = new Bank();
        Customer john = new Customer("John");
        john.openAccount(new Account(Account.CHECKING));
        bank.addCustomer(john);
        bank.addCustomer(john);
    }


    @Test
    public void checkingAccount() {
        Bank bank = new Bank();
        Account checkingAccount = new Account(Account.CHECKING);
        Customer bill = new Customer("Bill").openAccount(checkingAccount);
        bank.addCustomer(bill);

        checkingAccount.deposit(100.0);

        assertEquals(0.1, bank.totalInterestPaid(), DOUBLE_DELTA);
    }

    @Test
    public void savings_account() {
        Bank bank = new Bank();
        Account checkingAccount = new Account(Account.SAVINGS);
        bank.addCustomer(new Customer("Bill").openAccount(checkingAccount));

        checkingAccount.deposit(1500.0);

        assertEquals(2.0, bank.totalInterestPaid(), DOUBLE_DELTA);
    }

    @Test
    public void maxi_savings_account() {
        Bank bank = new Bank();
        Account checkingAccount = new Account(Account.MAXI_SAVINGS);
        Customer c= new Customer("Bill");
        bank.addCustomer(c.openAccount(checkingAccount));
        Transaction t3 = new Transaction(3000);
        Transaction t4 = new Transaction(3000);
        t3.setDate(2018,01,11,00,00,00);
        t4.setDate(2019,01,11,00,00,00);
        int[] acc=c.getAccountNumbers();
        c.insertTransactionToAccount(t3,acc[0]);
        c.insertTransactionToAccount(t4,acc[0]);

        assertEquals(3000.0*5/100, bank.totalInterestPaid(), DOUBLE_DELTA);
        c.openAccount(new Account(Account.SAVINGS));
        acc=c.getAccountNumbers();
        c.deposit(acc[1],2000);
        assertEquals(3000.0*5/100+1+(2000-1000)*0.002, bank.totalInterestPaid(), DOUBLE_DELTA);
    }

}
