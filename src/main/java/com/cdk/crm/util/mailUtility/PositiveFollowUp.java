package com.cdk.crm.util.mailUtility;

import java.io.File;

public class PositiveFollowUp extends PositiveTemplate{

    private String subject;
    public PositiveFollowUp() {
        super.path = "C:\\Users\\palsulea\\IdeaProjects\\CDKCRM\\PositiveFollowUp";
        super.mailMessage = new File(path);
        this.subject="Response Acknowledged";
    }

    public String getMailMessage( String repName,String gender,String phNo) {
        String message = readFile();
        return getMessageString(repName,gender,phNo,message);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private String getMessageString(String repName, String gender, String phno, String message) {

        message= message.replace("<repName>",repName);
        message=message.replace("<gender>",gender);
        message = message.replace("<phNo>",phno);
        return message;
    }

}
