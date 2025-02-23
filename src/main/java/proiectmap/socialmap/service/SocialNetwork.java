package proiectmap.socialmap.service;


import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.domain.Message;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.domain.validators.ValidationException;
import proiectmap.socialmap.repo.db.FriendshipDBRepository;
import proiectmap.socialmap.repo.db.UserDBRepository;
import proiectmap.socialmap.repo.db.MessagesDBRepository;
import proiectmap.socialmap.utils.*;
import proiectmap.socialmap.utils.Observable;
import proiectmap.socialmap.utils.Observer;
import proiectmap.socialmap.utils.events.ChangeEventType;
import proiectmap.socialmap.utils.events.FriendshipEvent;
import proiectmap.socialmap.utils.events.MessageEvent;
import proiectmap.socialmap.utils.events.UserEvent;
import proiectmap.socialmap.utils.events.MessageEvent;
import proiectmap.socialmap.utils.paging.Page;
import proiectmap.socialmap.utils.paging.Pageable;

import java.lang.reflect.Array;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//public class SocialNetwork implements Observable<UserEvent>, Observable<FriendshipEvent> {
public class SocialNetwork implements UserObservable, FriendshipObservable, MessageObservable {

    private final UserDBRepository repositoryUser;
    private final FriendshipDBRepository repositoryFriendship;
    private final MessagesDBRepository repositoryMessages;

    private final List<Observer<UserEvent>> observersUsers;
    private final List<Observer<FriendshipEvent>> observersFriendships;
    private final List<Observer<MessageEvent>> observersMessages;

    private Long currentUserID;

    public SocialNetwork(UserDBRepository repositoryUser, FriendshipDBRepository repositoryFriendship, MessagesDBRepository messagesDBRepository) {
        this.repositoryUser = repositoryUser;
        this.repositoryFriendship = repositoryFriendship;
        this.repositoryMessages = messagesDBRepository;
        this.observersUsers = new ArrayList<>();
        this.observersFriendships = new ArrayList<>();
        this.observersMessages = new ArrayList<>();
    }

    public Long getCurrentUserID(){
        return this.currentUserID;
    }
    public void setCurrentUserID(Long id){
        this.currentUserID = id;
    }

    // ------------------- Message Operations -------------------

    /**
     * Retrieve the conversation between two users.
     *
     * @param user1 The first user (usually the current user).
     * @param user2 The second user (the selected user).
     * @return A list of messages exchanged between the two users, sorted by timestamp.
     */
    //public List<Message> getConversation(User user1, User user2) {
    //    // Validate the users
    //    if (user1 == null || user2 == null) {
    //        throw new ValidationException("Users cannot be null!");
    //    }
    //    if (repositoryUser.findOne(user1.getId()).isEmpty() || repositoryUser.findOne(user2.getId()).isEmpty()) {
    //        throw new ValidationException("One or both users do not exist!");
    //    }

    //    List<Message> conversation = new ArrayList<>();
    //    for (Message message : repositoryMessages.findAll()) {
    //        //boolean isFromUser1ToUser2 = message.getFrom().equals(user1.getId()) && message.getTo().contains(user2.getId());
    //        //boolean isFromUser2ToUser1 = message.getFrom().equals(user2.getId()) && message.getTo().contains(user1.getId());

    //        //if (isFromUser1ToUser2 || isFromUser2ToUser1) {
    //        //    conversation.add(message);
    //       // }
    //        System.out.println(message);
    //        if(message.getFrom().equals(user1) && message.getTo().contains(user2))
    //            conversation.add(message);
    //        if(message.getFrom().equals(user2) && message.getTo().contains(user1))
    //            conversation.add(message);
    //    }

    //    conversation.sort(Comparator.comparing(Message::getData));
    //    return conversation;
    //}

    public List<Message> getConversation(User user1, User user2) {
        // Validate the users
        if (user1 == null || user2 == null) {
            throw new ValidationException("Users cannot be null!");
        }

        if (repositoryUser.findOne(user1.getId()).isEmpty() || repositoryUser.findOne(user2.getId()).isEmpty()) {
            throw new ValidationException("One or both users do not exist!");
        }

        return StreamSupport.stream(repositoryMessages.findAll().spliterator(), false)
                .filter(message ->
                        (message.getFrom().equals(user1) && message.getTo().contains(user2)) ||
                                (message.getFrom().equals(user2) && message.getTo().contains(user1))
                )
                .sorted(Comparator.comparing(Message::getData))
                .toList();
    }

    /**
     * Sends a message from the current user to one or more recipients.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(Message message) {
        if (message == null) {
            throw new ValidationException("Message cannot be null!");
        }
        if (message.getFrom() == null || message.getTo() == null || message.getTo().isEmpty()) {
            throw new ValidationException("Message must have a sender and at least one recipient!");
        }
        if (message.getMessage() == null || message.getMessage().isBlank()) {
            throw new ValidationException("Message text cannot be empty!");
        }

        if (repositoryUser.findOne(message.getFrom().getId()).isEmpty()) {
            throw new ValidationException("Sender does not exist!");
        }

        //for (User recipient : message.getTo()) {
        //    if (repositoryUser.findOne(recipient.getId()).isEmpty()) {
        //        throw new ValidationException("Recipient does not exist: " + recipient.getId());
        //    }
        //}

        if (message.getTo().stream().anyMatch(recipient -> repositoryUser.findOne(recipient.getId()).isEmpty())) {
            throw new ValidationException(
                    "One or more recipients do not exist: " +
                            message.getTo().stream()
                                    .filter(recipient -> repositoryUser.findOne(recipient.getId()).isEmpty())
                                    .map(User::getId)
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "))
            );
        }

        message.setId(getNewMessageId());

        repositoryMessages.save(message);

        notifyObserversMessage(new MessageEvent(ChangeEventType.ADD, message));
    }

    /**
     * Generates a new unique ID for a message.
     *
     * @return A new unique message ID.
     */
    private Long getNewMessageId() {
        Long maxId = 0L;
        for (Message message : repositoryMessages.findAll()) {
            if (message.getId() > maxId) {
                maxId = message.getId();
            }
        }
        return maxId + 1;
    }

    /**
     * Delete a message by ID.
     *
     * @param messageId ID of the message to delete.
     * @return The deleted message.
     */
    public Optional<Message> deleteMessage(Long messageId) {
        return repositoryMessages.delete(messageId);
    }


    /**
     * @return all Users from the social network
     */
    public Iterable<User> getUsers() {
        return repositoryUser.findAll();
    }

    /**
     * @param id Id of the user that needs to be found
     * @return User
     */
    public User findUser(Long id) {
        return repositoryUser.findOne(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
    }

    /**
     * @param user Adds the user to the map.repository
     */
    public Optional<User> addUser(User user) {
        Optional<User> newUser = repositoryUser.save(user);
        newUser.ifPresent(savedUser -> notifyObserversUser(new UserEvent(ChangeEventType.ADD, savedUser)));
        return newUser;
    }
    /**
     * @return All friendships from map.repository
     */
    public Iterable<Friendship> getFriendships() {
        return repositoryFriendship.findAll();
    }

    //public List<User> getFriendships(Long userId) {
    //    ArrayList<Long> ids = new ArrayList<>();
    //    for(Friendship friendship : repositoryFriendship.findAll()){
    //        if(friendship.getUser1Id().equals(userId) && friendship.getStatus().equals("Active"))ids.add(friendship.getUser2Id());
    //        if(friendship.getUser2Id().equals(userId) && friendship.getStatus().equals("Active"))ids.add(friendship.getUser1Id());
    //    }
    //    ArrayList<User> users = new ArrayList<>();
    //    for(Long id : ids){
    //        users.add(repositoryUser.findOne(id).orElse(null));
    //    }
    //    return users;
    //}

    public List<User> getFriendships(Long userId) {
        List<Long> friendIds = StreamSupport.stream(repositoryFriendship.findAll().spliterator(), false)
                .filter(friendship ->
                        friendship.getStatus().equals("Active") &&
                                (friendship.getUser1Id().equals(userId) || friendship.getUser2Id().equals(userId))
                )
                .map(friendship -> friendship.getUser1Id().equals(userId) ? friendship.getUser2Id() : friendship.getUser1Id())
                .toList();

        return friendIds.stream()
                .map(id -> repositoryUser.findOne(id).orElse(null))
                .filter(user -> user != null)
                .toList();
    }

    public List<User> getListFriends(User user) {
        List<User> friends = new ArrayList<>();
        getFriendships().forEach(friendship -> {
            if (friendship.getUser1Id().equals(user.getId())) {
                friends.add(findUser(friendship.getUser2Id()));
            } else if (friendship.getUser2Id().equals(user.getId())) {
                friends.add(findUser(friendship.getUser1Id()));
            }
        });
        return friends;
    }

    public Optional<User> removeUser(Long id) {
        Optional<User> uOpt = repositoryUser.findOne(id);

        if (uOpt.isEmpty()) {
            throw new ValidationException("User doesn't exist!");
        }

        User u = uOpt.get();

        getFriendships().forEach(friendship ->{
           if(friendship.getUser1Id().equals(id) || friendship.getUser2Id().equals(id)){
               repositoryFriendship.delete(friendship.getId());
           }
        });

        repositoryUser.delete(id);

        notifyObserversUser(new UserEvent(ChangeEventType.DELETE, u));

        return Optional.of(u);
    }

    public Long getNewFriendshipId() {
        Long id = 0L;
        for (Friendship f : repositoryFriendship.findAll()) {
            id = f.getId();
        }
        id++;
        return id;
    }

    public boolean addFriendship(Friendship friendship) {
        try {
            if (getFriendships() != null) {
                getFriendships().forEach(f -> {
                    if (f.getUser1Id().equals(friendship.getUser1Id()) && f.getUser2Id().equals(friendship.getUser2Id())) {
                        throw new ValidationException("The friendship already exists!");
                    }
                });

                if (repositoryUser.findOne(friendship.getUser1Id()).isEmpty() || repositoryUser.findOne(friendship.getUser2Id()).isEmpty()) {
                    throw new ValidationException("User doesn't exist!");
                }

                if (friendship.getUser1Id().equals(friendship.getUser2Id())) {
                    throw new ValidationException("IDs can't be the same!");
                }
            }

            friendship.setId(getNewFriendshipId());
            repositoryFriendship.save(friendship);

            notifyObserversFriendship(new FriendshipEvent(ChangeEventType.ADD, friendship));
            return true;

        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void removeFriendship(Long id1, Long id2) {
        Long id = 0L;
        for (Friendship f : repositoryFriendship.findAll()) {
            if ((f.getUser1Id().equals(id1) && f.getUser2Id().equals(id2)) || (f.getUser1Id().equals(id2) && f.getUser2Id().equals(id1)))
                id = f.getId();
        }
        if (id == 0L)
            throw new IllegalArgumentException("The friendship doesn't exist!");
        repositoryFriendship.delete(id);
    }

    public List<Friendship> getNewFriendRequests(){
        ArrayList<Friendship> newFriendships = new ArrayList<>();
        for(Friendship friendship : this.repositoryFriendship.findAll()){
            if(friendship.getStatus().equals("Pending")
            && friendship.isRecipient(currentUserID)){
                newFriendships.add(friendship);
            }
        }
        return newFriendships;
    }

    // -------------- FOR USERS
    @Override
    public void addObserverUser(Observer<UserEvent> e){
        observersUsers.add(e);
    }

    @Override
    public void removeObserverUser(Observer<UserEvent> e){
        observersUsers.remove(e);
    }

    @Override
    public void notifyObserversUser(UserEvent t){
        observersUsers.forEach(x -> x.update(t));
    }



    // --------------- FOR FRIENSHIPS
    @Override
    public void addObserverFriendship(Observer<FriendshipEvent> e) {
        observersFriendships.add(e);
    }

    @Override
    public void removeObserverFrienship(Observer<FriendshipEvent> e) {
        observersFriendships.remove(e);
    }

    @Override
    public void notifyObserversFriendship(FriendshipEvent t) {
        observersFriendships.forEach(x -> x.update(t));
    }

    public void updateFriendship(Friendship friendshipToBeUpdated){
        // trebuie sa cautam in service aceasta prietenie si sa o facem din Pending -> Active
        for(Friendship fr : this.repositoryFriendship.findAll()){
            if(fr.equals(friendshipToBeUpdated)){
                fr.setStatus("Active");
                this.repositoryFriendship.update(fr);
                return;
            }
        }
    }

    public void deleteFriendship(Friendship friendshipToBeDeleted){
        this.repositoryFriendship.delete(friendshipToBeDeleted.getId());
    }

    // ---------- OBSERVER MESSAGES --------
    @Override
    public void addObserverMessage(Observer<MessageEvent> e) {
        observersMessages.add(e);
    }

    @Override
    public void removeObserverMessage(Observer<MessageEvent> e) {
        observersMessages.remove(e);
    }

    @Override
    public void notifyObserversMessage(MessageEvent t) {
        for (Observer<MessageEvent> observer : observersMessages) {
            observer.update(t);
        }
    }

    public Page<User> friendshipPage(Pageable pageable, Long userId) {
        Page<Friendship> friendshipsPage = this.repositoryFriendship.findAllOnPage(pageable, userId);

        List<User> users = new ArrayList<>();
        for (Friendship friendship : friendshipsPage.getElementsOnPage()) {
                if(friendship.getUser1Id().equals(userId)){
                users.add(this.repositoryUser.findOne(friendship.getUser2Id()).orElse(null));
            } else {
                    users.add(this.repositoryUser.findOne(friendship.getUser1Id()).orElse(null));
            }
        }

        return new Page<>(users, friendshipsPage.getTotalNumberOfElements());
    }
}
