package com.worker.DB_managing;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.worker.DB_classes.MessagesEntity;
import com.worker.DB_classes.UserEntity;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by AsmodeusX on 29.10.2016.
 */
public class HibernateWorker implements Serializable {
    private HibUtil HibUtils = new HibUtil();
    private SessionFactory factory = HibUtil.getSessionFactory();
    public HibernateWorker () {
    }

    public List getAllUser(String login)
    {
        Session session = factory.openSession();
        List users = session.createQuery("SELECT U.login FROM com.worker.DB_classes.UserEntity U WHERE U.login!= :login").setParameter("login",login).list();
        System.err.println(users.size());

        if(!users.isEmpty())
        {
            session.close();
            return users;
        }

        session.close();
        return null;
    }

    public UserEntity getUserByLogin(String login) {
        //Костыль
        Session session = factory.openSession();
        List ids = session.createQuery("SELECT U.id FROM com.worker.DB_classes.UserEntity U WHERE U.login= :login").setParameter("login", login).list();
        System.err.println(ids.size());
        //Костыль
        if (!ids.isEmpty() || ids.size() > 1)
        {
            Integer currentId = (Integer) ids.get(0);
            UserEntity user = session.get(UserEntity.class, currentId);
            session.close();
            return user;
        }
        session.close();
        return null;
    }

    public Boolean registerNewUser(String login, String passwd, String eMail)
    {
        if (getUserByLogin(login) != null) {
            return false;
        }

        Session session = factory.openSession();
        session.beginTransaction();

        UserEntity newUser = new UserEntity();
        newUser.setLogin(login);
        newUser.setPassword(passwd);
        newUser.setEmail(eMail);

        session.save(newUser);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    public Boolean saveNewMessage(String message, Integer idfrom, String loginAddressee)
    {
        UserEntity userAddressee = getUserByLogin(loginAddressee);
        //java.util.Date dd = new java.util.Date();
        Date d = new Date(new java.util.Date().getTime());
        Session session = factory.openSession();
        session.beginTransaction();

        MessagesEntity newMessage = new MessagesEntity();
        newMessage.setMessage(message);
        newMessage.setDateMessage(d);
        newMessage.setIdfrom(idfrom);
        newMessage.setIdto(userAddressee.getId());
        newMessage.setIsread(0);

        session.save(newMessage);
        session.getTransaction().commit();
        session.close();
        return true;
    }

    public void shutdown ()
    {
        HibUtil.shutdown();
    }
}