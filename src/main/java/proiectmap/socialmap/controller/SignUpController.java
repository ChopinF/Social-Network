package proiectmap.socialmap.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.service.SocialNetwork;

import java.util.Locale;
import java.util.Optional;

public class SignUpController {

    @FXML
    private TextField idField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signUpButton;

    private SocialNetwork socialNetwork;

    public void setService(SocialNetwork socialNetwork){
        this.socialNetwork = socialNetwork;
    }

    @FXML
    public void handleSignUp(){
        try{
            Long id = Long.parseLong(idField.getText());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled out.", Alert.AlertType.ERROR);
                return;
            }

            if (!email.contains("@")) {
                showAlert("Validation Error", "Invalid email format.", Alert.AlertType.ERROR);
                return;
            }

            User newUser = new User(id, firstName, lastName, email, password);

            Optional<User> userAdded = socialNetwork.addUser(newUser);

            if (userAdded.isPresent()) {
                showAlert("Success", "User registered successfully!", Alert.AlertType.INFORMATION);
                clearFields();
            } else {
                showAlert("Error", "A user with this ID or email already exists.", Alert.AlertType.ERROR);
            }
            // Setam current user-ul la id-ul curent
            socialNetwork.setCurrentUserID(id);

        }
        catch (NumberFormatException e) {
            showAlert("Validation Error", "ID must be a number.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Utility method to clear all fields after successful sign-up
    private void clearFields() {
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
    }
}
