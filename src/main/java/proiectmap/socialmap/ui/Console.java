package proiectmap.socialmap.ui;

import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.domain.validators.ValidationException;
import proiectmap.socialmap.service.SocialCommunities;
import proiectmap.socialmap.service.SocialNetwork;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
        import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class Console {

    private SocialNetwork socialNetwork;
    private SocialCommunities socialCommunities;

    public Console(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        this.socialCommunities = new SocialCommunities(socialNetwork);
    }

    void printMenu() {
        System.out.println("\t\t\tMENU\t\t\t");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Print users");
        System.out.println("6. Print friendships");
        System.out.println("7. Communities");
        System.out.println("8. Most social community");
        System.out.println("9. List of users with at least N friends");
        System.out.println("10. Friendships of a User made in a certain month");
        System.out.println("11. Users with the family name that contains a given string");
        System.out.println("0. EXIT");
    }

    /**
     * Run function
     */
    public void run() {
        Scanner scan = new Scanner(System.in);
        boolean ok = true;
        printMenu();
        while (ok) {
            //printMenu();
            String input = scan.nextLine();
            switch (input) {
                case "1":
                    addUser();
                    break;
                case "2":
                    removeUser();
                    break;
                case "3":
                    addFriendship();
                    break;
                case "4":
                    removeFriendship();
                    break;
                case "5":
                    printUsers();
                    break;
                case "6":
                    printFriendships();
                    break;
                case "7":
                    printConnectedCommunities();
                    break;
                case "8":
                    printMostSocialCommunity();
                    break;
                case "9":
                    printListOfUsersWithNFriends();
                    break;
                case "10":
                    printFriendshipsMonth();
                    break;
                case "11":
                    printUsersStringFamilyName();
                    break;
                case "0":
                    System.out.println("exit");
                    ok = false;
                    break;
                default:
                    System.out.println("Invalid input!");
                    break;
            }
        }
    }

    private void printUsersStringFamilyName() {
        Scanner scan = new Scanner(System.in);
        System.out.println("string: ");
        String string = scan.nextLine();

//        socialNetwork.getUsers().forEach(user -> {
//            if (user.getLastName().contains(string) || user.getFirstName().contains(string)) {
//                System.out.println(user.getId() + ": " + user.getFirstName() + " " + user.getLastName());
//            }
//        });

        Collection<User> users = (Collection<User>) socialNetwork.getUsers();
        users.stream()
                .filter(user -> user.getLastName().contains(string) || user.getFirstName().contains(string)).
                forEach(user -> System.out.println(user.getId() + ": " + user.getFirstName() + " " + user.getLastName()));
    }

    private void printFriendshipsMonth() {
        Scanner scan = new Scanner(System.in);

        System.out.println("ID of the user: ");
        String var1 = scan.nextLine();
        try {
            Long id = Long.parseLong(var1);
            System.out.println("Month: ");
            Month month = Month.valueOf(scan.nextLine().toUpperCase());
            User user = socialNetwork.findUser(id);
            System.out.println(user.getId() + " " + user.getFirstName() + " " + user.getLastName());

//            socialNetwork.getFriendships().forEach(friendship -> {
//                User friend = null;
//                if (Objects.equals(friendship.getIdUser1(), user.getId()))
//                    friend = socialNetwork.findUser(friendship.getIdUser2());
//                if (Objects.equals(friendship.getIdUser2(), user.getId()))
//                    friend = socialNetwork.findUser(friendship.getIdUser1());
//                if (friendship.getDate().getMonth().equals(month) && friend != null) {
//                    System.out.println(friend.getFirstName() + " | " + friend.getLastName() + " | " + friendship.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//                }
//            });

            // using streams and filters
            StreamSupport.stream(socialNetwork.getFriendships().spliterator(), false)
                    .filter(friendship -> Objects.equals(friendship.getUser1Id(), user.getId()) || Objects.equals(friendship.getUser2Id(), user.getId()))
                    .filter(friendship -> friendship.getFriendsSince().getMonth().equals(month))
                    .map(friendship -> {
                        User friend = Objects.equals(friendship.getUser1Id(), user.getId()) ?
                                socialNetwork.findUser(friendship.getUser2Id()) :
                                socialNetwork.findUser(friendship.getUser1Id());
                        return new AbstractMap.SimpleEntry<>(friend, friendship.getFriendsSince());
                    })
                    .forEach(entry -> {
                        System.out.println(entry.getKey().getFirstName() + " | " +
                                entry.getKey().getLastName() + " | " +
                                entry.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    });
        } catch (IllegalArgumentException e) {
            System.out.println("Id must be a number and month must be a string!");
        } catch (ValidationException v) {
            System.out.println("User doesn't exist!");
        }
    }

    /**
     * Prints the list of users that have at least N friends
     */
    private void printListOfUsersWithNFriends() {
        System.out.println("N: ");
        Scanner scan = new Scanner(System.in);
        int N = scan.nextInt();
        Predicate<User> hasAtLeastNFriends = u -> socialNetwork.getListFriends(u).size() < N;
        List<User> users = new ArrayList<>((Collection) socialNetwork.getUsers());
        users.removeIf(hasAtLeastNFriends);
        users.forEach(u -> System.out.println(u.getFirstName() + " " + u.getLastName() + " " + socialNetwork.getListFriends(u).size() + " friend/s"));
    }


    /**
     * Prints the users from the social network
     */
    void printUsers() {
        System.out.println("\t\t\tUSERS\t\t\t");
        socialNetwork.getUsers().forEach(u -> {
            System.out.println("ID: " + u.getId() + " " + u.getFirstName() + " " + u.getLastName());
        });
    }


    /**
     * Adds user to the social network
     */
    void addUser() {
        System.out.println("Add user");
        Scanner scan = new Scanner(System.in);
        System.out.println("Id: ");
        String var = scan.nextLine();
        System.out.println("First name: ");
        String firstName = scan.nextLine();
        System.out.println("Last name: ");
        String lastName = scan.nextLine();
        try {
            Long id = Long.parseLong(var);
            socialNetwork.addUser(new User(id, firstName, lastName));
        } catch (ValidationException e) {
            System.out.println("Invalid user!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid argument");
        }

    }


    /**
     * Removes user from social network
     */
    void removeUser() {
        printUsers();
        System.out.println("Remove user");
        Scanner scan = new Scanner(System.in);
        System.out.println("Id: ");
        String var = scan.nextLine();
        try {
            Long id = Long.parseLong(var);
            User user = socialNetwork.findUser(id);
            if (user == null) throw new ValidationException("User doesn,t exist!");
            socialNetwork.removeUser(id);
            System.out.println("User: " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " was removed.");
        } catch (IllegalArgumentException e) {
            System.out.println("ID must be a number! It can't contain letters or symbols! ");
        } catch (ValidationException v) {
            System.out.println("User doesn't exist!");
        }
    }


    /**
     * Prints friendships
     */
    void printFriendships() {
        for (User u : socialNetwork.getUsers()) {
            System.out.println("Friends of user " + u.getFirstName() + " " + u.getLastName() + " -> " + socialNetwork.getListFriends(u).size());
            socialNetwork.getListFriends(u).forEach(friend -> {
                System.out.println(friend.getId() + ": " + friend.getFirstName() + " " + friend.getLastName());
            });
        }
    }

    /**
     * Adds a new friendship between two users
     */
    void addFriendship() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");
        String var1 = scan.nextLine();
        System.out.println("ID of the second user: ");
        String var2 = scan.nextLine();
        try {
            Long id1 = 0L, id2 = 0L;
            try {
                id1 = Long.parseLong(var1);
                id2 = Long.parseLong(var2);
            } catch (IllegalArgumentException e) {
                System.out.println("ID must be a number! It can't contain letters or symbols! ");
            }
//            socialNetwork.addFriendship(new Friendship(id1, id2, LocalDateTime.now()));
        } catch (ValidationException e) {
            System.out.println("Friendship is invalid! ");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments! ");
        }
    }


    /**
     * Removes a friendship between two friends
     */
    private void removeFriendship() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID of the first user: ");
        String var1 = scan.nextLine();
        System.out.println("ID of the second user: ");
        String var2 = scan.nextLine();
        try {
            Long id1 = 0L, id2 = 0L;
            try {
                id1 = Long.parseLong(var1);
                id2 = Long.parseLong(var2);
            } catch (IllegalArgumentException e) {
                System.out.println("ID must be a number! It can't contain letters or symbols! ");
            }
            socialNetwork.removeFriendship(id1, id2);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments! ");
        }
    }


    /**
     * Prints connected communities
     */
    private void printConnectedCommunities() {
        System.out.println("Social Communities\n");
        int nrOfCommunities = socialCommunities.connectedCommunities();
        System.out.println("Number of Social Communities: " + nrOfCommunities);
    }


    /**
     * Prints the most social community from the social network
     */
    private void printMostSocialCommunity() {
        System.out.println("Most social community: ");
        List<Long> mostSocialCommunity = socialCommunities.mostSocialCommunity();
        mostSocialCommunity.forEach(System.out::println);
    }

}
