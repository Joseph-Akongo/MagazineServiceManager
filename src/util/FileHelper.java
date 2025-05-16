// New helper class to handle file operations
package util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.Customer;
import model.Magazine;
import model.MagazineData;
import service.MagazineService;

import java.io.*;
import java.util.List;

public class FileHelper {
    
    public static FileChooser getDatFileChooser(String title, boolean saveMode) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        if (saveMode) {
            fileChooser.setInitialFileName("magazineData.dat");
        }
        return fileChooser;
    }
    
    public static File showOpenDialog(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return fileChooser.showOpenDialog(window);
    }

    public static File showSaveDialog(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        fileChooser.setInitialFileName("magazineData.dat");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return fileChooser.showSaveDialog(window);
    }

    public static boolean loadMagazineFromFile(File file) {
        if (file != null && file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                MagazineData data = (MagazineData) in.readObject();

                MagazineService.clearCustomers();
                MagazineService.setAvailableSupplements(data.getSupplements());
                MagazineService.setMagazine(new Magazine(data.getMagazineName(), data.getPrice()));
                for (Customer c : data.getCustomers()) {
                    MagazineService.addCustomer(c);
                }
                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean saveMagazineToFile(File file) {
        if (file != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                Magazine magazine = (Magazine) MagazineService.getMagazines();
                List<Customer> customers = MagazineService.getCustomers();
                List<model.Supplement> supplements = MagazineService.getAvailableSupplements();

                MagazineData data = new MagazineData(magazine.getName(), magazine.getCost(), supplements, customers);
                out.writeObject(data);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}