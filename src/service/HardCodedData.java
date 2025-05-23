/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: HardCodedData.java
 * Purpose: Loads preset data for testing the magazine subscription system including customers and supplements.
 */

package service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import model.AssociateCustomer;
import model.CreditCard;
import model.DirectDebit;
import model.EnterpriseCustomer;
import model.MagazineData;
import model.PayingCustomer;
import model.PaymentMethod;
import model.Supplement;

public class HardCodedData {
    
    // Wrapper method to expose loadData if needed elsewhere
    public void hardcodedData() {
        loadData();
    }

    public static void loadData() {
        
        MagazineService.clearCustomers(); // Clear previous customer data

        // Create supplement options
        Supplement sports = new Supplement("Sports", 2.0);
        Supplement tech = new Supplement("Tech", 3.0);
        Supplement racing = new Supplement("Racing", 1.5);
        Supplement news = new Supplement("News", 1.0);
        
        List<Supplement> allSupplements = List.of(sports, tech, racing, news);
        MagazineService.setAvailableSupplements(allSupplements);


        // Joseph - PayingCustomer with CreditCard
        PaymentMethod card = new PaymentMethod(new CreditCard("1234567812345678", 
            "12/25", "Joseph Akongo"));
        PayingCustomer joseph = new PayingCustomer("Joseph Akongo", "joseph@gmail.com", card);

        // Associate customers under Joseph
        AssociateCustomer cian = new AssociateCustomer("Cian Butler", "cian@outlook.com", joseph);
        AssociateCustomer ropes = new AssociateCustomer("Ropes Murray", "ropes@yahoo.com", joseph);

        // Add associates to Joseph
        joseph.addAssociate(cian);
        joseph.addAssociate(ropes);

        // Assign supplements
        joseph.addSupplement(tech);
        cian.addSupplement(racing);
        cian.addSupplement(news);
        ropes.addSupplement(sports);

        // Brenton - PayingCustomer with DirectDebit
        PaymentMethod direct = new PaymentMethod(new DirectDebit(12345678, 123456));
        PayingCustomer brenton = new PayingCustomer("Brenton", "Brenton@icloud.com", direct);
        brenton.addSupplement(news);

        // Akongo Soft Ltd - EnterpriseCustomer with 10 copies and DirectDebit
        PaymentMethod debit = new PaymentMethod(new DirectDebit(87654321, 987654));
        EnterpriseCustomer.ContactPerson contactPerson = new EnterpriseCustomer.ContactPerson("Jane Doe", 
            "jdoe@akongosoft.com");
        EnterpriseCustomer enterprise = new EnterpriseCustomer("Akongo Soft Ltd", "contact@akongosoft.com", 
            debit, contactPerson, 10);
        enterprise.addSupplement(sports);
        enterprise.addSupplement(tech);
        enterprise.addSupplement(racing);
        enterprise.addSupplement(news);

        // Register all customers
        MagazineService.addCustomer(joseph);
        MagazineService.addCustomer(cian);
        MagazineService.addCustomer(ropes);
        MagazineService.addCustomer(brenton);
        MagazineService.addCustomer(enterprise);
        
        System.out.println("Hardcoded data loaded successfully!");
    }
    
    public static void saveToFile() {
        // Call loadData() to populate MagazineService
        loadData();

        // Create MagazineData object
        MagazineData data = new MagazineData(
            "Tech World Weekly",                       
            9.99f,                                     
            MagazineService.getAvailableSupplements(),
            MagazineService.getCustomers()            
        );

        // Save to file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("magazineData.dat"))) {
            out.writeObject(data);
            System.out.println("magazineData.dat saved successfully.");
        } catch (IOException e) {
            System.err.println("magazinedata.dat Saved successfully");
            e.printStackTrace();
        }
    }
}
