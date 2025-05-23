/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: Customer.java
 * Purpose: Defines a generic magazine customer with name, email, and a list of subscribed supplements.
 *          This class serves as the base for more specific customer types like PayingCustomer and AssociateCustomer.
 */

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private List<Supplement> supplements = new ArrayList<>();

    // Constructor with input validation
    public Customer(String name, String email) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (!name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException("Name must contain only letters and spaces.");
        }
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.name = name;
        this.email = email;
    }

    public void addSupplement(Supplement supplement) {
        if (supplement != null) supplements.add(supplement);
    }

    public List<Supplement> getSupplements() {
        return supplements;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    
    public String getSpecificInfo() {
        return "";
    }
    
    // updates the name with validation.
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (!name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException("Name must contain only letters and spaces.");
        }
        this.name = name;
    }

    
    // updates the email with validation.
    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }
    
    public void setSupplements(List<Supplement> supplements) {
        if (supplements == null) {
            throw new IllegalArgumentException("Supplements list cannot be null.");
        }
        this.supplements = new ArrayList<>(supplements); // defensive copy
    }
    
    @Override
    public String toString() {
        return name; // or return name + " (" + email + ")" if you want more detail
    }
}
