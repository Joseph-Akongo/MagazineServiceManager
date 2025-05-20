package util;

public class InputValidator {

    public static boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.matches("[a-zA-Z ]+");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^\\S+@\\S+\\.\\S+$");
    }

    public static boolean isValidPaymentMethod(String method) {
        return method != null && (method.equalsIgnoreCase("Credit Card")
                || method.equalsIgnoreCase("Direct Debit"));
    }
    
    public static boolean isValidCreditCard(String cardNumber, String expiry, String holderName) {
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) return false;

        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) return false;

        if (holderName == null || holderName.trim().isEmpty()) return false;

        return true;
    }
    
    public static boolean isValidDirectDebit(String acc, String bsb) {
        if (acc == null || !acc.matches("\\d{8}")) return false;  // 8 digits
        if (bsb == null || !bsb.matches("\\d{6}")) return false;  // 6 digits
        return true;
    }
}
