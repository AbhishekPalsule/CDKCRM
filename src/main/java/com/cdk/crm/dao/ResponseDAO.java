package com.cdk.crm.dao;

import com.cdk.crm.dto.ResponseData;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ResponseDAO {

    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
        hibernateTemplate.setCheckWriteOperations(false);
    }

    public List<ResponseData> getAllDetailsOfLeadsByDate(){
         List<Object> responseData = (List<Object>) hibernateTemplate.find("select l.leadId,l.date,u.name,u.contact_no,u.email,s.make,s.model,s.cost,s.content,s.website from Lead l, User u , SearchInfo s where s.searchId = l.searchInfoId AND l.leadId = u.leadId ");
        List<ResponseData> responseDataList= new ArrayList<>();
        for (Object object:responseData) {
            Object obj[] = (Object[]) object;
            ResponseData rs = getResponseData(obj);
            responseDataList.add(rs);
        }
        //System.out.println(responseData.size());
        return responseDataList;
    }

    private ResponseData getResponseData(Object[] obj) {
        ResponseData rs = new ResponseData();
        rs.setLead_id((Integer) obj[0]);
        rs.setDate((Date)obj[1]);
        rs.setUserName((String)obj[2]);
        rs.setContact_no((String)obj[3]);
        rs.setEmail((String)obj[4]);
        rs.setMake((String)obj[5]);
        rs.setModel((String)obj[6]);
        rs.setCost((Float)obj[7]);
        rs.setContent((String)obj[8]);
        rs.setWebsite((String)obj[9]);
    return rs;
    }
}
