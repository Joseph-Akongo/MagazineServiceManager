package service;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: MagazineService.java
 * Purpose: Provides methods for managing customer data and displaying supplements and billing information.
 *          Acts as the core service class in the magazine subscription system.
 */
import model.Customer;
import model.PayingCustomer;
import model.AssociateCustomer;
import model.EnterpriseCustomer;
import model.Supplement;
import model.Magazine;
import java.util.ArrayList;
import java.util.List;
import model.PaymentMethod;

// Controller class.
public class MagazineService {
    private static List<Magazine> magazines = new ArrayList<>();

    // Storage for all customer types (Paying, Associate, Enterprise)
    static List<Customer> customers = new ArrayList<>();
    private static List<Supplement> availableSupplements = new ArrayList<>();
    
    static {
        // Test supplements
        availableSupplements.add(new Supplement("Sports", 2.00));
        availableSupplements.add(new Supplement("Fashion", 1.50));
        availableSupplements.add(new Supplement("Technology", 3.00));
        availableSupplements.add(new Supplement("Health", 2.50));
    }
    
    public static List<Supplement> getAvailableSupplements() {
        return availableSupplements;
    }
    
    public static void setAvailableSupplements(List<Supplement> list) {
        availableSupplements = new ArrayList<>(list);
    }
    
    // Add customer to list
    public static void addCustomer(Customer c) {
        customers.add(c);
    }

    // Remove customer by name
    public static void removeCustomer(String name){
        boolean customerExists = customers.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
        if(customerExists) {
            customers.removeIf(c -> c.getName().equalsIgnoreCase(name));
            System.out.println("Customer " + name + " removed.");
        } else {
            System.out.println("Customer " + name + " not found.");
        }
    }

    // Send weekly emails to all customers for a given number of weeks
    public static void weeklyEmails(int weeks) {
        double magCost = MagazineService.getMagCost();
        if (customers.isEmpty()) {
            System.out.println("No customers to send emails to.");
            return;
        }
    
        for (int i = 1; i <= weeks; i++) {
            System.out.println("\n----------------- Week " + i + " Emails ----------------");
            for (Customer c : customers) {
                System.out.println(EmailService.generateWeeklyEmail(c, magCost));
            }
        }
    }

    // Generate monthly billing summaries for paying customers
    public static void monthlyBills(int month) {
        if (month < 1 || month > 12) {
            System.out.println("------------------------------------------------");
            System.out.println("Invalid month. Please enter a number between 1 and 12.");
            System.out.println("------------------------------------------------");
            return;
        } 

        if (customers.isEmpty()) {
            System.out.println("No customers to bill.");
            return;
        }

        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        String monthName = monthNames[month - 1];
        int currentYear = 2025;

        System.out.println("\n--------- Monthly Billing for: " + monthName + " ----------");

        for (Customer c : customers) {
            // Billing applies only to paying customers
            if (c instanceof PayingCustomer) {
                PayingCustomer pc = (PayingCustomer) c;
                System.out.println("Payment method for " + pc.getName() + ": " + pc.getPaymentMethod());
                System.out.println(BillingService.generateMonthlyBilling(pc, month, currentYear));
            }
        }
    }

    public static void allMonthlyBills() {
        for (int month = 1; month <= 12; month++) {
            monthlyBills(month); // Reuse existing method
        }
    }

    // Clears all customer data when reloading hardcoded data
    public static void clearCustomers() {
        customers.clear();
    }

    // Change the billing method of a specific paying customer
    public static void changeBillingMethod(String name, PaymentMethod method) {
        for (Customer c : customers) {
            if (c instanceof PayingCustomer && c.getName().equalsIgnoreCase(name)) {
                PayingCustomer pc = (PayingCustomer) c;
                System.out.println("Current billing method: " + pc.getPaymentMethod());
                pc.setPaymentMethod(method);
                System.out.println("Billing method updated.");
            }
        }
    }

    // Adds a supplement to a customer's list
    public static void addSupplementToCustomer(String name, Supplement supplement) {
        for (Customer c : customers) {
            if (c.getName().equalsIgnoreCase(name)) {
                c.addSupplement(supplement);
                System.out.println("Supplement added to " + name);
                return;
            }
        }
        System.out.println("Customer not found.");
    }

    // Remove a supplement from a customer's list
    public static void removeSupplementFromCustomer(String name, String supplementName) {
        for (Customer c : customers) {
            if (c.getName().equalsIgnoreCase(name)) {
                boolean removed = c.getSupplements().removeIf(s -> s.getName().equalsIgnoreCase(supplementName));
                if (removed) {
                    System.out.println("Supplement removed.");
                } else {
                    System.out.println("Supplement not found.");
                }
                return;
            }
        }
        System.out.println("Customer not found.");
    }
    
    public static void removeSupplement(Supplement s) {
        availableSupplements.remove(s);
        for (Customer c : customers) {
            c.getSupplements().removeIf(existing -> existing.getName().equalsIgnoreCase(s.getName()));
        }
    }

    // Add an associate customer under a paying customer
    public static void addAssociateCustomer(String name, String email, String payerName) {
        PayingCustomer payer = null;
        for (Customer c : customers) {
            if (c instanceof PayingCustomer && c.getName().equalsIgnoreCase(payerName)) {
                payer = (PayingCustomer) c;
                break;
            }
        }

        if (payer != null) {
            AssociateCustomer associate = new AssociateCustomer(name, email, payer);
            payer.addAssociate(associate);
            customers.add(associate);
            System.out.println("Associate customer added successfully.");
        } else {
            System.out.println("Payer not found. Cannot add associate.");
        }
    }

    // Display a list of all customers
    public static void showAllCustomers() {
        System.out.println("\n----------------- All Customers ----------------");
        for (Customer c : customers) {
            // Avoid printing associates, already printed under their payer
            if (c instanceof AssociateCustomer) continue;

            if (c instanceof EnterpriseCustomer) {
                EnterpriseCustomer ec = (EnterpriseCustomer) c;
                System.out.println("Enterprise Customer: " + ec.getName() + " (" + ec.getEmail() + ")");
                System.out.println("  Contact Person: " + ec.getContact().getContactDetails());
                System.out.println("  Number of Copies: " + ec.getNumberOfCopies());
                System.out.println("  Payment method: " + ec.getPaymentMethod());

            } else if (c instanceof PayingCustomer) {
                PayingCustomer pc = (PayingCustomer) c;
                System.out.println("Paying Customer: " + pc.getName() + " (" + pc.getEmail() + ")");
                System.out.println("  Payment method: " + pc.getPaymentMethod());

                for (AssociateCustomer ac : pc.getAssociates()) {
                    System.out.println("    -> Associate: " + ac.getName() + " (" + ac.getEmail() + ")");
                }
                System.out.println(); // Print a new line for better readability
            }
        }
    }

    // Searches for a customer by name and prints detailed info
    public static void searchCustomer(String searchName) {
        boolean found = false;
        System.out.println("Searching for: " + searchName);
        System.out.println("Customers in system:");
        for (Customer c : customers) {
            System.out.println("- " + c.getName());
        }

        for (Customer c : customers) {
            if (c.getName().trim().equalsIgnoreCase(searchName.trim())) {
                found = true;
                System.out.println("\n-------- Customer Found -------");
                System.out.println("Name: " + c.getName());
                System.out.println("Email: " + c.getEmail());

                if (c instanceof EnterpriseCustomer) {
                    EnterpriseCustomer ec = (EnterpriseCustomer) c;
                    System.out.println("Customer Type: Enterprise Customer");
                    System.out.println("Contact Person: " + ec.getContact().getContactDetails());
                    System.out.println("Payment Method: " + ec.getPaymentMethod()); 
                    System.out.println("Number of Copies: " + ec.getNumberOfCopies());
                    printSupplements(ec.getSupplements());

                } else if (c instanceof PayingCustomer) {
                    PayingCustomer pc = (PayingCustomer) c;
                    System.out.println("Customer Type: Paying Customer");
                    System.out.println("Payment Method: " + pc.getPaymentMethod());
                    printSupplements(pc.getSupplements());

                    if (!pc.getAssociates().isEmpty()) {
                        System.out.println("\nAssociate Customers:");
                        for (AssociateCustomer ac : pc.getAssociates()) {
                            System.out.println("- " + ac.getName() + " (Email: " + ac.getEmail() + ")");
                            printSupplements(ac.getSupplements());
                        }
                    }

                } else if (c instanceof AssociateCustomer) {
                    AssociateCustomer ac = (AssociateCustomer) c;
                    System.out.println("Customer Type: Associate Customer");
                    printSupplements(ac.getSupplements());

                } else {
                    System.out.println("Customer Type: Regular Customer");
                }

                break; // End loop once match is found
            }
        }

        if (!found) {
            System.out.println("Customer \"" + searchName + "\" not found!");
        }
    }

    // This method returns the matched customer for testing purposes
    public static Customer findCustomerByName(String searchName) {
        for (Customer c : customers) {
            if (c.getName().trim().equalsIgnoreCase(searchName.trim())) {
                return c;
            }
        }
        return null;
    }
    
    public static Supplement findSupplementByName(String name) {
        for (Supplement s : availableSupplements) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    // Returns the total number of customers in the system for testing purposes
    public static int getCustomerCount() {
        return customers.size();
    }    

    // Helper method to display supplements for a customer
    private static void printSupplements(List<Supplement> supplements) {
        if (supplements.isEmpty()) {
            System.out.println("Supplements: None");
        } else {
            System.out.println("Supplements:");
            for (Supplement s : supplements) {
                System.out.println("- " + s.getName() + " ($" + s.getWeeklyCost() + "/week)");
            }
        }
    }

    public static List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
    
    public static void addMagazine(Magazine magazine) {
        magazines.add(magazine);
        System.out.println("Magazine added: " + magazine.getName());
    }
    
    public static void setMagazine(Magazine magazine) {
        magazines.clear();                // Replace existing
        magazines.add(magazine);
    }

    public static List<Magazine> getMagazines() {
        return magazines;
    }
    
    public static Magazine getMagazine() {
        return magazines.isEmpty() ? null : magazines.get(0);
    }
    
    public static float getMagCost() {
        Magazine mag = getMagazine();
        return (mag != null) ? mag.getPrice() : 0f;
    }

    public static void clearMagazines() {
        magazines.clear();
    }

}
