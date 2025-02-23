package proiectmap.socialmap.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.service.SocialNetwork;
import proiectmap.socialmap.utils.events.ChangeEventType;
import proiectmap.socialmap.utils.events.FriendshipEvent;
import proiectmap.socialmap.utils.events.UserEvent;

import java.util.Optional;

public class LoginController {
    private Runnable onLoginSuccess;
    @FXML
    private TextField emailField;  // Rename this to emailField

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private SocialNetwork socialNetwork;

    public void setService(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public void setOnLoginSucces(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void handleLogin(){
        String email = emailField.getText().trim();  // Get the email instead of username
        String password = passwordField.getText().trim();

        if(email.isEmpty() || password.isEmpty()){
            showAlert("Validation Error", "Both fields must be filled out.", Alert.AlertType.ERROR);
            return;
        }

        Optional<User> userOptional = findUserByEmailAndPassword(email, password);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            showAlert("Success", "Login successful! Welcome " + user.getFirstName() + "!", Alert.AlertType.INFORMATION);
            socialNetwork.setCurrentUserID(user.getId());
            socialNetwork.notifyObserversFriendship(new FriendshipEvent(ChangeEventType.RELOAD, null));
            socialNetwork.notifyObserversUser(new UserEvent(ChangeEventType.RELOAD, null));
            System.out.println("Login with this ID: " + user.getId());
            if(onLoginSuccess != null){
                onLoginSuccess.run();
            }
        }
        else {
            showAlert("Error", "Invalid email or password.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Find a user by email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return An Optional<User> if the user exists and the password matches.
     */
    private Optional<User> findUserByEmailAndPassword(String email, String password) {
        for (User user : socialNetwork.getUsers()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}