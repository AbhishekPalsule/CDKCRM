package com.cdk.crm.service;

import com.cdk.crm.util.mailUtility.AnalyzeMailUtil;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.*;

public class CheckMailService {
    private Properties properties ;
    private Session emailSession;
    private Folder emailFolder;
    private Store store;
    private List<String> responses;
    private String host;
    private String mailStoreType;
    private String user;
    private String password;
    public CheckMailService(String host ,String mailStoreType, String user ,String password) {
        this.host= host;
        this.user=user;
        this.mailStoreType=mailStoreType;
        this.password=password;

        properties = new Properties();
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        emailSession = Session.getDefaultInstance(properties);
       // initialize(host, user, password);
    }

    private void initialize(String host, String user, String password) {
        try {
            store = emailSession.getStore("pop3s");
            store.connect(host, user, password);
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public List<String> checkMail(){
        Message[] messages = null;
        responses = new ArrayList<>();
        try {
            messages = checkMessages();
            for (Message m: messages) {
                responses.add( AnalyzeMailUtil.getMailContent(m,""));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    return responses;
    }

  /*  private static String getMailContent(Part p,String res) throws MessagingException, IOException {
        if (p.isMimeType("text/plain")) {
            return (String) p.getContent();
        }
        if(p.isMimeType("multipart*//*")) {
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                res = getMailContent(mp.getBodyPart(i),res);
            }
        }
        return res;
    }
*/
    private Message[] checkMessages() throws MessagingException {
        initialize(host,user,password);
        FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN),false);
        return emailFolder.search(flagTerm);

    }
    public void closeResources() {
        try {
            emailFolder.close(false);
            store.close();
        }catch (MessagingException e){
            e.printStackTrace();
        }
        }
}
