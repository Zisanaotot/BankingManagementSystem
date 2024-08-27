package coe528_project;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;
import javafx.stage.Modality;
import java.util.ArrayList;
import javafx.event.ActionEvent;

public class BankApplication extends Application {
    
    private VBox mainLayout; // Declare mainLayout as a class-level variable
    private StackPane mainLoginPage; // Declare managerLoginPage as a class-level variable
    private Customer currentCustomer;
    private ArrayList<Customer> customers = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        
        mainLayout = new VBox(21);
        mainLoginPage = new StackPane();

        // Customer/Manager Login Page
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        loginGrid.add(new Label("Username:"), 0, 0);
        loginGrid.add(usernameField, 1, 0);
        loginGrid.add(new Label("Password:"), 0, 1);
        loginGrid.add(passwordField, 1, 1);
        loginGrid.add(loginButton, 1, 2);

        mainLoginPage.getChildren().add(loginGrid);

        // Manager Dashboard
        VBox managerDashboard = new VBox(10);
        managerDashboard.setAlignment(Pos.CENTER);
        Button logoutButton = new Button("Logout");
        Button addCustomerButton = new Button("Add Customer");
        Button deleteCustomerButton = new Button("Delete Customer");
        managerDashboard.getChildren().addAll(new Label("Manager Dashboard"), addCustomerButton, deleteCustomerButton, logoutButton);
        
        // Customer Dashboard
        VBox customerDashboard = new VBox(10);
        customerDashboard.setAlignment(Pos.CENTER);
        Button customerlogoutButton = new Button("Logout");
        Label welcomeLabel = new Label();
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Label customerLevel = new Label();
        Label accountBalance = new Label();
        Button onlinePurchaseButton = new Button("Online Purchase");
        
        
        
        customerDashboard.getChildren().addAll(welcomeLabel, customerLevel, accountBalance, depositButton, withdrawButton, onlinePurchaseButton, customerlogoutButton);

        // Add action for the logout button
        logoutButton.setOnAction(e -> logout());
        customerlogoutButton.setOnAction(e -> logout());
        
        // Main Scene
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(new Label("Login Page"), mainLoginPage);

        Scene scene = new Scene(mainLayout, 450, 450);

        // Main page Login Action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            currentCustomer = getCustomer(username, password);
            if (authenticateManager(username, password)) {
                // Show manager dashboard
                mainLayout.getChildren().clear();
                mainLayout.getChildren().add(managerDashboard);
            } else if (currentCustomer != null){
                // Show customer dashboard
                welcomeLabel.setText("Welcome, " + currentCustomer.getUsername() + "!");
                customerLevel.setText("Account Level: " + currentCustomer.getLevel().toString());
                accountBalance.setText("Account balance: $" + currentCustomer.getBalance());
                mainLayout.getChildren().clear();
                mainLayout.getChildren().add(customerDashboard);
            } else {
                display("Login Failed", "Invalid username or password.");
            }
        });
        
        // Online Purchase Action
        onlinePurchaseButton.setOnAction((ActionEvent e) -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Online Purchase");
            dialog.setHeaderText("Enter the item name and cost:");

            // Create text fields for item name and cost
            TextField itemName = new TextField();
            // itemName.setPromptText("Item Name");
            TextField itemCost = new TextField();
            // itemCost.setPromptText("Item Cost");

            // Add item name and cost fields to the dialog's content
            GridPane grid = new GridPane();
            grid.add(new Label("Item Name:"), 0, 0);
            grid.add(itemName, 1, 0);
            grid.add(new Label("Item Cost ($):"), 0, 1);
            grid.add(itemCost, 1, 1);

            dialog.getDialogPane().setContent(grid);

            // Show the dialog and handle the result
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                // Retrieve the entered item name and cost
                String name = itemName.getText().trim();
                String costText = itemCost.getText().trim();

                try {
                    double cost = Double.parseDouble(costText);
                    
                    if (currentCustomer.getBalance() > currentCustomer.getLevel().calculateFee(cost) && cost > 0){
                        currentCustomer.withdraw(currentCustomer.getLevel().calculateFee(cost));
                        display("Success", "Item succesfully purchased, $" + currentCustomer.getLevel().calculateFee(cost) + " has been withdrawn from your account");
                        customerLevel.setText("Account Level: " + currentCustomer.getLevel().toString());
                        accountBalance.setText("Account balance: $" + currentCustomer.getBalance());
                    }
                    else if (cost < 0){
                        display("ERROR", "Invalid item cost");
                    }
                    else if (currentCustomer.getBalance() < currentCustomer.getLevel().calculateFee(cost)){
                        display("ERROR", "Insufficient funds in your balance");
                    }
                } catch (NumberFormatException ex) {
                    // Handle case where item cost is not a valid double
                    display("ERROR", "Item cost is invalid");
                }
            }
        });

        // Deposit Action
        depositButton.setOnAction((ActionEvent e) -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Deposit funds");
            dialog.setContentText("Enter amount to deposit: $");
            
            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                try {
                    double depositAmount = Double.parseDouble(result.get());
                    if (depositAmount > 0){
                        currentCustomer.deposit(depositAmount);
                        customerLevel.setText("Account Level: " + currentCustomer.getLevel().toString());
                        accountBalance.setText("Account balance: $" + currentCustomer.getBalance());
                    }
                    else {
                        display("ERROR", "Deposit amount must be greater than 0");
                    }
                } catch (NumberFormatException ex) {
                    // Handle case where user input is not a valid double
                    display("ERROR", "Deposit amount not valid");
                }
            }
        });

        // Withdraw Action
        withdrawButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Withdraw funds");
            dialog.setContentText("Enter amount to withdraw: $");
            
            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                try {
                    double withdrawAmount = Double.parseDouble(result.get());
                    if (withdrawAmount > 0 && currentCustomer.getBalance() > withdrawAmount){
                        currentCustomer.withdraw(withdrawAmount);
                        customerLevel.setText("Account Level: " + currentCustomer.getLevel().toString());
                        accountBalance.setText("Account balance: $" + currentCustomer.getBalance());
                    }
                    else {
                        display("ERROR", "Withdraw amount not valid");
                    }
                } catch (NumberFormatException ex) {
                    // Handle case where user input is not a valid double
                    display("ERROR", "Withdraw amount not valid");
                }
            }
            
        });

        // Add Customer Action 
        addCustomerButton.setOnAction(e -> {
            // Create a dialog for entering customer details
            Dialog<Customer> dialog = new Dialog<>();
            dialog.setTitle("Add Customer");
            dialog.setHeaderText("Enter customer details");

            // Set the button types
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            // Create fields for entering customer details
            TextField newUsername = new TextField(); // Declare usernameField here
            PasswordField newPassword = new PasswordField();
            TextField balanceField = new TextField();
            Label errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: red");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(new Label("Username:"), 0, 0);
            grid.add(newUsername, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(newPassword, 1, 1);
            grid.add(new Label("Initial Balance:"), 0, 2);
            grid.add(balanceField, 1, 2);
            grid.add(errorLabel, 0, 3, 2, 1);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to a customer object when add button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        String username = newUsername.getText().trim();
                        String password = newPassword.getText().trim();
                        double balance = Double.parseDouble(balanceField.getText().trim());
                        if (balance < 100) {
                            display("Error", "Balance must be a minimum of $100");
                            return null;
                        }
                        if (isUsernameTaken(username)){
                            display("Error", "Username is taken, please use a different username.");
                            return null; // Prevent the dialog from closing
                        }
                        if (password.length() < 6){
                            display("Error", "Password must be at least 6 characters");
                            return null;
                        }
                        return new Customer(username, password, balance);
                        
                    } catch (NumberFormatException ex) {
                        display("Error", "Invalid Balance");
                        return null;
                    }
                }
                return null;
            });

            // Show the dialog and handle the result
            dialog.showAndWait().ifPresent(customer -> {
                if (customer != null) {
                    // You can add the new customer to your data structure or file here
                    createCustomerFile(customer);
                }
            });
        });

        // Delete Customer Action
        deleteCustomerButton.setOnAction(e -> {
            // Create a dialog for entering the username of the customer to be deleted
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Customer");
            dialog.setHeaderText("Enter the username of the customer to delete:");
            dialog.setContentText("Username:");

            // Show the dialog and wait for user input
            Optional<String> result = dialog.showAndWait();

            // Process the user input
            result.ifPresent(username -> {
                // Define the directory where customer files are stored
                String directoryPath = "customerDirectory/";

                // Create a File object for the directory
                File directory = new File(directoryPath);

                // Check if the directory exists
                if (!directory.exists() || !directory.isDirectory()) {
                    display("Error", "Customer directory not found.");
                    return; // Exit method
                }

                // Define the filename of the customer's file
                String filename = directoryPath + username + ".txt";

                // Create a File object for the customer's file
                File file = new File(filename);

                // Check if the file exists
                if (file.exists()) {
                    // Attempt to delete the file
                    if (file.delete()) {
                        display("Success", "Customer " + username + " deleted successfully.");
                    } else {
                        display("Error", "Failed to delete customer " + username + ".");
                    }
                } else {
                    display("Error", "Customer " + username + " not found.");
                }
            });
        });
        
        // Set the scene to the primaryStage
        primaryStage.setScene(scene);

        // Show the primaryStage
        primaryStage.show();
    }
    
    // Method to handle logout
    public void logout() {
        // Clear the main layout
        mainLayout.getChildren().clear();

        // Show the login page again
        mainLayout.getChildren().addAll(new Label("Login Page"), mainLoginPage);
        currentCustomer = null;
    }
    
    public void createCustomerFile(Customer customer) {
        customers.add(customer);
        String username = customer.getUsername();
        String folderPath = "customerDirectory/";
        String filename = folderPath + File.separator + username + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Username: " + customer.getUsername());
            writer.println("Password: " + customer.getPassword());
            writer.println("Balance: " + customer.getBalance());
            display("Customer", "Customer file created!");
        } catch (IOException e) {
            display("Error", "Error creating customer file: " + e.getMessage());
        }
    }

    // Method to authenticate manager
    private boolean authenticateManager(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }
    
    
    public boolean authenticateCustomer(String username, String password) {
        // Define the directory where customer files are stored
        String directoryPath = "customerDirectory/";

        // Create a File object for the directory
        File directory = new File(directoryPath);
        
        if (!directory.exists()) {
            directory.mkdirs(); // This will create all necessary parent directories
        }

        // Get all files in the directory
        File[] files = directory.listFiles();

        // Iterate through each file in the directory
        for (File file : files) {
            // Check if the file is a regular file (not a directory)
            if (file.isFile()) {
                String filename = file.getName();
                // Ensure filename is not null
                if (filename != null && filename.endsWith(".txt")) {
                    // Extract the username from the filename (remove the ".txt" extension)
                    String customerUsername = filename.substring(0, filename.lastIndexOf('.'));
                    if (customerUsername.equals(username)) {
                    // Found a matching customer file
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            // Read the password from the file
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("Password:")) {
                                    // Extract the password from the line
                                    String filePassword = line.substring("Password:".length()).trim();
                                    // Check if the passwords match
                                    return password.equals(filePassword); // Authentication successful
                                    // Invalid password
                                }
                            }
                            // Password not found
                            return false;
                        } catch (IOException e) {
                            return false;
                        }
                    }
                }
            }
        }
        // Username not found
        return false;
    }
    
    public Customer getCustomer(String username, String password){
        for (int i=0; i < customers.size(); i++){
            if (customers.get(i).getUsername().equals(username) && customers.get(i).getPassword().equals(password)){
                return customers.get(i);
            }
        }
        return null;
    }
    
    // Method to check if the username already exists
    public boolean isUsernameTaken(String username) {
        // Define the directory where customer files are stored
        String directoryPath = "customerDirectory/";

        // Create a File object for the directory
        File directory = new File(directoryPath);

        // Check if the directory exists
        if (!directory.exists() || !directory.isDirectory()) {
            return false; // Directory does not exist, username not taken
        }

        // Get all files in the directory
        File[] files = directory.listFiles();

        // Iterate through each file in the directory
        for (File file : files) {
            // Check if the file is a regular file (not a directory)
            if (file.isFile()) {
                String filename = file.getName();
                // Extract the username from the filename (remove the ".txt" extension)
                String customerUsername = filename.substring(0, filename.lastIndexOf('.'));
                // Inside the for loop after checking if the username exists
                if (customerUsername.equalsIgnoreCase(username)) {
                    // Username exists, set error message
                    return true; // Username exists
                }
            }
        }
        return false; // Username not found
    }


    public static void display(String title, String message){
        Stage window = new Stage();
        
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        window.setMinHeight(200);
        
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> window.close());
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
