package jm.task.core.jdbc;

import jdk.management.jfr.ConfigurationInfo;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.UserServiceImpl;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import java.sql.Driver;

public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");
        try (SessionFactory session = config.buildSessionFactory(); Session session2 = session.openSession()) {
        }
    }
}
