package service;

import java.io.Serializable;
import model.Customer;
import model.EnterpriseCustomer;
import model.Supplement;

public class EmailService implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static String generateWeeklyEmail(Customer customer, double magazineCost) {
        if (customer instanceof EnterpriseCustomer) {
            EnterpriseCustomer ec = (EnterpriseCustomer) customer;
            return generateEnterpriseWeeklyEmail(ec, magazineCost);
        } else {
            return generateStandardWeeklyEmail(customer, magazineCost);
        }
    }

    private static String generateStandardWeeklyEmail(Customer customer, double magazineCost) {
        StringBuilder sb = new StringBuilder("Dear " + customer.getName() + "\n");
        double total = magazineCost;
        sb.append("Weekly Magazine: $" + magazineCost + "\n");
        sb.append("Supplements:\n");
        for (Supplement s : customer.getSupplements()) {
            sb.append("- " + s.getName() + " ($" + s.getWeeklyCost() + ")\n");
            total += s.getWeeklyCost();
        }
        sb.append("Total Weekly Cost: $" + total + "\n");
        return sb.toString();
    }

    private static String generateEnterpriseWeeklyEmail(EnterpriseCustomer ec, double magazineCost) {
        double supplementCost = 0;
        for (Supplement s : ec.getSupplements()) {
            supplementCost += s.getWeeklyCost();
        }
        double totalCost = (magazineCost + supplementCost) * ec.getNumberOfCopies();
        StringBuilder sb = new StringBuilder("Dear " + ec.getName() + "\n");
        sb.append("Weekly Magazine: $" + magazineCost + "\n");
        sb.append("Supplements:\n");
        for (Supplement s : ec.getSupplements()) {
            sb.append("- " + s.getName() + " ($" + s.getWeeklyCost() + ")\n");
        }
        sb.append("Number of Copies: " + ec.getNumberOfCopies() + "\n");
        sb.append("Total Weekly Cost: $" + String.format("%.2f", totalCost) + "\n");
        return sb.toString();
    }
}
