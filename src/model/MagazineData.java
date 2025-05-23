/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: MagazineData.java
 * Purpose: Serializable data holder for saving and loading magazine info, supplements, and customers.
 */

package model;

import java.io.Serializable;
import java.util.List;

public class MagazineData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String magazineName; // Name of the magazine
    private float price; // Base price of the magazine
    private List<Supplement> supplements; // List of supplements
    private List<Customer> customers; // List of all customers

    // Constructor to initialize all fields
    public MagazineData(String magazineName, float price,
                        List<Supplement> supplements, List<Customer> customers) {
        this.magazineName = magazineName;
        this.price = price;
        this.supplements = supplements;
        this.customers = customers;
    }

    // Getters
    public String getMagazineName() { 
        return magazineName; 
    }
    
    public float getPrice() { 
        return price; 
    }
    
    public List<Supplement> getSupplements() { 
        return supplements; 
    }
    
    public List<Customer> getCustomers() { 
        return customers; 
    }
}
