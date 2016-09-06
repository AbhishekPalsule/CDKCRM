package com.cdk.crm.test;

import com.cdk.crm.dao.ResponseDAO;
import com.cdk.crm.util.mailUtility.InitialFollowUp;
import com.cdk.crm.util.mailUtility.NegativeFollowUp;
import com.cdk.crm.util.mailUtility.OfferFollowUp;
import com.cdk.crm.util.mailUtility.PositiveFollowUp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by palsulea on 9/1/2016.
 */
public class Test {

    @org.junit.Test
    public void testResponseData(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        ResponseDAO responseDAO = (ResponseDAO)applicationContext.getBean("responseDAO");
        System.out.println(responseDAO.getAllDetailsOfLeadsByDate());
    }
    @org.junit.Test
    public void testPositiveResponseMsg(){
        PositiveFollowUp followUp = new PositiveFollowUp();
        System.out.println(followUp.getMailMessage("Pooja","her","1234567"));
    }
    @org.junit.Test
    public void testinitialFollowMsg(){
        InitialFollowUp initialFollowUp = new InitialFollowUp();
        System.out.println(initialFollowUp.getMailMessage((long)1,"Abhishek","Pooja","her","1234567"));
    }
    @org.junit.Test
    public void testNegativeResponseMsg(){
        NegativeFollowUp followUp = new NegativeFollowUp();
        System.out.println(followUp.getMailMessage());
    }

    @org.junit.Test
    public void testOfferResponseMsg(){
        OfferFollowUp followUp = new OfferFollowUp();
        System.out.println(followUp.getMailMessage());
    }
}
