package model;

import java.io.Serializable;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: EnterpriseCustomer.java
 * Purpose: A special type of PayingCustomer that includes a contact person and multiple magazine copies.
 *          Used for organizational subscriptions.
 */

// EnterpriseCustomer a specialized PayingCustomer who orders multiple copies for a company
public class EnterpriseCustomer extends PayingCustomer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private PaymentMethod paymentMethod;

    // Inner static class to represent a point of contact for the enterprise
    public static class ContactPerson implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String contactName;
        private String contactEmail;

        public ContactPerson(String name, String email) {
            this.contactName = name;
            this.contactEmail = email;
        }

        // Return a readable representation of the contact
        public String getContactDetails() {
            return contactName + " (" + contactEmail + ")";
        }
    }

    // Each EnterpriseCustomer has a contact person
    ContactPerson contact;

    // Number of magazine copies to be delivered per week
    private int numberOfCopies;

    // Constructor initializes all enterprise-specific details
    public EnterpriseCustomer(String name, String email, PaymentMethod paymentMethod,
                              ContactPerson contact, int numberOfCopies) {
        super(name, email, paymentMethod);
        this.contact = contact;
        this.numberOfCopies = numberOfCopies;
    }

    public ContactPerson getContact() {
        return contact;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }
    
    @Override
    public String getSpecificInfo() {
        return paymentMethod != null ? paymentMethod.toString() : "";
    }

    @Override
    public void setSpecificInfo(String info) {
        if (info != null && !info.isEmpty()) {
            this.paymentMethod = new PaymentMethod(info);  // Again, adjust if needed
        }
    }
}
