package proiectmap.socialmap.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.service.SocialNetwork;

public class AddUserController {

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldLastName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private PasswordField textFieldPassword;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    private SocialNetwork socialNetwork;

    /**
     * Sets the service for this controller.
     * @param socialNetwork the SocialNetwork service instance
     */
    public void setService(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    /**
     * Handles the Save button click event.
     */
    @FXML
    public void handleSave() {
        try {
            String idText = textFieldId.getText().trim();
            String firstName = textFieldFirstName.getText().trim();
            String lastName = textFieldLastName.getText().trim();
            String email = textFieldEmail.getText().trim();
            String password = textFieldPassword.getText().trim();

            if (idText.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Validation Error", "All fields are required.", Alert.AlertType.ERROR);
                return;
            }

            Long id;
            try {
                id = Long.parseLong(idText);
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "ID must be a number.", Alert.AlertType.ERROR);
                return;
            }

            User user = new User(id, firstName, lastName, email, password);

            socialNetwork.addUser(user);
            showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);

            clearFields();
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        textFieldId.clear();
        textFieldFirstName.clear();
        textFieldLastName.clear();
        textFieldEmail.clear();
        textFieldPassword.clear();
    }


    /**
     * Handles the Cancel button click event.
     */
    @FXML
    public void handleCancel() {
        closeWindow();
    }

    /**
     * Displays an alert dialog.
     * @param title the title of the alert
     * @param message the message to display
     * @param alertType the type of alert
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.close();
    }
}