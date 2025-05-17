package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;
import util.FileHelper;

public class MagazineController {

    @FXML private TreeView<String> treeView;
    @FXML private Label typeLabel;
    @FXML private Label nameLabel;
    @FXML private Label payerLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label contactPersonLabel;
    @FXML private Label footer;
    @FXML private Label payerLabelTitle;
    @FXML private Label numberOfCopiesLabel;
    @FXML private VBox payingBox;
    @FXML private VBox enterpriseBox;
    @FXML private MenuItem createPaying;
    @FXML private MenuItem createAssociate;
    @FXML private MenuItem createEnterprise;
    @FXML private MenuItem editCustomer;
    @FXML private MenuItem loadFile;
    
    @FXML private ListView<String> supplementList;

    @FXML
    public void initialize() {
        loadTreeData();
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Customer selectedCustomer = MagazineService.findCustomerByName(newVal.getValue());
                if (selectedCustomer != null) {
                    updateDetailView(selectedCustomer);
                }
            }
        });
        
        createPaying.setOnAction(e -> showDialog("CreatePayingCustomerView.fxml"));
        createAssociate.setOnAction(e -> showDialog("CreateAssociateCustomerView.fxml"));
        createEnterprise.setOnAction(e -> showDialog("CreateEnterpriseCustomerView.fxml"));
        loadFile.setOnAction(e -> handleLoad());
        editCustomer.setOnAction(e -> handleEdit());
    }

    private void updateDetailView(Customer customer) {
        // Reset visibility
        payerLabel.setVisible(false);
        payerLabel.setManaged(false);
        payerLabelTitle.setVisible(false);
        payerLabelTitle.setManaged(false);
        payingBox.setVisible(false);
        payingBox.setManaged(false);
        enterpriseBox.setVisible(false);
        enterpriseBox.setManaged(false);

        // Common
        nameLabel.setText(customer.getName());
        typeLabel.setText(customer.getClass().getSimpleName());

        // AssociateCustomer: show payer
        if (customer instanceof AssociateCustomer ac) {
            payerLabel.setText(ac.getPayer().getName());
            payerLabel.setVisible(true);
            payerLabel.setManaged(true);
            payerLabelTitle.setVisible(true);
            payerLabelTitle.setManaged(true);
        }

        // PayingCustomer (but not Enterprise)
        if (customer instanceof PayingCustomer pc && !(customer instanceof EnterpriseCustomer)) {
            payingBox.setVisible(true);
            payingBox.setManaged(true);
            paymentMethodLabel.setText(pc.getPaymentMethod() != null ? pc.getPaymentMethod().toString() : "N/A");
        }

        // EnterpriseCustomer: show contact, copies, and payment
        if (customer instanceof EnterpriseCustomer ec) {
            enterpriseBox.setVisible(true);
            enterpriseBox.setManaged(true);

            contactPersonLabel.setText(ec.getContact() != null
                ? ec.getContact().getContactDetails()
                : "No contact person");

            numberOfCopiesLabel.setText(String.valueOf(ec.getNumberOfCopies()));

            payingBox.setVisible(true);
            payingBox.setManaged(true);
            paymentMethodLabel.setText(ec.getPaymentMethod() != null ? ec.getPaymentMethod().toString() : "N/A");
        }

        // Supplements
        supplementList.getItems().clear();
        for (Supplement s : customer.getSupplements()) {
            supplementList.getItems().add(s.getName() + " ($" + s.getWeeklyCost() + ")");
        }
    }

    private void showDialog(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("New Dialog");
            stage.showAndWait();
            loadTreeData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            footer.setText("Please select a customer to edit.");
            return;
        }

        Customer customer = MagazineService.findCustomerByName(selectedItem.getValue());
        if (customer == null) {
            footer.setText("Customer not found.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditCustomerView.fxml"));
            Parent root = loader.load();

            EditCustomerController controller = loader.getController();
            controller.setCustomer(customer);

            Stage stage = new Stage();
            stage.setTitle("Edit Customer");
            stage.setScene(new Scene(root));
            stage.initOwner(treeView.getScene().getWindow());
            stage.showAndWait();

            updateDetailView(customer);
            loadTreeData();

        } catch (Exception e) {
            e.printStackTrace();
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

            // Optional: refresh data if supplements affect the view
            loadTreeData();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open supplement creation window.").showAndWait();
        }
    }

    private void loadTreeData() {
        TreeItem<String> root = new TreeItem<>("Customers");
        TreeItem<String> payingNode = new TreeItem<>("Paying Customers");
        TreeItem<String> associateNode = new TreeItem<>("Associate Customers");
        TreeItem<String> enterpriseNode = new TreeItem<>("Enterprise Customers");

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

        root.getChildren().addAll(payingNode, associateNode, enterpriseNode);
        root.setExpanded(true);
        treeView.setRoot(root);
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

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    

}
