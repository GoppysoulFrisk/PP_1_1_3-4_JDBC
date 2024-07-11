package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserServiceImpl;

import java.sql.Driver;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        UserServiceImpl userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.saveUser("Alex", "Robinovich", (byte) 37);
        userService.saveUser("Sveta", "Kyzmina", (byte) 14);
        userService.saveUser("Evgenii", "Lubimov", (byte) 56);
        System.out.println(userService.getAllUsers().toString());
        userService.cleanUsersTable();
        System.out.println(userService.getAllUsers().toString());
//        userService.dropUsersTable();
//        System.out.println(userService.getAllUsers().toString());

    }
}
