package com.cdk.crm.dao;

import com.cdk.crm.domain.FollowUp;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.List;

public class FollowUpDAO {

    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
        hibernateTemplate.setCheckWriteOperations(false);
    }

    public FollowUp add(FollowUp followUp){
        hibernateTemplate.save(followUp);
        return followUp;
    }

    public void update(FollowUp followUp){
        hibernateTemplate.saveOrUpdate(followUp);
    }

    public FollowUp get(int leadId){
        FollowUp followUp = (FollowUp) hibernateTemplate.get(FollowUp.class,leadId);
        return followUp;
    }

    public List<Object> getAllLead(){
        List<Object> followUps = hibernateTemplate.find("select f.leadId, f.currentResponse, f.followUpCount, u.email from FollowUp f, User u where u.leadId = f.leadId AND f.currentResponse != null");
        return followUps;

    }

    public List<Object> getCustomersForOffer(){

        List<Object> eligibleCustomers = hibernateTemplate.find("select f.leadId, f.currentResponse, f.followUpCount, u.email from FollowUp f, User u where u.leadId = f.leadId AND f.followUpCount = 2 AND f.currentResponse = 'Yes' ");
        return eligibleCustomers;
    }


    public List<Object> getNegativeResponses() {
        List<Object> eligibleCustomers = hibernateTemplate.find("select f.leadId, f.currentResponse, f.followUpCount, u.email from FollowUp f, User u where u.leadId = f.leadId AND f.followUpCount = 2 AND f.currentResponse = 'No' ");
        return eligibleCustomers;
    }

    public void delete(int leadId){
        FollowUp followup = get(leadId);
        hibernateTemplate.delete(followup);
    }
}