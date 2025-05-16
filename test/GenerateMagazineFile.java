//import java.io.FileOutputStream;
//import java.io.ObjectOutputStream;
//import java.util.List;
//import model.*;
//
//public class GenerateMagazineFile {
//    public static void main(String[] args) {
//        try {
//            // Create supplements
//            Supplement tech = new Supplement("Tech", 3.0f);
//            Supplement sports = new Supplement("Sports", 2.0f);
//            Supplement news = new Supplement("News", 1.0f);
//
//            // Create a paying customer
//            PayingCustomer joseph = new PayingCustomer("Joseph Akongo", "joseph@example.com", null);
//            joseph.addSupplement(tech);
//
//            // Associate under Joseph
//            AssociateCustomer cian = new AssociateCustomer("Cian Butler", "cian@example.com", joseph);
//            cian.addSupplement(sports);
//            cian.addSupplement(news);
//
//            // Add to list
//            List<Customer> customers = List.of(joseph, cian);
//            List<Supplement> supplements = List.of(tech, sports, news);
//
//            // Create MagazineData object
//            MagazineData data = new MagazineData("Tech World Weekly", 9.99f, supplements, customers);
//
//            // Save to file in the root project directory
//            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("magazineData.dat"))) {
//                out.writeObject(data);
//                System.out.println("✅ magazineData.dat saved successfully in the project directory.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
