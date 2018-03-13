package com.vgaw.hibernate.dao;

import com.vgaw.hibernate.pojo.User;
import com.vgaw.hibernate.util.HibernateUtil;
import org.hibernate.Session;

/**
 * Created by caojin on 15-10-26.
 */
public class UserDao {
    /**
     * save user info.
     * @param user
     */
    public void saveUser(User user){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        session.persist(user);

        session.getTransaction().commit();
    }

    /**
     * query whether the user is exist by the given name.
     * @param name
     * @return
     */
    public User queryUserByName(String name){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        User user = (User) session.createQuery("select p from User p where p.name = :pName")
                .setParameter("pName", name)
                .uniqueResult();
        session.getTransaction().commit();

        return user;
    }

    /**
     * 根据用户id查询用户信息
     * @param id
     * @return
     */
    public User queryUserById(long id){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        User user = (User) session.createQuery("select p from User p where p.id = :pId")
                .setParameter("pId", id)
                .uniqueResult();
        session.getTransaction().commit();

        return user;
    }

}
