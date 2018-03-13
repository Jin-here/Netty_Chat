package com.vgaw.hibernate.dao;

import com.vgaw.hibernate.pojo.Friend;
import com.vgaw.hibernate.pojo.User;
import com.vgaw.hibernate.util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;

/**
 * Created by caojin on 2016/3/1.
 */
public class FriendDao {
    public void saveFriend(Friend friend) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        session.persist(friend);

        session.getTransaction().commit();
    }

    public ArrayList<String> queryFriendByName(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        ArrayList<String> friendList = (ArrayList<String>) session.createQuery("select p.friendName from Friend p where p.name = :pName")
                .setParameter("pName", name)
                .list();
        ArrayList<String> friendList1 = (ArrayList<String>) session.createQuery("select p.name from Friend p where p.friendName = :pName")
                .setParameter("pName", name)
                .list();
        friendList.addAll(friendList1);

        session.getTransaction().commit();
        return friendList;
    }
}
