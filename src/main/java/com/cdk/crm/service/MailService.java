package com.cdk.crm.service;

import com.cdk.crm.util.mailUtility.InitialFollowUp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;

import static com.cdk.crm.util.JSONUtil.getJSONArray;
import static com.cdk.crm.util.JSONUtil.getJsonObject;

public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public boolean sendMail(String address,String subject,String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        try {
            mailMessage.setFrom("lead.cdkcrm@gmail.com");
            mailMessage.setTo(address);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public  String sendFollowUps(String jsonLeads){
       String ids="";
        try {
            JSONArray jsonArray = getJSONArray(jsonLeads);
            for(int i =0 ; i < jsonArray.size() ; i ++){
                JSONObject data = getJsonObject(jsonArray, i);
                InitialFollowUp ifu = new InitialFollowUp();
                String message = ifu.getMailMessage((Long) data.get("lead_id"),(String ) data.get("userName"),(String) data.get("make"),(String) data.get("model") , (String) data.get("website"));
                boolean status = sendMail((String) data.get("email"),ifu.getSubject(),message);
                if(status){
                    System.out.println(status);
                     ids+=data.get("lead_id")+",";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return ids;
    }
/*
    public static JSONObject getJsonObject(JSONArray jsonArray, int index) {
        Object dataObject = jsonArray.get(index);
        return (JSONObject)dataObject;
    }

    public static JSONArray getJSONArray(String jsonLeads) throws ParseException {
        JSONParser parser = new JSONParser();
        System.out.println(jsonLeads);
        return (JSONArray)parser.parse(jsonLeads);
    }*/

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
