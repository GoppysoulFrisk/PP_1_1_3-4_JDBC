package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        UserServiceImpl userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.saveUser("kfodkfod", "jfsdhfjsdhf", (byte) 56);
        System.out.println(userService.getAllUsers().toString());
        Util.closeSessionFactory();
    }
}
