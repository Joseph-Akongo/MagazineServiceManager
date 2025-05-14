package model;

/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: Supplement.java
 * Purpose: Represents optional magazine content a customer can subscribe to, with a name and weekly cost.
 */

// Add-on to a customer's magazine subscription.
public class Supplement {
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

    // Optional: Useful for displaying in menus or summaries
    @Override
    public String toString() {
        return name + " ($" + String.format("%.2f", weeklyCost) + "/week)";
    }
}
