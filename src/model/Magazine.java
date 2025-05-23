/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: Magazine.java
 * Purpose: Represents a magazine with a name and base price.
 */

package model;

import java.io.Serializable;

public class Magazine implements Serializable {

    private static final long serialVersionUID = 1L;

    private String magName; // Magazine title
    private float price;    // Weekly price

    public Magazine(String magName, float price) {
        this.magName = magName;
        this.price = price;
    }

    public String getName() {
        return magName;
    }

    public float getPrice() {
        return price;
    }

    // Sets the magazine name
    public void setName(String magName) {
        this.magName = magName;
    }

    // Sets the magazine price with validation
    public void setPrice(float price) {
        if (price < 0) {
            System.out.println("Invalid float was inputted");
            return;
        }
        this.price = price;
    }
}
