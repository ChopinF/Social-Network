package proiectmap.socialmap.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.service.SocialNetwork;
import proiectmap.socialmap.utils.Observable;
import proiectmap.socialmap.utils.Observer;
import proiectmap.socialmap.utils.events.FriendshipEvent;

import java.time.LocalDate;
import java.util.ArrayList;

public class FriendshipsController implements Observer<FriendshipEvent> {
    @FXML
    private TableView<Friendship> friendshipTableView;

    @FXML
    private TableColumn<Friendship, Long> columnIDuser1;

    @FXML
    private TableColumn<Friendship, Long> columnIDuser2;

    @FXML
    private TableColumn<Friendship, LocalDate> colFriendsFrom;

    @FXML
    private TableColumn<Friendship, String> colStatus;

    @FXML
    private TableColumn<Friendship, Void> colAction; // Updated to Void for buttons

    private SocialNetwork socialNetwork;
    private ObservableList<Friendship> friendshipsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        columnIDuser1.setCellValueFactory(new PropertyValueFactory<>("user1Id"));
        columnIDuser2.setCellValueFactory(new PropertyValueFactory<>("user2Id"));
        colFriendsFrom.setCellValueFactory(new PropertyValueFactory<>("friendsSince"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAction.setCellFactory(getActionCellFactory());

        friendshipTableView.setItems(friendshipsList);
    }

    public void setService(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        if (socialNetwork != null) {
            System.out.println("----------------Am setat service pentru friendships");
        }
        socialNetwork.setCurrentUserID(0L);
        System.out.println(socialNetwork.getCurrentUserID());
        this.socialNetwork.addObserverFriendship(this);
        loadFriendships();
    }

    private void loadFriendships() {
        friendshipsList.clear();
        Long currentId = socialNetwork.getCurrentUserID();
        ArrayList<Friendship> friendships = new ArrayList<>();
        //for (Friendship fr : socialNetwork.getFriendships()) {
        //    if (fr.getUser1Id().equals(currentId) || fr.getUser2Id().equals(currentId)) {
        //        System.out.println(fr);
        //        friendships.add(fr);
        //    }
        //}
        for(Friendship fr : socialNetwork.getFriendships()){
            if ("Active".equals(fr.getStatus()) && (fr.getUser1Id().equals(currentId) || fr.getUser2Id().equals(currentId))) {
                friendships.add(fr);
            } else if ("Pending".equals(fr.getStatus()) && fr.isRecipient(currentId)) {
                friendships.add(fr);
            }
        }
        friendshipsList.addAll(friendships);
    }

    @Override
    public void update(FriendshipEvent event) {
        switch (event.getType()) {
            case ADD:
                Friendship newFriendship = event.getNewFriendship();
                if (!friendshipsList.contains(newFriendship)) {
                    friendshipsList.add(newFriendship);
                }
                break;

            case DELETE:
                Friendship deletedFriendship = event.getOldFriendship();
                friendshipsList.remove(deletedFriendship);
                break;

            case RELOAD:
                loadFriendships();
                break;

            default:
                System.out.println("Unknown event type: " + event.getType());
        }
    }

    private Callback<TableColumn<Friendship, Void>, TableCell<Friendship, Void>> getActionCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Friendship, Void> call(TableColumn<Friendship, Void> param) {
                return new TableCell<>() {
                    private final Button acceptButton = new Button("Accept");
                    private final Button declineButton = new Button("Decline");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox pendingBox = new HBox(5, acceptButton, declineButton);
                    private final HBox activeBox = new HBox(deleteButton);

                    {
                        // Action for "Accept" button
                        acceptButton.setOnAction(event -> {
                            Friendship friendship = getTableView().getItems().get(getIndex());
                            handleAccept(friendship);
                        });

                        // Action for "Decline" button
                        declineButton.setOnAction(event -> {
                            Friendship friendship = getTableView().getItems().get(getIndex());
                            handleDecline(friendship);
                        });

                        // Action for "Delete" button
                        deleteButton.setOnAction(event -> {
                            Friendship friendship = getTableView().getItems().get(getIndex());
                            handleDelete(friendship);
                        });

                        // Styling
                        pendingBox.setStyle("-fx-alignment: center;");
                        activeBox.setStyle("-fx-alignment: center;");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableView().getItems().get(getIndex()) == null) {
                            setGraphic(null);
                        } else {
                            Friendship friendship = getTableView().getItems().get(getIndex());
                            Long currentUserId = socialNetwork.getCurrentUserID();

                            if ("Pending".equals(friendship.getStatus()) && friendship.isRecipient(currentUserId)) {
                                setGraphic(pendingBox); // Show Accept/Decline buttons only for recipient
                            } else if ("Active".equals(friendship.getStatus())) {
                                setGraphic(activeBox); // Show Delete button for Active friendships
                            } else {
                                setGraphic(null); // No actions for others
                            }
                        }
                    }
                };
            }
        };
    }

    // Handle "Delete" action
    private void handleDelete(Friendship friendship) {
        System.out.println("Deleted friendship: " + friendship);
        socialNetwork.deleteFriendship(friendship); // Assuming this method exists in SocialNetwork
        friendshipsList.remove(friendship); // Remove from the table
    }

    // Handle "Accept" action
    private void handleAccept(Friendship friendship) {
        System.out.println("Accepted friendship: " + friendship);
        friendship.setStatus("Active");
        friendship.setFriendsSince(LocalDate.now());
        socialNetwork.updateFriendship(friendship); // Assuming this method exists in SocialNetwork
        friendshipTableView.refresh();
    }

    // Handle "Decline" action
    private void handleDecline(Friendship friendship) {
        System.out.println("Declined friendship: " + friendship);
        socialNetwork.deleteFriendship(friendship); // Assuming this method exists in SocialNetwork
        friendshipsList.remove(friendship);
    }
}