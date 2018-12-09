package com.abc;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class TransactionTest {
    @Test
    public void transaction() {
        Transaction t = new Transaction(5);
        Date date1=t.getTransactionDate();
        Transaction t2 = new Transaction(5);
        Date date2=  t2.getTransactionDate();
        Date date=new Date();
        date2.setHours(2);

        assertTrue(t instanceof Transaction);
    }
}
