package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import service.MagazineService;

public class MagazineController {

    @FXML private TreeView<String> treeView;
    @FXML private Label typeLabel;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label paymentLabel;
    @FXML private Label supplementsLabel;
    @FXML private Label footer;

    @FXML private MenuItem createPaying;
    @FXML private MenuItem createAssociate;
    @FXML private MenuItem createEnterprise;
    @FXML private MenuItem editCustomer;
    @FXML private MenuItem loadFile;
    @FXML private Label contactLabel;

    @FXML
    public void initialize() {
        loadTreeData();

        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String selectedName = newVal.getValue();
            Customer customer = MagazineService.findCustomerByName(selectedName);
            if (customer != null) {
                updateDetailView(customer);
            }
        });
        
        createPaying.setOnAction(e -> showDialog("CreatePayingCustomerView.fxml"));
        createAssociate.setOnAction(e -> showDialog("CreateAssociateCustomerView.fxml"));
        createEnterprise.setOnAction(e -> showDialog("CreateEnterpriseCustomerView.fxml"));
        loadFile.setOnAction(e -> handleLoad());
        editCustomer.setOnAction(e -> handleEdit());
    }

    private void updateDetailView(Customer customer) {
        typeLabel.setText(
            customer instanceof EnterpriseCustomer ? "Enterprise"
          : customer instanceof PayingCustomer    ? "Paying"
          : "Associate"
        );

        nameLabel.setText(customer.getName());
        emailLabel.setText(customer.getEmail());

        // Payment info
        if (customer instanceof PayingCustomer) {
            paymentLabel.setText(((PayingCustomer) customer).getPaymentMethod().toString());
        } else {
            paymentLabel.setText("");
        }

        // Supplements
        StringBuilder supplements = new StringBuilder();
        for (Supplement s : customer.getSupplements()) {
            supplements.append(s.toString()).append("\n");
        }
        supplementsLabel.setText(supplements.toString());

        if (customer instanceof EnterpriseCustomer) {
            EnterpriseCustomer ec = (EnterpriseCustomer) customer;
            EnterpriseCustomer.ContactPerson cp = ec.getContact();

            if (cp != null) {
                contactLabel.setText(cp.getContactDetails());  // e.g., "Jane Doe (jdoe@...)"
            } else {
                contactLabel.setText("N/A");
            }
        } else {
            contactLabel.setText("");
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

    private void loadTreeData() {
        TreeItem<String> root;

        // Use the first magazine as the root label, or fallback if list is empty
        List<Magazine> magazines = MagazineService.getMagazines();
        if (!magazines.isEmpty()) {
            root = new TreeItem<>(magazines.get(0).getName());
        } else {
            root = new TreeItem<>("Magazine Service");
        }

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
}
