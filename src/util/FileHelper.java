/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: FileHelper.java
 * Purpose: Handles loading and saving of MagazineData to/from .dat files, including dialogs and error alerts.
 */

package util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.*;
import service.MagazineService;
import javafx.scene.control.Alert;
import java.io.*;
import java.util.List;

public class FileHelper {

    // Returns a pre-configured FileChooser for .dat files
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

    // Shows open file dialog for loading .dat files
    public static File showOpenDialog(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return fileChooser.showOpenDialog(window);
    }

    // Shows save file dialog for saving .dat files
    public static File showSaveDialog(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT files", "*.dat"));
        fileChooser.setInitialFileName("magazineData.dat");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return fileChooser.showSaveDialog(window);
    }

    // Loads MagazineData from a file and populates the system
    public static boolean loadMagazineFromFile(File file) {
        if (file == null || !file.exists()) {
            showAlert("Load Failed", "No file selected or file does not exist.");
            return false;
        }

        if (file.length() == 0) {
            showAlert("Load Failed", "The selected file is empty.");
            return false;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof MagazineData data) {
                MagazineService.clearCustomers();
                MagazineService.setAvailableSupplements(data.getSupplements());
                MagazineService.setMagazine(new Magazine(data.getMagazineName(), data.getPrice()));
                for (Customer c : data.getCustomers()) {
                    MagazineService.addCustomer(c);
                }
                return true;
            } else {
                showAlert("Load Failed", "Invalid file format.");
                return false;
            }
        } catch (EOFException e) {
            showAlert("Load Failed", "The selected file appears to be corrupted or incomplete.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Load Failed", "An unexpected error occurred while loading the file.");
        }

        return false;
    }

    // Saves current MagazineService state to a file
    public static boolean saveMagazineToFile(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            List<Magazine> magazines = MagazineService.getMagazines();
            if (magazines.isEmpty()) {
                showAlert("Save Error", "No magazine exists to save.");
                return false;
            }

            Magazine magazine = magazines.get(0);
            MagazineData data = new MagazineData(
                magazine.getName(),
                magazine.getPrice(),
                MagazineService.getAvailableSupplements(),
                MagazineService.getCustomers()
            );

            System.out.println("Saving magazine: " + data.getMagazineName());
            System.out.println("Price: " + data.getPrice());
            System.out.println("Customers: " + data.getCustomers().size());
            System.out.println("Supplements: " + data.getSupplements().size());

            out.writeObject(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Save Error", "Failed to save magazine to file.");
            return false;
        }
    }

    // Displays an error alert dialog with the given message
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
