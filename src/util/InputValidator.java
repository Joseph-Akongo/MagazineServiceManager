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
}
