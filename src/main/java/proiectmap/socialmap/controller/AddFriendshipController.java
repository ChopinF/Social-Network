package proiectmap.socialmap.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.service.SocialNetwork;

import java.time.LocalDate;

public class AddFriendshipController {

    @FXML
    private TextField textFieldUser1Id;

    @FXML
    private TextField textFieldUser2Id;

    @FXML
    private DatePicker datePickerFriendsSince;

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
    public void handleSaveFriendship() {
        try {
            String user1IdText = textFieldUser1Id.getText().trim();
            String user2IdText = textFieldUser2Id.getText().trim();
            LocalDate friendsSinceDate = datePickerFriendsSince.getValue();

            if (user1IdText.isEmpty() || user2IdText.isEmpty() || friendsSinceDate == null) {
                showAlert("Validation Error", "All fields are required.", Alert.AlertType.ERROR);
                return;
            }

            Long user1Id, user2Id;
            try {
                user1Id = Long.parseLong(user1IdText);
                user2Id = Long.parseLong(user2IdText);
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "User IDs must be numeric.", Alert.AlertType.ERROR);
                return;
            }

            Friendship friendship = new Friendship(user1Id, user2Id, friendsSinceDate, "Pending");

            socialNetwork.addFriendship(friendship);
            showAlert("Success", "Friendship added successfully!", Alert.AlertType.INFORMATION);

            clearFields();

        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the Cancel button click event.
     */
    @FXML
    public void handleCancelFriendship() {
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
     * Clears all input fields.
     */
    private void clearFields() {
        textFieldUser1Id.clear();
        textFieldUser2Id.clear();
        datePickerFriendsSince.setValue(null);
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.close();
    }
}
