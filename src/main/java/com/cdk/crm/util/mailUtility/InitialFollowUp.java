package com.cdk.crm.util.mailUtility;

import java.io.File;

public class InitialFollowUp extends Template{
    private String subject;
    public InitialFollowUp() {
        super.path = "C:\\Users\\palsulea\\IdeaProjects\\CDKCRM\\InitialFollowUp.txt";
        super.mailMessage = new File(super.path);
        this.subject="Follow up for Enquiry";
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMailMessage(Long leadId, String name , String make , String model , String website) {
        System.out.println("a");
        String message = readFile();
        return getMessageString(""+leadId,name, make, model, website, message);
    }

    private String getMessageString(String leadId,String name, String make, String model, String website, String message) {
        message= message.replace("<name>",name);
        message= message.replace("<make>",make);
        message= message.replace("<model>",model);
        message = message.replace("<website>",website);
        message = message.replace("<leadId>",leadId);
        return message;
    }
}