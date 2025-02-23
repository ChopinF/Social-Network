package proiectmap.socialmap.repo.db;

import proiectmap.socialmap.domain.Friendship;
import proiectmap.socialmap.domain.validators.FriendshipValidator;
import proiectmap.socialmap.repo.Repository;
import proiectmap.socialmap.utils.paging.Page;
import proiectmap.socialmap.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class FriendshipDBRepository implements Repository<Long, Friendship>, FriendshipRepo {

    FriendshipValidator friendshipValidator;
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDBRepository(FriendshipValidator friendshipValidator) {
        this.friendshipValidator = friendshipValidator;
        this.url = DataBaseConfig.getDbUrl();
        this.username = DataBaseConfig.getDbUser();
        this.password = DataBaseConfig.getDbPassword();
    }

    @Override
    public Optional<Friendship> findOne(Long idToFind) {
        String query = "SELECT * FROM friendships WHERE \"id\" = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, idToFind);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFriend1 = resultSet.getLong("user1_id");
                Long idFriend2 = resultSet.getLong("user2_id");
                Timestamp date = resultSet.getTimestamp("friends_since");
                String status = resultSet.getString("status"); // Read status as String
                LocalDateTime friendsFrom = new Timestamp(date.getTime()).toLocalDateTime();
                LocalDate curentDay = friendsFrom.toLocalDate();
                friendship = new Friendship(idFriend1, idFriend2, curentDay, status);
                friendship.setId(idToFind);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendship);
    }

    @Override
    public Iterable<Friendship> findAll() {
        Map<Long, Friendship> friendships = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("user1_id");
                Long idFriend2 = resultSet.getLong("user2_id");
                Timestamp date = resultSet.getTimestamp("friends_since");
                String status = resultSet.getString("status"); // Read status as String
                LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                LocalDate curentDate = friendsFrom.toLocalDate();
                Friendship friendship = new Friendship(idFriend1, idFriend2, curentDate, status);
                friendship.setId(id);
                friendships.put(friendship.getId(), friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships.values();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "INSERT INTO friendships(\"id\", \"user1_id\", \"user2_id\", \"friends_since\", \"status\") VALUES (?,?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getUser1Id());
            statement.setLong(3, entity.getUser2Id());
            LocalDate date = entity.getFriendsSince();
            LocalDateTime curentDate = date.atStartOfDay();
            statement.setTimestamp(4, Timestamp.valueOf(curentDate));
            statement.setString(5, entity.getStatus()); // Use status as String
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Long aLong) {
        String query = "DELETE FROM friendships WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Friendship friendshipToDelete = null;
        for (Friendship friendship : findAll()) {
            if (Objects.equals(friendship.getId(), aLong)) {
                friendshipToDelete = friendship;
            }
        }
        return Optional.ofNullable(friendshipToDelete);
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "UPDATE friendships SET \"user1_id\" = ?, \"user2_id\" = ?, \"friends_since\" = ?, \"status\" = ? WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getUser1Id());
            statement.setLong(2, entity.getUser2Id());
            LocalDate date = entity.getFriendsSince();
            LocalDateTime curentDate = date.atStartOfDay();
            statement.setTimestamp(3, Timestamp.valueOf(curentDate));
            statement.setString(4, entity.getStatus()); // Use status as String
            statement.setLong(5, entity.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    public Page<Friendship> findAllOnPage(Pageable pageable){
        ArrayList<Friendship> friendshipsOnPage = new ArrayList<>();
        String sql = "SELECT * FROM frienships";
        sql += " limit ? offset ?";

        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
        PreparedStatement statement = connection.prepareStatement(sql);){
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            try(ResultSet resultSet = statement.executeQuery()){
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("user1_id");
                Long idFriend2 = resultSet.getLong("user2_id");
                Timestamp date = resultSet.getTimestamp("friends_since");
                String status = resultSet.getString("status"); // Read status as String
                LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                LocalDate curentDate = friendsFrom.toLocalDate();
                Friendship friendship = new Friendship(idFriend1, idFriend2, curentDate, status);
                friendship.setId(id);
                //friendships.put(friendship.getId(), friendship);
                friendshipsOnPage.add(friendship);
            }
        }
        catch(SQLException e){
            e.printStackTrace();;
            throw new RuntimeException(e);
        }

        return new Page<>(friendshipsOnPage, friendshipsOnPage.size());
    }

    public Page<Friendship> findAllOnPage(Pageable pageable, Long userId) {
        ArrayList<Friendship> friendshipsOnPage = new ArrayList<>();
        String sql = "SELECT * FROM friendships WHERE user1_id = ? OR user2_id = ?";
        sql += " LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, pageable.getPageSize() * pageable.getPageNumber());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    Long idFriend1 = resultSet.getLong("user1_id");
                    Long idFriend2 = resultSet.getLong("user2_id");
                    Timestamp date = resultSet.getTimestamp("friends_since");
                    String status = resultSet.getString("status");
                    LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                    LocalDate currentDate = friendsFrom.toLocalDate();

                    Friendship friendship = new Friendship(idFriend1, idFriend2, currentDate, status);
                    friendship.setId(id);

                    friendshipsOnPage.add(friendship);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new Page<>(friendshipsOnPage, friendshipsOnPage.size());
    }
}