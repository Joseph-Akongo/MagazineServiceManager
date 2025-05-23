/**
 * Author: Joseph Akongo
 * Student Number: 33255426
 * File: MagazineController.java
 * Purpose: Main controller for managing the Magazine Service system UI.
 *          Handles creation, editing, viewing, billing, file operations, and data loading
 *          for customers and supplements.
 */

package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;
import util.FileHelper;

public class MagazineController {
    
    @FXML private ListView<String> supplementList;
    @FXML private TreeView<String> treeView;
    @FXML private Label typeLabel, nameLabel, emailLabel, payerLabel, paymentMethodLabel, contactPersonLabel;
    @FXML private Label footer, payerLabelTitle, numberOfCopiesLabel, listOfAssociates;
    @FXML private Label supplementNameLabel, supplementCostLabel;
    @FXML private StackPane mainContentPane;
    @FXML private VBox supplementDetailBox, detailsPane, payingBox, associatesBox, enterpriseBox;
    @FXML private MenuItem createPaying, createAssociate, createEnterprise, editCustomer, loadFile;
    @FXML private Button billingButton;
    
    private Customer currentCustomer;

    // Initializes the controller and configures event handlers for UI actions.
    @FXML
    public void initialize() {
        loadTreeData();
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            String selectedName = newVal.getValue();

            Customer selectedCustomer = MagazineService.findCustomerByName(selectedName);
            Supplement selectedSupplement = MagazineService.findSupplementByName(selectedName);

            if (selectedCustomer != null) {
                // Hide supplement panel
                supplementDetailBox.setVisible(false);
                supplementDetailBox.setManaged(false);

                // Show customer panel
                detailsPane.setVisible(true);
                detailsPane.setManaged(true);

                currentCustomer = selectedCustomer;
                updateDetailView(currentCustomer);
            } else if (selectedSupplement != null) {
                // Hide customer panel
                detailsPane.setVisible(false);
                detailsPane.setManaged(false);

                // Show supplement panel
                supplementDetailBox.setVisible(true);
                supplementDetailBox.setManaged(true);

                updateSupplementDetailView(selectedSupplement);
            }
            
            // Hide billing view if active
            if (mainContentPane.lookup("#billingRoot") != null) {
                mainContentPane.lookup("#billingRoot").setVisible(false);
                mainContentPane.lookup("#billingRoot").setManaged(false);
            }
        }
    });
        
        createPaying.setOnAction(e -> showDialog("CreatePayingCustomerView.fxml"));
        createAssociate.setOnAction(e -> showDialog("CreateAssociateCustomerView.fxml"));
        createEnterprise.setOnAction(e -> showDialog("CreateEnterpriseCustomerView.fxml"));
        loadFile.setOnAction(e -> handleLoad());
        editCustomer.setOnAction(e -> handleEdit());
    }

    public void updateDetailView(Customer customer) {
        // Reset visibility
        payerLabel.setVisible(false);
        payerLabel.setManaged(false);
        payerLabelTitle.setVisible(false);
        payerLabelTitle.setManaged(false);
        payingBox.setVisible(false);
        payingBox.setManaged(false);
        enterpriseBox.setVisible(false);
        enterpriseBox.setManaged(false);
        associatesBox.setVisible(false);
        associatesBox.setManaged(false);
        
        // Billing button logic
        billingButton.setOnAction(e -> handleBillingView());

        // Common
        nameLabel.setText(customer.getName());
        emailLabel.setText(customer.getEmail());
        typeLabel.setText(customer.getClass().getSimpleName());

        // AssociateCustomer: show payer
        if (customer instanceof AssociateCustomer ac) {
            payerLabel.setText(ac.getPayer().getName());
            payerLabel.setVisible(true);
            payerLabel.setManaged(true);
            payerLabelTitle.setVisible(true);
            payerLabelTitle.setManaged(true);
            billingButton.setVisible(false);
            billingButton.setManaged(false);
        }

        // Determine if the customer is a non-associate PayingCustomer
        PayingCustomer pc = (customer instanceof PayingCustomer) ? (PayingCustomer) customer : null;
        if (pc != null && !(customer instanceof AssociateCustomer)) {
            associatesBox.setVisible(true);
            associatesBox.setManaged(true);
            billingButton.setVisible(true);
            billingButton.setManaged(true);
            
            if (pc.getAssociates().isEmpty()) {
                listOfAssociates.setText("None");
            } else {
                StringBuilder sb = new StringBuilder();
                for (AssociateCustomer ac : pc.getAssociates()) {
                    sb.append("- ").append(ac.getName()).append(" (").append(ac.getEmail()).append(")\n");
                }
                listOfAssociates.setText(sb.toString().trim());
            }

            payingBox.setVisible(true);
            payingBox.setManaged(true);
            paymentMethodLabel.setText(pc.getPaymentMethod() != null ? pc.getPaymentMethod().toString() : "N/A");
        }

        // EnterpriseCustomer: show contact and copies
        if (customer instanceof EnterpriseCustomer ec) {
            enterpriseBox.setVisible(true);
            enterpriseBox.setManaged(true);

            contactPersonLabel.setText(ec.getContact() != null
                ? ec.getContact().getContactDetails()
                : "No contact person");

            numberOfCopiesLabel.setText(String.valueOf(ec.getNumberOfCopies()));
        }

        // Supplements
        supplementList.getItems().clear();
        for (Supplement s : customer.getSupplements()) {
            supplementList.getItems().add(s.getName() + " ($" + s.getWeeklyCost() + ")");
        }
    }
    
    private void loadTreeData() {
        treeView.setRoot(null);  // Clear existing tree

        // Root node named after the magazine
        String magazineName = (MagazineService.getMagazine() != null)? MagazineService.getMagazine().getName(): "Magazine Service";
        TreeItem<String> root = new TreeItem<>(magazineName);

        // Main categories under root
        TreeItem<String> customersNode = new TreeItem<>("Customers");
        TreeItem<String> supplementsNode = new TreeItem<>("Supplements");

        // Sub-categories under "Customers"
        TreeItem<String> payingNode = new TreeItem<>("Paying Customers");
        TreeItem<String> associateNode = new TreeItem<>("Associate Customers");
        TreeItem<String> enterpriseNode = new TreeItem<>("Enterprise Customers");

        // Populate customers
        for (Customer c : MagazineService.getCustomers()) {
            TreeItem<String> item = new TreeItem<>(c.getName());
            if (c instanceof EnterpriseCustomer) {
                enterpriseNode.getChildren().add(item);
            } else if (c instanceof PayingCustomer) {
                payingNode.getChildren().add(item);
            } else if (c instanceof AssociateCustomer) {
                associateNode.getChildren().add(item);
            }
        }

        // Populate supplements
        for (Supplement s : MagazineService.getAvailableSupplements()) {
            TreeItem<String> item = new TreeItem<>(s.getName());
            supplementsNode.getChildren().add(item);
        }

        // Combine and set root
        customersNode.getChildren().addAll(payingNode, associateNode, enterpriseNode);
        root.getChildren().addAll(customersNode, supplementsNode);
        root.setExpanded(true);
        treeView.setRoot(root);
    }
    
    // Refreshes the tree view while keeping the magazine root.
    public void refreshTreeView() {
        treeView.getRoot().getChildren().clear();
        for (Customer c : MagazineService.getCustomers()) {
            TreeItem<String> customerItem = new TreeItem<>(c.getName());
            treeView.getRoot().getChildren().add(customerItem);
        }
    }

    private void showDialog(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create");
            stage.showAndWait();
            loadTreeData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateSupplementDetailView(Supplement s) {
        detailsPane.setVisible(false);
        detailsPane.setManaged(false);
        supplementDetailBox.setVisible(true);
        supplementDetailBox.setManaged(true);

        supplementNameLabel.setText(s.getName());
        supplementCostLabel.setText("$" + s.getWeeklyCost());
    }

    // Editing of a selected customer.
    private void handleEdit() {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select a customer to edit.");
            return;
        }

        String customerName = selectedItem.getValue();
        Customer selectedCustomer = MagazineService.findCustomerByName(customerName);
        if (selectedCustomer == null) {
            showAlert("Error", "Selected customer not found.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditCustomerView.fxml"));
            Parent root = loader.load();

            EditCustomerController controller = loader.getController();
            // Pass both the customer and the TreeItem so it can be updated directly
            controller.setCustomer(selectedCustomer, selectedItem);

            Stage stage = new Stage();
            stage.setTitle("Edit Customer");
            stage.setScene(new Scene(root));
            stage.showAndWait();  // Wait until the edit window is closed

            // Refresh the detail view with the updated data
            loadTreeData();
            
            // Always reload the selected customer from service to get fresh references
            Customer updated = MagazineService.findCustomerByName(selectedCustomer.getName());
            updateDetailView(updated);

            // Print updated info for verification
            System.out.print("Updated customer: " + selectedCustomer.getName() + ", Email: " + 
                    selectedCustomer.getEmail() + ", Supplements: " + selectedCustomer.getSupplements().size());
            
            // If Associate, print the payer
            if (selectedCustomer instanceof AssociateCustomer ac) {
                System.out.println(", Payer: " + (ac.getPayer() != null ? ac.getPayer().getName() : "None"));
            }
            
            System.out.println();

        } catch (IOException e) {
            showAlert("Error", "Could not load edit view.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditMagazine() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditMagazineView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Edit Magazine Information");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh footer or tree if name/cost changes
            Magazine updated = MagazineService.getMagazine();
            if (updated != null) {
                footer.setText("Magazine: " + updated.getName() + " ($" + updated.getPrice() + ")");
                loadTreeData();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load Edit Magazine view.");
        }
    }
    
    @FXML
    private void handleEditSupplement() {
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Select a supplement to edit.");
            return;
        }

        Supplement supplement = MagazineService.findSupplementByName(selected.getValue());
        if (supplement == null) {
            showAlert("Invalid Selection", "Selected item is not a supplement.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditSupplementView.fxml"));
            Parent root = loader.load();

            EditSupplementController controller = loader.getController();
            controller.setSupplement(supplement);

            Stage stage = new Stage();
            stage.setTitle("Edit Supplement");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // After editing
            updateSupplementDetailView(supplement);
            loadTreeData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open supplement editor.");
        }
    }
   
    
    @FXML
    private void handleCreateSupplement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateSupplementView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create Supplement");
            stage.setScene(new Scene(root));
            stage.initOwner(treeView.getScene().getWindow()); // or use any visible node
            stage.showAndWait();

            // Refresh data if supplements affect the view
            loadTreeData();
            
            updateDetailView(currentCustomer);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open supplement creation window.").showAndWait();
        }
    }
    
    public void handleLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Magazine File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DATass files","*.dat"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        
        File file = fileChooser.showOpenDialog(treeView.getScene().getWindow());
        
        if (file != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                MagazineData data = (MagazineData) in.readObject();

                // Clear existing data
                MagazineService.clearCustomers();
                MagazineService.setAvailableSupplements(data.getSupplements());
                MagazineService.setMagazine(new Magazine(data.getMagazineName(), data.getPrice()));
                for (Customer c : data.getCustomers()) {
                    MagazineService.addCustomer(c);
                }

                loadTreeData();  // Refresh view
                footer.setText("Loaded magazine: " + data.getMagazineName());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                footer.setText("Failed to load magazine file.");
            }
        } else {
            footer.setText("No file selected.");
        }
    }
    
    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = FileHelper.getDatFileChooser("Open Magazine File", false);
        File file = fileChooser.showOpenDialog(treeView.getScene().getWindow());
        if (FileHelper.loadMagazineFromFile(file)) {
            loadTreeData();
            footer.setText("Magazine loaded: " + file.getName());
        } else {
            showAlert("Load Failed", "Could not load magazine from file.");
        }
    }

    @FXML
    private void handleSaveFile() {
        FileChooser fileChooser = FileHelper.getDatFileChooser("Save Magazine File", true);
        File file = fileChooser.showSaveDialog(treeView.getScene().getWindow());
        if (FileHelper.saveMagazineToFile(file)) {
            footer.setText("Magazine saved: " + file.getName());
        } else {
            showAlert("Save Failed", "Could not save magazine to file.");
        }
    }

    // Opens the billing pane for the selected paying customer
    @FXML
    private void handleBillingView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BillingView.fxml"));
            VBox billingPane = loader.load();
            BillingController billingController = loader.getController();
            billingController.setCustomer(currentCustomer, mainContentPane, detailsPane);

            // Replace visible content
            mainContentPane.getChildren().setAll(detailsPane, supplementDetailBox, billingPane);
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);
            billingPane.setVisible(true);
            billingPane.setManaged(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Exit
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    //Alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private static MagazineController instance;

    public MagazineController() {
        instance = this;
    }

    public static MagazineController getInstance() {
        return instance;
    }
}
