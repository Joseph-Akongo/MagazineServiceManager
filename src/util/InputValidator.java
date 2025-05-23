/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: InputValidator.java
 * Purpose: Provides utility methods to validate user input such as names, emails, and payment details.
 */

package util;

public class InputValidator {

    // Validates name (letters and spaces only, non-null/non-empty)
    public static boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.matches("[a-zA-Z ]+");
    }

    // Validates email using simple regex pattern
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^\\S+@\\S+\\.\\S+$");
    }

    // Validates payment method is either Credit Card or Direct Debit
    public static boolean isValidPaymentMethod(String method) {
        return method != null && (method.equalsIgnoreCase("Credit Card") || method.equalsIgnoreCase("Direct Debit"));
    }

    // Validates credit card details: 16-digit card number, MM/YY expiry, non-empty name
    public static boolean isValidCreditCard(String cardNumber, String expiry, String holderName) {
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) return false;
        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;
        if (holderName == null || holderName.trim().isEmpty()) return false;
        return true;
    }

    // Validates direct debit details: 8-digit account number and 6-digit BSB
    public static boolean isValidDirectDebit(String acc, String bsb) {
        if (acc == null || !acc.matches("\\d{8}")) return false;
        if (bsb == null || !bsb.matches("\\d{6}")) return false;
        return true;
    }
}
