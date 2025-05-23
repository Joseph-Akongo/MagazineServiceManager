/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: EmailService.java
 * Purpose: Generates weekly billing email messages for different customer types.
 */

package service;

import java.io.Serializable;
import model.AssociateCustomer;
import model.Customer;
import model.EnterpriseCustomer;
import model.PayingCustomer;
import model.Supplement;

public class EmailService implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Generates a weekly billing summary email based on customer type
    public static String generateWeeklyEmail(Customer customer, double magCost) {
        StringBuilder email = new StringBuilder("Weekly Billing Summary for " + customer.getName() + "\n");

        // Base cost
        double total = magCost;
        email.append("Magazine: $" + magCost + "\n");

        // Supplements
        if (!customer.getSupplements().isEmpty()) {
            email.append("Supplements:\n");
            for (Supplement s : customer.getSupplements()) {
                email.append(" - ").append(s.getName()).append(": $").append(s.getWeeklyCost()).append("\n");
                total += s.getWeeklyCost();
            }
        }

        // Enterprise: multiply total by number of copies
        if (customer instanceof EnterpriseCustomer ec) {
            email.append("Copies: ").append(ec.getNumberOfCopies()).append("\n");
            email.append("Per Copy: $").append(total).append("\n");
            total *= ec.getNumberOfCopies();
            return generateEnterpriseWeeklyEmail(ec, magCost);
        }

        email.append("Total Weekly Charge: $").append(String.format("%.2f", total)).append("\n");

        return email.toString();
    }
    
    // Generates a basic billing email for standard (non-enterprise) customers
    public static String generateStandardWeeklyEmail(Customer customer, double magazineCost) {
        StringBuilder sb = new StringBuilder("Dear " + customer.getName() + "\n");

        sb.append("Weekly Magazine Cost: $" + String.format("%.2f", magazineCost) + "\n");

        double total = magazineCost;
        sb.append("Supplements:\n");

        if (customer.getSupplements().isEmpty()) {
            sb.append("- None\n");
        } else {
            for (Supplement s : customer.getSupplements()) {
                sb.append("- " + s.getName() + " ($" + s.getWeeklyCost() + ")\n");
                total += s.getWeeklyCost();
            }
        }

        sb.append("Total Weekly Cost: $" + String.format("%.2f", total) + "\n");
        return sb.toString();
    }

    // Builds a weekly billing email specifically for EnterpriseCustomers
    private static String generateEnterpriseWeeklyEmail(EnterpriseCustomer ec, double magazineCost) {
        StringBuilder sb = new StringBuilder("Dear " + ec.getName() + "\n");

        sb.append("Weekly Magazine Base Cost: $" + String.format("%.2f", magazineCost) + "\n");
        sb.append("Number of Copies: " + ec.getNumberOfCopies() + "\n");

        double supplementCost = 0;
        sb.append("Supplements:\n");
        if (ec.getSupplements().isEmpty()) {
            sb.append("- None\n");
        } else {
            for (Supplement s : ec.getSupplements()) {
                sb.append("- " + s.getName() + " ($" + s.getWeeklyCost() + ")\n");
                supplementCost += s.getWeeklyCost();
            }
        }

        double totalPerCopy = magazineCost + supplementCost;
        double totalCost = totalPerCopy * ec.getNumberOfCopies();

        sb.append("Total Cost per Copy: $" + String.format("%.2f", totalPerCopy) + "\n");
        sb.append("Total Weekly Cost: $" + String.format("%.2f", totalCost) + "\n");

        return sb.toString();
    }
}
