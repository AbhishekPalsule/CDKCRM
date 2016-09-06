package com.cdk.crm.util.mailUtility;

import java.io.File;

public class NegativeFollowUp extends Template {
    private String subject;
    public NegativeFollowUp() {
        super.path = "C:\\Users\\palsulea\\IdeaProjects\\CDKCRM\\NegativeFollowUp";
        super.mailMessage = new File(path);
        this.subject = "Thank you ";
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMailMessage() {
        String message = readFile();
        return message;
    }

    /*private String getMessageString(String message) {
        message= message.replace("<name>",cusName);
        return message;
    }*/


}
