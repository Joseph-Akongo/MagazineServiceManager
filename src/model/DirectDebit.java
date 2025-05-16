package model;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: DirectDebit.java
 * Purpose: Represents a direct debit payment option using BSB and account number.
 *          Used as a payment method in the subscription system.
 */

import java.io.Serializable;
import java.util.Scanner;

public class DirectDebit implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Private fields for account number and BSB
    private int accountNumber;
    private int bsb;

    // Constructor validates input and assigns values
    public DirectDebit(int accountNumber, int bsb) {
        // Validate both fields; if either is invalid, throw an exception 
        if (checkAccountNumber(accountNumber) == -1 || checkBsb(bsb) == -1) {
            throw new IllegalArgumentException("Invalid account number or BSB.");
        }
        this.accountNumber = accountNumber;
        this.bsb = bsb;
    }

    // Validation method for account number (1 to 8 digits)
    public int checkAccountNumber(int accountNumber) {
        if (accountNumber <= 0 || accountNumber > 99999999) {
            System.out.println("Invalid account number. Please ensure it is a positive integer with up to 8 digits.");
            return -1;
        }
        return accountNumber;
    }

    // Validation method for BSB (1 to 6 digits)
    public int checkBsb(int bsb) {
        if (bsb <= 0 || bsb > 999999) {
            System.out.println("Invalid BSB number. Please ensure it is a positive integer with up to 6 digits.");
            return -1;
        }
        return bsb;
    }

    public static DirectDebit createFromInput(Scanner scanner) {
        try {
            System.out.print("Enter account number: ");
            int accountNumber = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter BSB: ");
            int bsb = Integer.parseInt(scanner.nextLine());

            return new DirectDebit(accountNumber, bsb);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Account number and BSB must be numeric.");
        }
    }

    // Getter for account number
    public int getAccountNumber() {
        return accountNumber;
    }

    // Getter for BSB
    public int getBsb() {
        return bsb;
    }

    // toString method gives a readable output of the account details
    @Override
    public String toString() {
        return "Direct Debit: Account Number: " + accountNumber + ", BSB: " + bsb;
    }
}
