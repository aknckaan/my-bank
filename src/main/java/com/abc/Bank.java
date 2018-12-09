package com.abc;

import java.util.HashMap;

public class Bank {
    //private List<Customer> customers;
    private HashMap<Integer,Customer> customers;
    public Bank() {
        customers = new HashMap<Integer, Customer>();
    }

    public void addCustomer(Customer customer) {

        if (customers.containsKey(customer.cus_number))
            throw new IllegalArgumentException("This customer already exists!");
        customers.put(customer.cus_number,customer);
    }

    public String customerSummary() {
        String summary = "Customer Summary";

        for (Customer c : customers.values())
            summary += "\n - " + c.getName() + " (" + format(c.getNumberOfAccounts(), "account") + ")";
        return summary;
    }

    //Make sure correct plural of word is created based on the number passed in:
    //If number passed in is 1 just return the word otherwise add an 's' at the end
    private String format(int number, String word) {
        return number + " " + (number == 1 ? word : word + "s");
    }

    public double totalInterestPaid() {
        double total = 0;
        for(Customer c: customers.values())
            total += c.totalInterestEarned();
        return total;
    }

    public String getFirstCustomer() {
        try {
            customers = null;
            return customers.get(0).getName();
        } catch (Exception e){
            e.printStackTrace();
            return "Error";
        }
    }
}
