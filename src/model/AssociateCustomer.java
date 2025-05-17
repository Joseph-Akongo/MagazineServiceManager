package model;

import java.io.Serializable;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: AssociateCustomer.java
 * Purpose: Represents a customer whose subscription is paid by a PayingCustomer.
 *          Inherits from Customer and maintains a reference to their payer.
 */

// AssociateCustomer is a specialized type of Customer functionality from the base class
public class AssociateCustomer extends Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    // The PayingCustomer who is responsible for this Associate's billing object association
    private PayingCustomer payer;

    // Constructor 
    public AssociateCustomer(String name, String email, PayingCustomer payer) {
        // Reuses parent class constructor 
        super(name, email);
        this.payer = payer;
    }
    
    public PayingCustomer getPayer(){
        return payer;
    }
    
    public void setPayer(PayingCustomer payer){
        if(payer == null){
            throw new IllegalArgumentException("Payer cannot be null!");
        }
        this.payer = payer;
    }
    
    @Override
    public String getSpecificInfo(){
        return payer != null ? payer.getName() : "";
    }
    
    public void setSpecificInfo(String Info){
        
    }
}
