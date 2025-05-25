/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: BillingController.java
 * Purpose: Controls the billing view of the Magazine Service system.
 *          It displays both weekly and monthly billing information for a customer,
 *          allows switching between months, and supports background loading using tasks.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Customer;
import model.PayingCustomer;
import service.BillingService;

import java.time.LocalDate;
import java.util.stream.IntStream;
import javafx.concurrent.Task;

public class BillingController {

    // FXML-bound UI elements
    @FXML private VBox billingRoot; 
    @FXML private Label customerNameLabel; 
    @FXML private TextArea weeklyBillingArea; 
    @FXML private ComboBox<String> monthComboBox; 
    @FXML private TextArea monthlyBillingArea; 

    // Non-FXML fields
    private StackPane mainContentPane; // Reference to the main content panel (for view switching)
    private VBox detailsPane; // Reference to the details pane (to go back to)
    private Customer customer; // The customer whose billing info is shown

    // Month names for populating the ComboBox
    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    // Sets the customer billing displayed, initializes the month selection, and triggers billing calculations.
    public void setCustomer(Customer customer, StackPane mainContentPane, VBox detailsPane) {
        this.customer = customer;
        this.mainContentPane = mainContentPane;
        this.detailsPane = detailsPane;

        customerNameLabel.setText("Billing for: " + customer.getName());

        // Populate the month combo box
        monthComboBox.getItems().clear();
        IntStream.rangeClosed(1, 12).forEach(m -> monthComboBox.getItems().add(MONTH_NAMES[m - 1]));
        int currentMonth = LocalDate.now().getMonthValue();
        monthComboBox.getSelectionModel().select(currentMonth - 1);

        // When the user selects a month, reload monthly billing
        monthComboBox.setOnAction(e -> {
            int selectedMonth = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            loadMonthlyBillingAsync(selectedMonth);
        });

        // Load initial weekly and monthly billing asynchronously
        loadWeeklyBillingAsync();
        loadMonthlyBillingAsync(currentMonth);

        // Show the billing pane
        billingRoot.setVisible(true);
        billingRoot.setManaged(true);
    }

    // Loads weekly billing information on a background thread and displays the result.
    private void loadWeeklyBillingAsync() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                float total = BillingService.getWeeklyCharge(customer);
                return BillingService.generateWeeklyBilling(customer) + "\nTotal Weekly Cost: $" + String.format("%.2f", total);
            }
        };

        task.setOnSucceeded(e -> weeklyBillingArea.setText(task.getValue())); // Display result on success
        task.setOnFailed(e -> weeklyBillingArea.setText("Error loading weekly billing.")); // Show error on failure
        new Thread(task).start(); // Run the task in a new thread
    }
    
    //Loads monthly billing info for a given month on a background thread.
    private void loadMonthlyBillingAsync(int month) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return BillingService.generateMonthlyBilling(customer, month, LocalDate.now().getYear());
            }
        };

        task.setOnSucceeded(e -> monthlyBillingArea.setText(task.getValue())); // Display result on success
        task.setOnFailed(e -> monthlyBillingArea.setText("Error loading monthly billing.")); // Show error on failure
        new Thread(task).start(); // Run the task in a new thread
    }

    // Synchronously updates monthly billing, used internally or for specific fallback logic.
    private void updateMonthlyBilling(int month) {
        if (customer instanceof PayingCustomer) {
            String result = BillingService.generateMonthlyBilling(customer, month, LocalDate.now().getYear());
            monthlyBillingArea.setText(result);
        } else {
            monthlyBillingArea.setText("Monthly billing is only available for Paying Customers.");
        }
    }
    
    // Handles the back button. Returns to the customer details panel.
    @FXML
    private void handleBack() {
        billingRoot.setVisible(false);
        billingRoot.setManaged(false);
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
    }
}
