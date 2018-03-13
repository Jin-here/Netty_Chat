package hibernate;


import com.vgaw.hibernate.pojo.User;
import com.vgaw.hibernate.util.HibernateUtil;
import junit.framework.TestCase;
import org.hibernate.Session;

import javax.jws.soap.SOAPBinding;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2015/9/18.
 */
public class TestFunc extends TestCase {
    public void testCRUD(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        //add test
        /*User user = new User();
        user.setPassword("123");
        user.setNickName("caojin");
        session.persist(user);

        User user1 = new User();
        user1.setPassword("123");
        user1.setNickName("gusihan");
        session.persist(user1);

        List<User> members = new ArrayList<User>();
        members.add(user);
        members.add(user1);
        Group group = new Group();
        group.setName("kingdom");
        group.setMembers(members);
        session.persist(group);*/

        //query test
        //1:simple query
        /*List<Group> groups = session.createQuery("from Group").list();
        for (Group group : groups){
            //cascade delete by default
            session.delete(group);
        }
        List<User> members = session.createQuery("from User").list();
        for (User member : members){
            //cascade delete by default
            session.delete(member);
        }*/
        //2:fuck query
        /*String nickName = "caojin";
        User user = (User) session.createQuery("select p from User p where p.nickName = :pnickName")
                .setParameter("pnickName", nickName)
                .uniqueResult();
        List<Group> groups = user.getGroups();
        for (Group group : groups){
            System.out.println(group.getName());
        }*/

        // 测试好友
        User caojin = (User) session.createQuery("select p from User p where p.name = :pnickName")
                .setParameter("pnickName", "caojin")
        .uniqueResult();
        User chenkai = (User) session.createQuery("select p from User p where p.name = :pnickName")
                .setParameter("pnickName", "chenkai")
                .uniqueResult();
        System.out.println(caojin.getPassword() + ":" + chenkai.getPassword());

        session.getTransaction().commit();

        HibernateUtil.getSessionFactory().close();
    }
}
