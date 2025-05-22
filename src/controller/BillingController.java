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

    @FXML private VBox billingRoot;
    @FXML private Label customerNameLabel;
    @FXML private TextArea weeklyBillingArea;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private TextArea monthlyBillingArea;

    private StackPane mainContentPane;
    private VBox detailsPane;
    private Customer customer;

    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public void setCustomer(Customer customer, StackPane mainContentPane, VBox detailsPane) {
        this.customer = customer;
        this.mainContentPane = mainContentPane;
        this.detailsPane = detailsPane;

        customerNameLabel.setText("Billing for: " + customer.getName());

        // Load months
        monthComboBox.getItems().clear();
        IntStream.rangeClosed(1, 12).forEach(m -> monthComboBox.getItems().add(MONTH_NAMES[m - 1]));
        int currentMonth = LocalDate.now().getMonthValue();
        monthComboBox.getSelectionModel().select(currentMonth - 1);

        // Add change listener
        monthComboBox.setOnAction(e -> {
            int selectedMonth = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            loadMonthlyBillingAsync(selectedMonth);
        });

        // Load billing asynchronously
        loadWeeklyBillingAsync();
        loadMonthlyBillingAsync(currentMonth);

        billingRoot.setVisible(true);
        billingRoot.setManaged(true);
    }
    
    private void loadWeeklyBillingAsync() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                float total = BillingService.getWeeklyCharge(customer);
                return BillingService.generateWeeklyBilling(customer) + "\nTotal Weekly Cost: $" + String.format("%.2f", total);
            }
        };

        task.setOnSucceeded(e -> weeklyBillingArea.setText(task.getValue()));
        task.setOnFailed(e -> weeklyBillingArea.setText("Error loading weekly billing."));
        new Thread(task).start();
    }

    private void loadMonthlyBillingAsync(int month) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return BillingService.generateMonthlyBilling(customer, month, LocalDate.now().getYear());
            }
        };

        task.setOnSucceeded(e -> monthlyBillingArea.setText(task.getValue()));
        task.setOnFailed(e -> monthlyBillingArea.setText("Error loading monthly billing."));
        new Thread(task).start();
    }

    private void updateMonthlyBilling(int month) {
        if (customer instanceof PayingCustomer) {
            String result = BillingService.generateMonthlyBilling(customer, month, LocalDate.now().getYear());
            monthlyBillingArea.setText(result);
        } else {
            monthlyBillingArea.setText("Monthly billing is only available for Paying Customers.");
        }
    }

    @FXML
    private void handleBack() {
        billingRoot.setVisible(false);
        billingRoot.setManaged(false);
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
    }
}
