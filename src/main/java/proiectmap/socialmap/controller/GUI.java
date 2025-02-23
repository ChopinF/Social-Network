package proiectmap.socialmap.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.repo.db.FriendshipDBRepository;
import proiectmap.socialmap.repo.db.MessagesDBRepository;
import proiectmap.socialmap.repo.db.UserDBRepository;
import proiectmap.socialmap.service.SocialNetwork;
import proiectmap.socialmap.domain.validators.*;

import java.lang.management.PlatformManagedObject;
import java.util.List;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load User view and controller
        FXMLLoader loaderUsers = new FXMLLoader(getClass().getResource("/proiectmap/socialmap/views/users.fxml"));
        AnchorPane usersView = loaderUsers.load();
        UsersController usersController = loaderUsers.getController();

        // Load Friendship view and controller
        FXMLLoader loaderFriendships = new FXMLLoader(getClass().getResource("/proiectmap/socialmap/views/friendships.fxml"));
        AnchorPane friendshipsView = loaderFriendships.load();
        FriendshipsController friendshipsController = loaderFriendships.getController();

        // Load sign up view and controller
        FXMLLoader loaderSignUp = new FXMLLoader(getClass().getResource("/proiectmap/socialmap/views/sign-up.fxml"));
        AnchorPane signupView = loaderSignUp.load();
        SignUpController signUpController = loaderSignUp.getController();

        // Load sign in
        FXMLLoader loaderLogIn = new FXMLLoader(getClass().getResource("/proiectmap/socialmap/views/login.fxml"));
        AnchorPane loginView = loaderLogIn.load();
        LoginController loginController = loaderLogIn.getController();

        // Loader for Add User
        FXMLLoader loaderAddUser = new FXMLLoader(getClass().getResource("/proiectmap/socialmap/views/add-user.fxml"));
        AnchorPane addUserView = loaderAddUser.load();
        AddUserController addUserController = loaderAddUser.getController();

        // Loader for add frienship
        FXMLLoader loaderAddFrienship = new FXMLLoader(getClass().getResource("/proiectmap/socialmap/views/add-friendship.fxml"));
        AnchorPane addFrienshipView = loaderAddFrienship.load();
        AddFriendshipController addFriendshipController = loaderAddFrienship.getController();

        // Create a TabPane to switch between Users and Friendships views
        TabPane tabPane = new TabPane();

        // Create a "Users" tab
        Tab usersTab = new Tab("Users", usersView);
        usersTab.setClosable(false);

        // Create a "Friendships" tab
        Tab friendshipsTab = new Tab("Friendships", friendshipsView);
        friendshipsTab.setClosable(false);

        // Tab for sign up
        Tab signupTab = new Tab("Sign-up", signupView);
        signupTab.setClosable(false);

        // Tab for login
        Tab loginTab = new Tab("Log-In", loginView);
        loginTab.setClosable(false);

        // Tab for add user
        Tab adduserTab = new Tab("Add-user", addUserView);
        adduserTab.setClosable(false);

        // tab for frienship user
        Tab addFrienshipTab = new Tab("Add-frienship", addFrienshipView);
        addFrienshipTab.setClosable(false);

        // Add tabs to the TabPane
      //  tabPane.getTabs().addAll(
      //          usersTab,
      //          friendshipsTab,
      //          signupTab,
      //          loginTab,
      //          adduserTab,
      //          addFrienshipTab
      //  );

         // Dinamic la inceput sa am numai login si sign up
        tabPane.getTabs().addAll(
          signupTab,
          loginTab
        );

        UserValidator userValidator = new UserValidator();
        UserDBRepository userRepo = new UserDBRepository(userValidator);
        FriendshipValidator friendshipValidator = new FriendshipValidator(userRepo);
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(friendshipValidator);
        MessagesDBRepository messagesDBRepository = new MessagesDBRepository(userRepo);
        SocialNetwork socialNetwork = new SocialNetwork(userRepo, friendshipDBRepository, messagesDBRepository);

        usersController.setService(socialNetwork);
        friendshipsController.setService(socialNetwork);
        signUpController.setService(socialNetwork);
        loginController.setService(socialNetwork);
        addUserController.setService(socialNetwork);
        addFriendshipController.setService(socialNetwork);

        loginController.setOnLoginSucces(()->{
            tabPane.getTabs().clear();
            tabPane.getTabs().addAll(
              usersTab,
              friendshipsTab,
              adduserTab,
              addFrienshipTab
            );

            List<Friendship> newFriendRequests = socialNetwork.getNewFriendRequests();

            if(!newFriendRequests.isEmpty()){
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("New Friend Requests");
                    alert.setHeaderText("You have new friend requests!");
                    alert.setContentText("Click 'View' to see your friend requests.");

                    ButtonType viewButton = new ButtonType("View");
                    ButtonType closeButton = new ButtonType("Close");
                    alert.getButtonTypes().setAll(viewButton, closeButton);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == viewButton) {
                            tabPane.getSelectionModel().select(friendshipsTab);
                        }
                    });
                });
            }
        });

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Social Network");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}