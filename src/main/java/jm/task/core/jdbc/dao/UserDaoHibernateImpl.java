package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    private final SessionFactory sessionFactory = Util.getSessionFactory();

    public UserDaoHibernateImpl() {
    }

    @FunctionalInterface
    interface SmartInterface {
        void execute(Session session);
    }

    public void smartMethod(SmartInterface smartInterface) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            smartInterface.execute(session);
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void createUsersTable() {
       smartMethod(session -> session.createNativeQuery("CREATE TABLE IF NOT EXISTS users" +
               " (id BIGSERIAL not null , name VARCHAR(50), " +
               "lastname VARCHAR(50), " +
               "age SMALLINT NOT NULL)")
               .executeUpdate());
    }


    @Override
    public void dropUsersTable() {
        smartMethod(session -> session.createNativeQuery("DROP TABLE IF EXISTS users").executeUpdate());
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        smartMethod(session -> session.save(new User(name, lastName, age)));
    }

    @Override
    public void removeUserById(long id) {
        smartMethod(session -> session.delete(session.get(User.class, id)));
    }

    @Override
    public List<User> getAllUsers() {
        final List<User> users = new ArrayList<>();
        smartMethod(session -> {
            CriteriaQuery<User> criteriaQuery = session.getCriteriaBuilder().createQuery(User.class);
            criteriaQuery.from(User.class);
            users.addAll(session.createQuery(criteriaQuery).getResultList());
        });
        return users;
    }

    @Override
    public void cleanUsersTable() {
        smartMethod(session -> session.createNativeQuery("TRUNCATE TABLE users;").executeUpdate());
    }
}
