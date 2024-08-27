
package coe528_project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author alifa
 */


/**
 * Overview:
 * Represents a customer of the bank.
 * Responsibilities include managing customer's account balance and level.
 * This class is mutable.
 * 
 * Abstraction Function:
 * AF(c) = Customer, c, which has a username, password, balance, 
 *         and account level. Respectively, c.username, c.password,
 *         c.balance, and c.accountLevel
 * 
 * Rep Invariant:
 * False if: c.username == null || c.password == null || c == null ||
 *           c.balance < 0 || c.accountLevel == null
 *
 */

// rep
public class Customer extends Person implements AccountLevel{
    private double balance;
    private AccountLevel accountLevel;
    
    /**
     * Constructs a customer with the given username, password, and initial balance.
     * 
     * @param username The username of the customer.
     * @param password The password of the customer.
     * @param balance The initial balance of the customer.
     * 
     * Effects: Initializes a customer object with the provided username, password, and balance.
     *          Updates accountLevel with current account level
     * Requires: The initial balance must be greater than or equal to 100.
     */
    public Customer(String username, String password, double balance){
        super(username, password, "Customer");
        if (balance >= 100){
            this.balance = balance;
        }
        this.accountLevel = calculateLevel();
    }
    
    /**
     * Retrieves the current balance of the customer.
     * 
     * @return The current balance of the customer.
     * 
     * Effects: Retrieves the current balance of the customer.
     */
    public double getBalance(){
        return this.balance;
    }
    
    /**
     * Sets the balance of the customer to the specified amount.
     * 
     * @param amount The amount to set the balance to.
     * 
     * Modifies: The balance of the customer.
     * Effects: Sets the balance of the customer to the specified amount.
     * Requires: The specified amount must not be negative.
     */
    public void setBalance(double amount){
        if (amount > 0){
            this.balance = amount;
        }
    }
    
    /**
     * Retrieves the account level of the customer.
     * 
     * @return The account level of the customer.
     * 
     * Effects: Retrieves the account level of the customer.
     */
    public AccountLevel getLevel() {
        return this.accountLevel;
    }
    
    /**
     * Calculates and returns the account level of the customer based on their balance.
     * 
     * @return The account level of the customer.
     * 
     * Effects: Calculates and returns the account level of the customer based on their balance.
     */
    public AccountLevel calculateLevel() {
        if (this.balance < 10000) {
            return new SilverLevelCustomer();
        } else if (this.balance < 20000) {
            return new GoldLevelCustomer();
        } else {
            return new PlatinumLevelCustomer();
        }
    }
    
    /**
     * Withdraws the specified amount from the customer's balance.
     * 
     * @param amount The amount to withdraw.
     * 
     * Modifies: The balance of the customer.
     * Effects: Withdraws the specified amount from the customer's balance.
     *          Updates customer file with new balance
     * Requires: The specified amount must not exceed the current balance.
     */
    public void withdraw(double amount){
        
        this.setBalance((this.balance - amount));
        
        this.accountLevel = calculateLevel();
        
        String username = this.getUsername();
        String folderPath = "customerDirectory/";
        String filename = folderPath + File.separator + username + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username: " + this.getUsername());
            writer.println("Password: " + this.getPassword());
            writer.println("Balance: " + this.getBalance());
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }
    
    /**
     * Calculates the fee for a given purchase amount based on the customer's account level.
     * 
     * @param amount The purchase amount.
     * @return The fee for the purchase.
     * 
     * Effects: Calculates and returns the fee for a given purchase amount based on the customer's account level.
     */
    public double calculateFee(double amount){
        return this.getLevel().calculateFee(amount);
    }

    
    /**
     * Deposits the specified amount into the customer's balance.
     * 
     * @param amount The amount to deposit.
     * 
     * Modifies: The balance of the customer.
     * Effects: Deposits the specified amount into the customer's balance.
     *          Updates customer file with new balance
     * Requires: The specified amount must be greater than 0.
     */
    public void deposit(double amount){
        
        if (amount > 0){
            this.setBalance(this.getBalance() + amount);
        }
        
        this.accountLevel = calculateLevel();
        
        String username = this.getUsername();
        String folderPath = "customerDirectory/";
        String filename = folderPath + File.separator + username + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username: " + this.getUsername());
            writer.println("Password: " + this.getPassword());
            writer.println("Balance: " + this.getBalance());
        } catch (IOException e) {
            System.out.println("ERROR");
        }
    }
}