package service;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: BillingService.java
 * Purpose: Responsible for generating monthly billing summaries for paying customers and their associates.
 *          Includes utility for calculating the number of weeks in a given month.
 */
import model.AssociateCustomer;
import model.Customer;
import model.EnterpriseCustomer;
import model.PayingCustomer;
import model.Supplement;

// BillingService single responsibility for generating billing summaries
public class BillingService {

    // Generates monthly bill for a PayingCustomer and their associates
    public static String generateMonthlyBilling(PayingCustomer customer, double magCost, int month, int year) {
        // Month names for display
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        // How many weeks are in the given month
        int daysInMonth = getDaysInMonth(month, year);
        int weeks = (int) Math.ceil(daysInMonth / 7.0);

        // Start constructing the bill
        StringBuilder bill = new StringBuilder("Monthly Billing for " + customer.getName() + 
                " (" + monthNames[month - 1] + ") " + year + "\n");

        // Base cost for the paying customer
        double total = magCost * weeks;

        // Add supplement costs
        for (Supplement s : customer.getSupplements())
            total += s.getWeeklyCost() * weeks;

        // Add line for the paying customer
        bill.append("- You: $" + total + "\n");

        // Loop associate customers and calculate individual charges
        for (AssociateCustomer ac : customer.getAssociates()) {
            double acTotal = magCost * weeks;
            for (Supplement s : ac.getSupplements())
                acTotal += s.getWeeklyCost() * weeks;

            // Add line for associate
            bill.append("- " + ac.getName() + ": $" + acTotal + "\n");

            // Add to total charge
            total += acTotal;
        }

        // Add total for the entire account
        bill.append("Total Monthly Charges: $" + total + "\n");

        return bill.toString(); // Return final bill
    }

    // Utility method to determine how many days are in a given month (handles leap years)
    private static int getDaysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                // Leap year logic
                if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
                    return 29;
                else
                    return 28;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }
    
    public static float getWeeklyCharge(Customer customer) {
        float total = 0;

        // Base magazine cost
        if (MagazineService.getMagazine() != null) {
            total += MagazineService.getMagazine().getPrice();
        }

        // Add supplement costs
        for (Supplement s : customer.getSupplements()) {
            total += s.getWeeklyCost();
        }

        // If Enterprise, multiply by number of copies
        if (customer instanceof EnterpriseCustomer ec) {
            total *= ec.getNumberOfCopies();
        }

        return total;
    }

    public static float getMonthlyCharge(Customer customer) {
        return getWeeklyCharge(customer) * 4;  // Simplified monthly calculation
    }
}
