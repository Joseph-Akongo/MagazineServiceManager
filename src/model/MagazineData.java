package model;

import java.io.Serializable;
import java.util.List;

public class MagazineData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String magazineName;
    private float price;
    private List<Supplement> supplements;
    private List<Customer> customers;

    public MagazineData(String magazineName, float price,
                        List<Supplement> supplements, List<Customer> customers) {
        this.magazineName = magazineName;
        this.price = price;
        this.supplements = supplements;
        this.customers = customers;
    }

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
