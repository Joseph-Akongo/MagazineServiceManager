/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: BillingService.java
 * Purpose: Provides billing summaries (weekly/monthly) for all customer types.
 */

package service;

import model.*;
import java.time.YearMonth;

public class BillingService {

    // Builds a weekly billing summary for a customer
    public static String generateWeeklyBilling(Customer customer) {
        float magCost = MagazineService.getMagCost();
        StringBuilder summary = new StringBuilder();

        if (customer instanceof PayingCustomer pc) {
            float total = magCost;
            summary.append("Magazine Cost: $").append(String.format("%.2f", magCost)).append("\n");

            for (Supplement s : pc.getSupplements()) {
                summary.append("Supplement - ").append(s.getName())
                       .append(": $").append(String.format("%.2f", s.getWeeklyCost())).append("\n");
                total += s.getWeeklyCost();
            }

            summary.append("- You: $").append(String.format("%.2f", total)).append("\n");

            for (AssociateCustomer ac : pc.getAssociates()) {
                float acTotal = 0;
                for (Supplement s : ac.getSupplements()) {
                    summary.append("  ").append(ac.getName()).append(" Supplement - ").append(s.getName())
                           .append(": $").append(String.format("%.2f", s.getWeeklyCost())).append("\n");
                    acTotal += s.getWeeklyCost();
                }
                summary.append("- ").append(ac.getName()).append(" (Supplements Only): $")
                       .append(String.format("%.2f", acTotal)).append("\n");
            }

        } else if (customer instanceof EnterpriseCustomer ec) {
            int copies = ec.getNumberOfCopies();
            float supplementTotal = 0;

            summary.append("Magazine Cost: $").append(String.format("%.2f", magCost)).append("\n");
            summary.append("Number of copies: ").append(copies).append("\n");

            for (Supplement s : ec.getSupplements()) {
                summary.append("Supplement - ").append(s.getName())
                       .append(": $").append(String.format("%.2f", s.getWeeklyCost())).append("\n");
                supplementTotal += s.getWeeklyCost();
            }

            float total = (magCost + supplementTotal) * copies;
            summary.append("Total Weekly Cost: $").append(String.format("%.2f", total)).append("\n");

        } else {
            float total = magCost;
            summary.append("Magazine Cost: $").append(String.format("%.2f", magCost)).append("\n");

            for (Supplement s : customer.getSupplements()) {
                summary.append("Supplement - ").append(s.getName())
                       .append(": $").append(String.format("%.2f", s.getWeeklyCost())).append("\n");
                total += s.getWeeklyCost();
            }

            summary.append("- ").append(customer.getName()).append(": $").append(String.format("%.2f", total)).append("\n");
        }

        return summary.toString();
    }

    // Builds a monthly billing summary for a customer
    public static String generateMonthlyBilling(Customer customer, int month, int year) {
        float magCost = MagazineService.getMagCost();
        int weeks = (int) Math.ceil(getDaysInMonth(month, year) / 7.0);

        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        StringBuilder bill = new StringBuilder("Monthly Billing for ")
            .append(customer.getName()).append(" (").append(monthNames[month - 1])
            .append(") ").append(year).append("\n");

        double total = 0;

        if (customer instanceof PayingCustomer pc) {
            double custTotal = magCost * weeks;
            bill.append("Magazine Cost: $").append(String.format("%.2f", magCost)).append("\n");

            for (Supplement s : pc.getSupplements()) {
                bill.append("Supplement - ").append(s.getName())
                    .append(": $").append(String.format("%.2f", s.getWeeklyCost() * weeks)).append("\n");
                custTotal += s.getWeeklyCost() * weeks;
            }

            bill.append("- You: $").append(String.format("%.2f", custTotal)).append("\n");
            total += custTotal;

            for (AssociateCustomer ac : pc.getAssociates()) {
                double acTotal = 0;
                for (Supplement s : ac.getSupplements()) {
                    bill.append("  ").append(ac.getName()).append(" Supplement - ").append(s.getName())
                        .append(": $").append(String.format("%.2f", s.getWeeklyCost() * weeks)).append("\n");
                    acTotal += s.getWeeklyCost() * weeks;
                }
                bill.append("- ").append(ac.getName()).append(": $").append(String.format("%.2f", acTotal)).append("\n");
                total += acTotal;
            }

        } else if (customer instanceof EnterpriseCustomer ec) {
            int copies = ec.getNumberOfCopies();
            double supplementTotal = ec.getSupplements().stream().mapToDouble(Supplement::getWeeklyCost).sum();

            bill.append("Magazine Cost: $").append(String.format("%.2f", magCost)).append("\n");
            bill.append("Number of copies: ").append(copies).append("\n");

            for (Supplement s : ec.getSupplements()) {
                bill.append("Supplement - ").append(s.getName())
                    .append(": $").append(String.format("%.2f", s.getWeeklyCost())).append("\n");
            }

            double custTotal = (magCost + supplementTotal) * copies * weeks;
            bill.append("- ").append(ec.getName()).append(": $").append(String.format("%.2f", custTotal)).append("\n");
            total = custTotal;

        } else {
            double custTotal = magCost * weeks;
            bill.append("Magazine Cost: $").append(String.format("%.2f", magCost)).append("\n");

            for (Supplement s : customer.getSupplements()) {
                bill.append("Supplement - ").append(s.getName())
                    .append(": $").append(String.format("%.2f", s.getWeeklyCost() * weeks)).append("\n");
                custTotal += s.getWeeklyCost() * weeks;
            }

            bill.append("- ").append(customer.getName()).append(": $").append(String.format("%.2f", custTotal)).append("\n");
            total = custTotal;
        }

        bill.append("Total Monthly Charges: $").append(String.format("%.2f", total)).append("\n");
        return bill.toString();
    }

    // Calculates total weekly charge for one customer
    public static float getWeeklyCharge(Customer customer) {
        float total = MagazineService.getMagCost();
        for (Supplement s : customer.getSupplements()) {
            total += s.getWeeklyCost();
        }
        if (customer instanceof EnterpriseCustomer ec) {
            total *= ec.getNumberOfCopies();
        }
        return total;
    }

    // Returns number of days in the given month
    private static int getDaysInMonth(int month, int year) {
        return YearMonth.of(year, month).lengthOfMonth();
    }
}
