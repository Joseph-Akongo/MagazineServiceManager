/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: Supplement.java
 * Purpose: Represents optional magazine content a customer can subscribe to, with a name and weekly cost.
 */

package model;

import java.io.Serializable;

// Add-on to a customer's magazine subscription.
public class Supplement implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private double weeklyCost;

    // Constructor with basic validation
    public Supplement(String name, double weeklyCost) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Supplement name cannot be empty.");
        }
        if (weeklyCost < 0) {
            throw new IllegalArgumentException("Supplement cost cannot be negative.");
        }

        this.name = name;
        this.weeklyCost = weeklyCost;
    }

    // Updates the supplement's name (if needed)
    public void setSupplement(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid supplement name.");
        }
        this.name = name;
    }

    // Returns the name of the supplement
    public String getName() {
        return name;
    }

    // Returns the weekly cost of the supplement
    public double getWeeklyCost() {
        return weeklyCost;
    }

    @Override
    public String toString() {
        return name + " ($" + String.format("%.2f", weeklyCost) + "/week)";
    }

    public void setName(String name) {
       this.name = name;
    }

    public void setWeeklyCost(double weeklyCost) {
        this.weeklyCost = weeklyCost;
    }
}
