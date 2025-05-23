/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: AssociateCustomer.java
 * Purpose: Represents a customer whose subscription is paid by a PayingCustomer.
 *          Inherits from Customer and maintains a reference to their payer.
 */

package model;

import java.io.Serializable;

public class AssociateCustomer extends Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private PayingCustomer payer; // Link to the PayingCustomer

    public AssociateCustomer(String name, String email, PayingCustomer payer) {
        super(name, email); // Call parent constructor
        this.payer = payer;
    }

    public PayingCustomer getPayer() {
        return payer;
    }

    public void setPayer(PayingCustomer payer) {
        if (payer == null) {
            throw new IllegalArgumentException("Payer cannot be null!");
        }
        this.payer = payer;
    }
}
