/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: PayingCustomer.java
 * Purpose: Represents a paying customer who may also be responsible for associate customers.
 *          Stores payment method and a list of associated customers.
 */

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// PayingCustomer inherits from Customer and adds associates and a billing method
public class PayingCustomer extends Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Associates linked to this paying customer 
    private List<AssociateCustomer> associates = new ArrayList<>();

    // Payment method used for billing
    private PaymentMethod paymentMethod;

    // Constructor: initializes base customer info and billing method
    public PayingCustomer(String name, String email, PaymentMethod method) {
        super(name, email);
        this.paymentMethod = method;
    }

    // Add a linked associate if not null 
    public void addAssociate(AssociateCustomer ac) {
        if (ac != null) associates.add(ac);
    }

    // Getters
    public List<AssociateCustomer> getAssociates() {
        return associates;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    // Setter for payment method with null check
    public void setPaymentMethod(PaymentMethod method) {
        this.paymentMethod = method;
    }
    
    @Override
    public String getSpecificInfo() {
        return paymentMethod != null ? paymentMethod.toString() : "";
    }
    
    public void removeAssociate(AssociateCustomer ac) {
        if (ac != null) {
            associates.remove(ac);
        }
    }
}
