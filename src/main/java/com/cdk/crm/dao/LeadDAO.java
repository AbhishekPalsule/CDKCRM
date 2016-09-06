package com.cdk.crm.dao;

import com.cdk.crm.domain.Lead;
import com.cdk.crm.dto.ResponseData;
import org.springframework.orm.hibernate3.HibernateTemplate;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeadDAO {

    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
        hibernateTemplate.setCheckWriteOperations(false);
    }
    public Lead add(Lead lead){
         hibernateTemplate.save(lead);

        //System.out.println(save);
        return lead;
    }

    public void delete(int leadId){
        Lead lead = get(leadId);
        hibernateTemplate.delete(lead);
    }

    public Lead get(int leadId){
        Lead lead = (Lead) hibernateTemplate.get(Lead.class,leadId);
        return lead;
    }

    public Lead getAllLead(){
        return (Lead) hibernateTemplate.loadAll(Lead.class);
    }


}
