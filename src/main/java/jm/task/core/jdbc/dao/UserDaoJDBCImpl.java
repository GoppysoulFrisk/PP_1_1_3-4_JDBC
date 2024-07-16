package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private Connection connection;

    public UserDaoJDBCImpl() {
        this.connection = Util.getConnection();
    }

    @FunctionalInterface
    interface SmartInterface {
        void execute(Connection connection) throws SQLException;
    }

    public void smartMethod(SmartInterface smartInterface) {
        try {
            connection.setAutoCommit(false);
            smartInterface.execute(connection);
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Ошибка, транзакция не откатнулась", ex);
                }
            }
            throw new RuntimeException("Ошибка, пользователь не сохранился", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException("Ошибка, автокоммит не задан", e);
                }
            }
        }
    }

    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
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

    @Override
    public void dropUsersTable() {
        try (Statement dropTable = connection.createStatement()) {
            dropTable.executeUpdate("DROP TABLE IF EXISTS User;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String name, String last_name, byte age) {
        smartMethod(connection -> {
            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO Users (name, last_name, age) VALUES (?, ?, ?)");
            insert.setString(1, name);
            insert.setString(2, last_name);
            insert.setByte(3, age);

            int count = insert.executeUpdate();

            if (count > 0) {
                System.out.println("Пользователь с именем " + name + " " + last_name + " добавлен в базу данных");
            }
        });
    }


    @Override
    public void removeUserById(long id) {
        smartMethod(connection -> {
            PreparedStatement remove = connection.prepareStatement("DELETE FROM Users WHERE id = ?;");
            remove.setLong(1, id);
            remove.executeUpdate();
        });
    }


    @Override
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

    @Override
    public void cleanUsersTable() {
        smartMethod(connection -> connection.createStatement().executeUpdate("DELETE FROM Users;"));
    }
}
