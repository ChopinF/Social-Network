package proiectmap.socialmap.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.domain.Message;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.service.SocialNetwork;
import proiectmap.socialmap.utils.Observer;
import proiectmap.socialmap.utils.events.UserEvent;
import proiectmap.socialmap.utils.events.ChangeEventType;
import proiectmap.socialmap.utils.paging.Page;
import proiectmap.socialmap.utils.paging.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersController implements Observer<UserEvent> {
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Long> tableUserId;
    @FXML
    private TableColumn<User, String>tableUserFirstName;
    @FXML
    private TableColumn<User, String>tableUserLastName;

    private SocialNetwork socialNetwork;
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    private Button prevButtonUsers;
    @FXML
    private Button nextButtonUsers;

    @FXML
    private Button nextButtonFriendships;
    @FXML
    private Button prevButtonFriendships;

    @FXML
    private TableView<User> friendshipsTableView;
    @FXML
    private TableColumn<User, Long> friendshipUserId;
    @FXML
    private TableColumn<User, String> friendshipFirstName;
    @FXML
    private TableColumn<User, String> friendshipLastName;

    private ObservableList<User> friendshipList = FXCollections.observableArrayList();

    // Lab paginare
    private int pageSize = 5;
    private int currentPageUsers = 0;
    private int totalNumberOfElementsUsers = 0;

    private int pageSizeFriendships = 3;
    private int currentPageFriendships = 0;
    private int totalNumberOfElementsFriendships = 0;

    @FXML
    public void initialize(){
        tableUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableUserFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableUserLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));


        tableView.setItems(userList);

        friendshipUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        friendshipFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendshipLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendshipsTableView.setItems(friendshipList);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showFriendshipsForUser(newSelection);
            }
        });

        prevButtonUsers.setOnAction(e -> loadPageUsers(currentPageUsers - 1));
        nextButtonUsers.setOnAction(e -> loadPageUsers(currentPageUsers + 1));

        prevButtonFriendships.setOnAction(e -> {
            if (currentPageFriendships > 0) {
                User selectedUser = tableView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    --currentPageFriendships;
                    loadPageFriendships(currentPageFriendships, selectedUser.getId());
                }
            }
        });

        nextButtonFriendships.setOnAction(e -> {
            int totalPages = (int) Math.ceil((double) totalNumberOfElementsFriendships / pageSizeFriendships);
            if (currentPageFriendships < totalPages - 1) {
                User selectedUser = tableView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    ++currentPageFriendships;
                    loadPageFriendships(currentPageFriendships, selectedUser.getId());
                }
            }
        });
    }

    private void showFriendshipsForUser(User selectedUser){
        friendshipList.clear();

        try{
            List<User> friendships = socialNetwork.getFriendships(selectedUser.getId());

            totalNumberOfElementsFriendships = friendships.size();
            loadPageFriendships(0, selectedUser.getId());
        }
        catch(Exception e){
            showAlert("Error", "Could not load friendships: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    //private void showFriendshipsForUser(User selectedUser) {
    //    friendshipList.clear();
    //    try {
    //        friendshipList.addAll(socialNetwork.getFriendships(selectedUser.getId()));
    //    } catch (Exception e) {
    //        showAlert("Error", "Could not load friendships: " + e.getMessage(), Alert.AlertType.ERROR);
    //    }
    //}

    //private void loadPageFriendships(int page, Long userId){
    //    List<User> friendships = socialNetwork.getFriendships(userId);
    //    totalNumberOfElementsFriendships = friendships.size();

    //    int totalPages = (int) Math.ceil((double) totalNumberOfElementsFriendships / pageSizeFriendships);

    //    if (page < 0 || page >= totalPages) {
    //        return;
    //    }

    //    currentPageFriendships = page;

    //    int startIndex = currentPageFriendships * pageSizeFriendships;
    //    int endIndex = Math.min(startIndex + pageSizeFriendships, totalNumberOfElementsFriendships);

    //    friendshipList.clear();
    //    List<User> frUsers = friendships.subList(startIndex, endIndex);

    //    for(User u : frUsers){
    //        System.out.println(u);
    //    }

    //    friendshipList.addAll(friendships.subList(startIndex, endIndex));

    //    prevButtonFriendships.setDisable(currentPageFriendships == 0);
    //    nextButtonFriendships.setDisable(currentPageFriendships == totalPages - 1);
    //}

    private void loadPageFriendships(int page, Long userId){
        Pageable pageable = new Pageable(page, pageSizeFriendships);

        Page<User> userPage = socialNetwork.friendshipPage(pageable, userId);

        friendshipList.clear();

        ArrayList<User> usersOnPage = new ArrayList<>();
        for(User u : userPage.getElementsOnPage()){
            usersOnPage.add(u);
        }
        friendshipList.addAll(usersOnPage);
        //totalNumberOfElementsFriendships = userPage.getTotalNumberOfElements();
        ArrayList<User> inTotal = new ArrayList<>(this.socialNetwork.getFriendships(userId));
        totalNumberOfElementsFriendships = inTotal.size();

        int totalPages = (int) Math.ceil((double) totalNumberOfElementsFriendships / pageSizeFriendships);

        System.out.println(totalPages);
        System.out.println(currentPageFriendships);

        prevButtonFriendships.setDisable(currentPageFriendships == 0);
        nextButtonFriendships.setDisable(currentPageFriendships == totalPages - 1);

        currentPageFriendships = page;
    }

    @FXML
    private void handlePreviousPageFriendships() {
        if (currentPageFriendships > 0) {
            User selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                currentPageFriendships--;
                loadPageFriendships(currentPageFriendships, selectedUser.getId());
            }
        }
    }

    @FXML
    private void handleNextPageFriendships() {
        int totalPages = (int) Math.ceil((double) totalNumberOfElementsFriendships / pageSizeFriendships);
        if (currentPageFriendships < totalPages - 1) {
            User selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                ++currentPageFriendships;
                loadPageFriendships(currentPageFriendships, selectedUser.getId());
            }
        }
    }

    public void setService(SocialNetwork socialNetwork){
        this.socialNetwork = socialNetwork;
        this.socialNetwork.addObserverUser(this);
        //System.out.println(" --------- Am setat service pentru users");
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        ArrayList<User> users = new ArrayList<>();
        Long currentUserId = socialNetwork.getCurrentUserID();
        System.out.println("Current User ID: " + currentUserId);

        for (User u : socialNetwork.getUsers()) {
            if (!Objects.equals(u.getId(), currentUserId)) {
                //System.out.println(u);
                users.add(u);
            }
        }
        totalNumberOfElementsUsers = users.size();
        loadPageUsers(0); // we load the first page
        //userList.addAll(users);
    }

    private void loadPageUsers(int page) {
        int totalPages = (int) Math.ceil((double) totalNumberOfElementsUsers / pageSize);

        if (page < 0 || page >= totalPages) {
            return;
        }

        currentPageUsers = page;

        int startIndex = currentPageUsers * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalNumberOfElementsUsers);

        List<User> usersForPage = new ArrayList<>();

        int index = 0;
        for (User u : socialNetwork.getUsers()) {
            if (index >= startIndex && index < endIndex) {
                usersForPage.add(u);
            }
            index++;
            if (index >= endIndex) {
                break;
            }
        }

        userList.clear();
        userList.addAll(usersForPage);

        prevButtonUsers.setDisable(currentPageUsers == 0);
        nextButtonUsers.setDisable(currentPageUsers == totalPages - 1);
    }

    @FXML
    private void handlePreviousPageUsers(){
        if(currentPageUsers > 0){
            loadPageUsers(currentPageUsers - 1);
        }
    }

    @FXML
    private void handleNextPageUsers(){
        if (currentPageUsers < Math.ceil((double) totalNumberOfElementsUsers / pageSize) - 1) {
            loadPageUsers(currentPageUsers + 1);
        }
    }


    @Override
    public void update(UserEvent userEvent) {
        if (userEvent.getType() == ChangeEventType.ADD) {
            System.out.println("User added: " + userEvent.getNewUser());
            loadUsers();
        } else if (userEvent.getType() == ChangeEventType.DELETE) {
            System.out.println("User deleted: " + userEvent.getOldUser());
            loadUsers();
        }
        else if(userEvent.getType() == ChangeEventType.RELOAD){
            loadUsers();
        }
    }

    @FXML
    public void handleDeleteUser(){
        User selectedUser = tableView.getSelectionModel().getSelectedItem();

        if(selectedUser != null){
            boolean confirmation = showConfirmationAlert("Delete User", "Are you sure you want to delete this user?");
            if(confirmation){
                socialNetwork.removeUser(selectedUser.getId());
                tableView.getItems().remove(selectedUser);
                showAlert("Success", "User successfully deleted!", Alert.AlertType.INFORMATION);
            }
            else {
                showAlert("Error", "Please select a user to delete.", Alert.AlertType.ERROR);
            }
        }
    }

    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
        return alert.getResult().getText().equalsIgnoreCase("OK");
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleAddFriendship(){
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null){
        try {
                Long currentId = socialNetwork.getCurrentUserID();
                Long selectedId = selectedUser.getId();
                Friendship friendship = new Friendship(currentId, selectedId, LocalDate.now(), "Pending");
                socialNetwork.addFriendship(friendship);
                showAlert("Success", "Friend request sent successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception e){
                showAlert("Error", "Could not send friend request: " + e.getMessage(), Alert.AlertType.ERROR);        }
              }
        else {
            showAlert("Error", "Please select a user to send a friend request.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onSendMessageToSelected(){
        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        List<User> selectedUsers = tableView.getSelectionModel().getSelectedItems();
        if(selectedUsers.isEmpty()){
            showAlert("Error", "No users selected", Alert.AlertType.ERROR);
            return;
        }

        Stage messageStage = new Stage();
        VBox layout = new VBox(pageSize);
        TextArea messageField = new TextArea();
        messageField.setPromptText("Enter your message here: ");
        Button sendButton = new Button("Send");
        layout.getChildren().addAll(messageField, sendButton);

        sendButton.setOnAction(e -> {
            String messageText = messageField.getText().trim();
            if(messageText.isEmpty()){
                showAlert("Error", "Message cannot be empty", Alert.AlertType.ERROR);
                return;
            }

            for(User user : selectedUsers){
                Message message = new Message(
                    null,
                    this.socialNetwork.findUser(socialNetwork.getCurrentUserID()),
                    List.of(user),
                    messageText,
                        LocalDateTime.now()
                );
                System.out.println(message);
                socialNetwork.sendMessage(message);
            }

            showAlert("Success", "Message sent successfully", Alert.AlertType.INFORMATION);
            messageStage.close();
        });
        messageStage.setScene(new Scene(layout, 300, 200));
        messageStage.show();
    }

    @FXML
    private void onOpenChatWithSelected(){
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            showAlert("Error", "No user selected", Alert.AlertType.ERROR);
            return;
        }

        Stage chatStage = new Stage();
        VBox chatLayout = new VBox(10);
        ListView<String> chatListView = new ListView<>();
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message...");
        Button sendButton = new Button("Send");
        chatLayout.getChildren().addAll(chatListView, messageField, sendButton);

        User currentUser = this.socialNetwork.findUser(this.socialNetwork.getCurrentUserID());
        List<Message> conversation = socialNetwork.getConversation(currentUser , selectedUser);

        System.out.println("Userul curent: " + currentUser);
        System.out.println("Userul cu care deschid chat: " + selectedUser);

        for(Message msg : conversation){
            chatListView.getItems().add("User: {" + msg.getFrom().getFirstName() + "} : " + msg.getMessage());
        }

        sendButton.setOnAction(e -> {
            String messageText = messageField.getText().trim();

            if(messageText.isEmpty()){
                showAlert("Error", "Message cannot be empty", Alert.AlertType.ERROR);
                return;
            }

            Message newMessage = new Message(
              null,
              socialNetwork.findUser(socialNetwork.getCurrentUserID()),
              List.of(selectedUser),
              messageText,
              LocalDateTime.now()
            );

            socialNetwork.sendMessage(newMessage);
            chatListView.getItems().add(newMessage.toString());
            messageField.clear();;
        });

        chatStage.setScene(new Scene(chatLayout, 400, 300));
        chatStage.setTitle("Chat with " + selectedUser.getEmail());
        chatStage.show();
    }
}
