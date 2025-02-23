package proiectmap.socialmap.repo.db;

import proiectmap.socialmap.domain.Message;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.repo.Repository;
import proiectmap.socialmap.repo.db.UserDBRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_ids TEXT NOT NULL, -- Lista de ID-uri (salvate ca JSON sau delimitate de virgulă)
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id)
);
 */
public class MessagesDBRepository implements Repository<Long, Message>{
    private String url = null;
    private String username = null;
    private String password = null;
    private UserDBRepository userDBRepository = null;

    public MessagesDBRepository(UserDBRepository userDBRepository) {
        this.url = DataBaseConfig.getDbUrl();
        this.username = DataBaseConfig.getDbUser();
        this.password = DataBaseConfig.getDbPassword();
        this.userDBRepository = userDBRepository;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        String query = "SELECT * FROM messages WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(parseMessage(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                messages.add(parseMessage(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity){
        String query = "INSERT INTO messages (sender_id, receiver_ids, content, timestamp) VALUES (?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(query)){
            statement.setLong(1, entity.getFrom().getId());
            statement.setString(2, entity.getTo().stream().map(User::getId).map(String::valueOf).collect(Collectors.joining(",")));
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getData()));
            statement.executeUpdate();

            ResultSet generatedKeys= statement.getGeneratedKeys();
            if(generatedKeys.next()){
                entity.setId(generatedKeys.getLong(1));
            }
            return Optional.of(entity);
        }
        catch(SQLException e){
            e.printStackTrace();;
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long id){
        Optional<Message> messageToDelete = findOne(id);
        if (messageToDelete.isEmpty()) return Optional.empty();

        String query = "DELETE FROM messages WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messageToDelete;
    }

    @Override
    public Optional<Message> update(Message entity) {
        String query = "UPDATE messages SET content = ?, timestamp = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, entity.getMessage());
            statement.setTimestamp(2, Timestamp.valueOf(entity.getData()));
            statement.setLong(3, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    private Message parseMessage(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long senderId = resultSet.getLong("sender_id");
        String receiverIds = resultSet.getString("receiver_ids");
        String content = resultSet.getString("content");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");

        Optional<User> senderOpt = userDBRepository.findOne(senderId);
        User sender = senderOpt.orElseThrow(() ->
                new RuntimeException("Sender with ID " + senderId + " not found in the database"));

        List<User> receivers = List.of(receiverIds.split(",")).stream()
                .map(receiverId -> {
                    Long receiverIdLong = Long.parseLong(receiverId.trim());
                    Optional<User> receiverOpt = userDBRepository.findOne(receiverIdLong);
                    return receiverOpt.orElseThrow(() ->
                            new RuntimeException("Receiver with ID " + receiverIdLong + " not found in the database"));
                })
                .toList();

        return new Message(id, sender, receivers, content, timestamp.toLocalDateTime());
    }
}
