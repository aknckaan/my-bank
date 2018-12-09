package com.abc;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CustomerTest {
    private static final double DOUBLE_DELTA = 1e-4;
    @Test //Test customer statement generation
    public void testApp(){

        Account checkingAccount = new Account(Account.CHECKING);
        Account savingsAccount = new Account(Account.SAVINGS);

        Customer henry = new Customer("Henry").openAccount(checkingAccount).openAccount(savingsAccount);

        checkingAccount.deposit(100.0);
        savingsAccount.deposit(4000.0);
        savingsAccount.withdraw(200.0);

        assertEquals("Statement for Henry\n" +
                "\n" +
                "Checking Account\n" +
                "  deposit $100.00\n" +
                "Total $100.00\n" +
                "\n" +
                "Savings Account\n" +
                "  deposit $4,000.00\n" +
                "  withdrawal $200.00\n" +
                "Total $3,800.00\n" +
                "\n" +
                "Total In All Accounts $3,900.00", henry.getStatement());

        // Run the same app with the new method of adding account

        henry = new Customer("Henry");
        henry.openAccount(new int[] {Account.CHECKING, Account.SAVINGS});
        int[] acc_nums=henry.getAccountNumbers();
        HashMap<Integer,Double> hm = new HashMap<Integer,Double>();
        hm.put(acc_nums[0],100.0);
        hm.put(acc_nums[1],4000.0);

        henry.deposit(hm);
        henry.withdraw(acc_nums[1],200.0);
        assertEquals("Statement for Henry\n" +
                "\n" +
                "Checking Account\n" +
                "  deposit $100.00\n" +
                "Total $100.00\n" +
                "\n" +
                "Savings Account\n" +
                "  deposit $4,000.00\n" +
                "  withdrawal $200.00\n" +
                "Total $3,800.00\n" +
                "\n" +
                "Total In All Accounts $3,900.00", henry.getStatement());

    }

    @Test
    public void maxiSavingsTest()
    {
        double balance=0.0;

        Customer henry = new Customer("Henry");
        henry.openAccount(new int[] {Account.MAXI_SAVINGS});
        Transaction t = new Transaction(1000);
        t.setDate(2018,01,01,00,00,00);
        // 9 day later money withdrawal
        Transaction t2 = new Transaction(-1000);
        t2.setDate(2018,01,10,00,00,00);
        henry.insertTransactionToAccount(t,henry.getAccountNumbers()[0]);
        henry.insertTransactionToAccount(t2,henry.getAccountNumbers()[0]);
        double expected=1000+(50.0*(9.0/365.0)) -1000;
        assertEquals(expected-balance,henry.totalInterestEarned(),DOUBLE_DELTA);

        Transaction t3 = new Transaction(500);
        t3.setDate(2018,01,11,00,00,00);
        henry.insertTransactionToAccount(t3,henry.getAccountNumbers()[0]);
        expected+=(expected*0.1/100)/365 + 500;
        // 1000 *5% -1000 + 500*0.001% = 550.5
        balance-=500;
        assertEquals(expected+balance,henry.totalInterestEarned(),DOUBLE_DELTA);

        Transaction t4 = new Transaction(-10);
        t4.setDate(2018,01,12,02,00,00);
        Transaction t5 = new Transaction(1000);
        t5.setDate(2018,01,22,01,50,00);
        // money deposit 9 days and 23.50 hours after the last withdrawal. Should have 0.1 % interest
        balance-=-10+1000;
        henry.insertTransactionToAccount(t4,henry.getAccountNumbers()[0]);
        henry.insertTransactionToAccount(t5,henry.getAccountNumbers()[0]);
        expected+=(expected*0.1/100)/365 -10;
        expected+=(expected*0.1/100)/365*10+1000;
        assertEquals(expected+balance,henry.totalInterestEarned(),DOUBLE_DELTA);

        Transaction t6 = new Transaction(1000);
        t6.setDate(2018,01,22,02,00,01);
        henry.insertTransactionToAccount(t6,henry.getAccountNumbers()[0]);
        // 1 money deposit 1 second and 10 days after the last withdrawal
        Transaction t7 = new Transaction(1000);
        t7.setDate(2018,03,22,02,00,01);
        balance-=1000+1000;
        henry.insertTransactionToAccount(t7,henry.getAccountNumbers()[0]);
        expected+=1000;
        expected+=(expected*5/100)/365*59+1000;

        assertEquals(expected+balance,henry.totalInterestEarned(),0.1);

    }

    //Test depositing to an account that doesn't exits
    @Test(expected= IllegalArgumentException.class)
    public void unknownAccountTest() {
        Customer henry = new Customer("Henry");
        henry.openAccount(new int[] {Account.CHECKING, Account.SAVINGS});
        HashMap<Integer,Double> hm = new HashMap<Integer,Double>();
        hm.put(10,4000.0);
        henry.deposit(hm);
    }

    //Test oppening an account type that doesn't exist
    @Test(expected= IllegalArgumentException.class)
    public void unvalidAccountTest() {
        Customer henry = new Customer("Henry");
        henry.openAccount(new int[] {Account.CHECKING,10});

    }

    @Test
    public void depositBetweenAcounts() {
        Customer henry = new Customer("Henry");
        henry.openAccount(new int[] {Account.CHECKING, Account.MAXI_SAVINGS});
        int[] nums=henry.getAccountNumbers();
        HashMap<Integer,Double> hm = new HashMap<Integer,Double>();
        hm.put(nums[0],4000.0);
        hm.put(nums[1],1.0);
        henry.deposit(hm);
        henry.depositBetween(nums[0],nums[1],1000.0);

        assertEquals("Statement for Henry\n" +
                "\n" +
                "Checking Account\n" +
                "  deposit $4,000.00\n" +
                "  transacted to account: "+nums[1]+", amount: $1,000.00\n"+
                "Total $3,000.00\n" +
                "\n" +
                "Maxi Savings Account\n" +
                "  deposit $1.00\n" +
                "  transaction received from account: "+nums[0]+", amount: $1,000.00\n" +
                "Total $1,001.00\n" +
                "\n" +
                "Total In All Accounts $4,001.00", henry.getStatement());


    }

    // Wrong account number doesnt exist
    @Test(expected= IllegalArgumentException.class)
    public void depositBetweenAcountsError() {
        Customer henry = new Customer("Henry");
        henry.openAccount(new int[] {Account.CHECKING, Account.MAXI_SAVINGS});
        int[] nums=henry.getAccountNumbers();
        HashMap<Integer,Double> hm = new HashMap<Integer,Double>();
        hm.put(nums[0],4000.0);
        hm.put(nums[1],1.0);
        henry.deposit(hm);
        henry.depositBetween(1000,nums[1],1000.0);


    }
    // Wrong account doesnt belong to the customer
    @Test(expected= IllegalArgumentException.class)
    public void depositBetweenAcountsError2() {
        Customer henry = new Customer("Henry");
        Customer ellie = new Customer("ellie");
        henry.openAccount(new int[] {Account.CHECKING, Account.MAXI_SAVINGS});
        ellie.openAccount(new int[] {Account.CHECKING, Account.MAXI_SAVINGS});
        int[] nums=henry.getAccountNumbers();
        int[] nums2=ellie.getAccountNumbers();
        HashMap<Integer,Double> hm = new HashMap<Integer,Double>();
        hm.put(nums[0],4000.0);
        hm.put(nums[1],1.0);
        henry.deposit(hm);
        henry.depositBetween(nums[0],nums2[0],1000.0);
    }


    @Test
    public void testOneAccount(){
        Customer oscar = new Customer("Oscar").openAccount(new Account(Account.SAVINGS));
        assertEquals(1, oscar.getNumberOfAccounts());
    }

    @Test
    public void testTwoAccount(){
        Customer oscar = new Customer("Oscar")
                .openAccount(new Account(Account.SAVINGS));
        oscar.openAccount(new Account(Account.CHECKING));
        assertEquals(2, oscar.getNumberOfAccounts());
    }

    @Test
    public void testThreeAcounts() {
        Customer oscar = new Customer("Oscar")
                .openAccount(new Account(Account.SAVINGS));
        oscar.openAccount(new Account(Account.CHECKING));
        oscar.openAccount(new Account(Account.MAXI_SAVINGS));
        assertEquals(3, oscar.getNumberOfAccounts());
    }

    @Test
    public void testNewAccountMethods(){
        Customer oscar = new Customer("Oscar");// Test new method on each account type
        oscar.openAccount(new int[] {Account.SAVINGS});
        assertEquals(1, oscar.getNumberOfAccounts());


        oscar.openAccount(new int[] {Account.MAXI_SAVINGS});
        assertEquals(2, oscar.getNumberOfAccounts());


        oscar.openAccount(new int[] {Account.CHECKING});
        assertEquals(3, oscar.getNumberOfAccounts());


        oscar = new Customer("Oscar"); // Try with old method

        assertEquals(0, oscar.getNumberOfAccounts());


        oscar.openAccount(new Account(Account.SAVINGS)); // Test compatibility of old implemented method

        assertEquals(1, oscar.getNumberOfAccounts());


        oscar.openAccount(new int[] {Account.MAXI_SAVINGS});
        assertEquals(2, oscar.getNumberOfAccounts());


        // Check customer numbers
        Customer c1 = new Customer("c1");
        assertNotEquals(c1.cus_number,oscar);


    }

}
