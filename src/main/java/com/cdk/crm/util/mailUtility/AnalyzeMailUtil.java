package com.cdk.crm.util.mailUtility;

import com.cdk.crm.service.CheckMailService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;
import java.util.List;

public class AnalyzeMailUtil {


    public static String getMailContent(Part p, String result) throws MessagingException, IOException {
        if (p.isMimeType("text/plain")) {
            System.out.println(p.getContent());
            return (String) p.getContent();
        }
        if(p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++) {
                result = getMailContent(mp.getBodyPart(i),result);
            }
        }
        return result;
    }

}
