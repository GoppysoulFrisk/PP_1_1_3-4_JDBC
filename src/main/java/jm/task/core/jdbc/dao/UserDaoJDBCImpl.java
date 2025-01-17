package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    Connection connection = Util.getConnection();

    public UserDaoJDBCImpl() {
    }

    public void createUsersTable() {
        try (Statement createTable = connection.createStatement()) {
            createTable.executeUpdate("CREATE TABLE IF NOT EXISTS Users ("
                    + "id BIGINT(11) NOT NULL AUTO_INCREMENT, "
                    + "name VARCHAR(20), "
                    + "last_name VARCHAR(20), "
                    + "age TINYINT, "
                    + "PRIMARY KEY (id));");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropUsersTable() {
        try (Statement dropTable = connection.createStatement()) {
            dropTable.executeUpdate("DROP TABLE IF EXISTS User;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(String name, String last_name, byte age) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO Users (name, last_name, age) VALUES (?, ?, ?);")) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setByte(3, age);
            int complitnost = preparedStatement.executeUpdate();
            if (complitnost > 0){
                System.out.println("User с именем — " + name + " " + last_name + " добавлен в базу данных");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUserById(long id) {
        try (PreparedStatement remove = connection.prepareStatement(
                "DELETE FROM Users WHERE id = ?;")) {

            remove.setLong(1, id);
            remove.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (Statement geter = connection.createStatement()) {

            ResultSet resultSet = geter.executeQuery("SELECT * FROM Users;");

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setAge(resultSet.getByte("age"));

                userList.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userList;
    }

    public void cleanUsersTable() {
        try (Statement clean = connection.createStatement()) {
            clean.executeUpdate("DELETE FROM Users;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
