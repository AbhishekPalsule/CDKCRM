package com.cdk.crm.dao;

import com.cdk.crm.domain.User;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserDAO {

    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
        hibernateTemplate.setCheckWriteOperations(false);
    }
    public User add(User user){
        hibernateTemplate.save(user);
        return user;
    }

    public void delete(int userId){
        User user = get(userId);
        hibernateTemplate.delete(user);
    }

    public User get(int userId){
        User user = (User) hibernateTemplate.get(User.class,userId);
        return user;
    }

    public User getAllLead(){
        return (User) hibernateTemplate.loadAll(User.class);
    }

}
