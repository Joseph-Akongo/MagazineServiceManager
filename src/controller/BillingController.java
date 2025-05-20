package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Customer;
import service.BillingService;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BillingController {

    @FXML private Label customerNameLabel;
    @FXML private Label weeklyChargeLabel;
    @FXML private Label monthlyChargeLabel;
    @FXML private VBox billingRoot;

    private StackPane mainContentPane;
    private VBox detailsPane;

    public void setCustomer(Customer customer, StackPane mainContentPane, VBox detailsPane) {
        billingRoot.setVisible(true);
        billingRoot.setManaged(true);

        this.mainContentPane = mainContentPane;
        this.detailsPane = detailsPane;

        customerNameLabel.setText("Customer: " + customer.getName());
        weeklyChargeLabel.setText("Weekly: $" + BillingService.getWeeklyCharge(customer));
        monthlyChargeLabel.setText("Monthly: $" + BillingService.getMonthlyCharge(customer));
    }

    @FXML
    private void handleBack() {
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
        mainContentPane.lookup("#billingRoot").setVisible(false);
        mainContentPane.lookup("#billingRoot").setManaged(false);
    }
}
