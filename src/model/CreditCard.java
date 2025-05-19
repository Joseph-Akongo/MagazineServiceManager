package model;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: CreditCard.java
 * Purpose: Models a credit card with cardholder name, number, and expiry date.
 *          Used as a payment method in the subscription system.
 */

// CreditCard extends PaymentMethod — using inheritance to specialize payment behavior
import java.io.Serializable;
import java.util.Scanner;

public class CreditCard implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated fields specific to credit card payment
    private String cardNumber;
    private String expiryDate;
    private String cardHolderName;

    // Constants card format rules
    private static final int cardNumberLength = 16;
    private static final int expiryDateLenth = 5; // Format: MM/YY

    // Constructor initialize the credit card's details
    public CreditCard(String cardNumber, String expiryDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cardHolderName = cardHolderName;
    }

    public CreditCard() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Validation logic to check if card data is properly formatted
    // Returns 1 if valid, 0 if invalid — simple but clear
    public int checkCardValidity() {
        if (cardNumber == null || cardNumber.length() != cardNumberLength || !cardNumber.matches("\\d+")) {
            return 0; // Invalid card number
        }
        if (expiryDate == null || expiryDate.length() != expiryDateLenth || !expiryDate.matches("\\d{2}/\\d{2}")) {
            return 0; // Invalid expiry date
        }
        if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
            return 0; // Invalid card holder name
        }
        return 1; // Valid card
    }

    public static CreditCard createFromInput(Scanner scanner) {
        System.out.print("Enter card number: ");
        String number = scanner.nextLine();

        System.out.print("Enter expiry date (MM/YY): ");
        String expiry = scanner.nextLine();

        System.out.print("Enter card holder name: ");
        String holder = scanner.nextLine();

        return new CreditCard(number, expiry, holder);
    }

    // Setters and getters 
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    

    // Override toString() for string representation when displaying the payment method
    @Override
    public String toString() {
        return "Credit Card: " + cardNumber + ", Expiry: " + expiryDate + ", Holder: " + cardHolderName;
    }
}
