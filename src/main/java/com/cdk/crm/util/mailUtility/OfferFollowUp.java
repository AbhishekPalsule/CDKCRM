package com.cdk.crm.util.mailUtility;

import java.io.File;


public class OfferFollowUp extends PositiveTemplate {

    private String subject;
    public OfferFollowUp() {
        super.path = "C:\\Users\\palsulea\\IdeaProjects\\CDKCRM\\OfferFollowUp.txt";
        super.mailMessage = new File(path);
        this.subject = "Offer";
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMailMessage() {
        String message = readFile();
        return getMessageString(message);
    }

    private String getMessageString(String message) {
        message=message.replace("<offer>",getOfferMessage());
        return message;
    }

}
