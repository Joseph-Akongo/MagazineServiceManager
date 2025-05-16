package model;

import java.io.Serializable;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: PaymentMethod.java
 * Purpose: A wrapper class for either a CreditCard or DirectDebit method used by a customer.
 *          Ensures uniform handling of different payment types.
 */

// Wraper for payment methods Credit Card / Direct Debit and ensures only one type is used at a time.
public class PaymentMethod implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String method;
    private CreditCard card;
    private DirectDebit debit;

    // Constructor for just the method type 
    public PaymentMethod(String method) {
        this.method = method;
    }

    // Constructor for credit card
    public PaymentMethod(CreditCard card) {
        this.method = "Credit Card";
        this.card = card;
    }

    // Constructor for direct debit
    public PaymentMethod(DirectDebit debit) {
        this.method = "Direct Debit";
        this.debit = debit;
    }

    // Factory method from string input (from user prompt)
    public static PaymentMethod fromString(String methodInput) {
        switch (methodInput.toLowerCase()) {
            case "credit card":
                return new PaymentMethod("Credit Card");
            case "direct debit":
                return new PaymentMethod("Direct Debit");
            default:
                throw new IllegalArgumentException("Invalid payment method: " + methodInput);
        }
    }

    // Getters and setters 
    public String getMethod() {
        return method;
    }

    public CreditCard getCard() {
        return card;
    }

    public DirectDebit getDebit() {
        return debit;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    // Display readable string depending on type
    @Override
    public String toString() {
        if (card != null) {
            return card.toString();
        } else if (debit != null) {
            return debit.toString();
        } else {
            return method;
        }
    }
}
