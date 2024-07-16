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

    private final SessionFactory sessionFactory;

    public UserDaoHibernateImpl() {
        this.sessionFactory = Util.getSessionFactory();
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

    public void closeSession() {
        if (this.sessionFactory != null) {
            try {
                this.sessionFactory.close();
            } catch (HibernateException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void createUsersTable() {
       smartMethod(session -> session.createNativeQuery("CREATE TABLE IF NOT EXISTS Users ("
                       + "id BIGINT(11) NOT NULL AUTO_INCREMENT, "
                       + "name VARCHAR(20), "
                       + "last_name VARCHAR(20), "
                       + "age TINYINT, "
                       + "PRIMARY KEY (id));")
               .executeUpdate());
    }


    @Override
    public void dropUsersTable() {
        smartMethod(session -> session.createNativeQuery("DROP TABLE IF EXISTS Users").executeUpdate());
    }

    @Override
    public void saveUser(String name, String last_name, byte age) {
        smartMethod(session -> session.save(new User(name, last_name, age)));
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
