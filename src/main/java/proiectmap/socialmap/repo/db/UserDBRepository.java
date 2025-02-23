package proiectmap.socialmap.repo.db;
import proiectmap.socialmap.domain.User;
import proiectmap.socialmap.domain.validators.UserValidator;
import proiectmap.socialmap.repo.Repository;
import proiectmap.socialmap.utils.paging.Page;
import proiectmap.socialmap.utils.paging.Pageable;

import java.sql.*;
import java.util.*;

/*
CREATE TABLE users (
        id BIGSERIAL PRIMARY KEY,       -- Auto-incrementing primary key
        first_name VARCHAR(255) NOT NULL, -- First name, cannot be null
last_name VARCHAR(255) NOT NULL,  -- Last name, cannot be null
email VARCHAR(255) UNIQUE NOT NULL, -- Email, must be unique and not null
password VARCHAR(255) NOT NULL    -- Password, cannot be null
        );
 */
public class UserDBRepository implements Repository<Long, User>, UserRepo {


    UserValidator userValidator;
    private final String url;
    private final String username;
    private final String password;

    public UserDBRepository(UserValidator userValidator) {
        this.userValidator = userValidator;
        this.url = DataBaseConfig.getDbUrl();
        this.username = DataBaseConfig.getDbUser();
        this.password = DataBaseConfig.getDbPassword();
    }


    @Override
    public Optional<User> findOne(Long idToSearch) {
        String query = "select * from users WHERE \"id\" = ?";
        User user = null;
       // try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "albert", "admin");
         try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, idToSearch);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                user = new User(idToSearch, firstName, lastName, email, password);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        HashMap<Long, User> users = new HashMap<>();
        //try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "albert", "admin");
         try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String nume = resultSet.getString("first_name");
                String prenume = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User user = new User(id, nume, prenume, email, password);

                users.put(user.getId(), user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users.values();
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        String query = "INSERT INTO users(\"id\", \"first_name\", \"last_name\", \"email\", \"password\" ) VALUES (?,?,?,?,?)";

        //try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "albert", "admin");
         try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEmail());
            statement.setString(5, entity.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Long idToDelete) {

        String query = "DELETE FROM users WHERE \"id\" = ?";

        //try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "albert", "admin");
        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, idToDelete);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        User userToDelete = null;
        for (User user : findAll()) {
            if (Objects.equals(user.getId(), idToDelete)) {
                userToDelete = user;
            }
        }
        return Optional.ofNullable(userToDelete);
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }

    public Page<User> findAllOnPage(Pageable pageable){
        ArrayList<User> usersOnPage = new ArrayList<>();
        String sql = "SELECT * FROM users";
        sql += " limit ? offset ?";

        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
        PreparedStatement statement = connection.prepareStatement(sql);){

            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");

                    User user = new User(id, firstName, last_name, email, password);

                    usersOnPage.add(user);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();;
            throw new RuntimeException(e);
        }
        return new Page<>(usersOnPage, usersOnPage.size());
    }
}
