/*
package com.cdk.crm.Controller;

import com.cdk.crm.service.CheckMailService;
import com.cdk.crm.service.MailService;
import com.cdk.crm.util.JSONUtil;
import com.cdk.crm.util.mailUtility.NegativeFollowUp;
import com.cdk.crm.util.mailUtility.OfferFollowUp;
import com.cdk.crm.util.mailUtility.PositiveFollowUp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MailController {
    @Autowired
    private MailService mailService;

    private List<String> responses;
    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @RequestMapping( value ="/sendEmail",method = RequestMethod.GET)
    public ModelAndView doSendEmail(HttpServletRequest request , HttpServletResponse response){
            System.out.println("initiate Sending procedure");
            System.out.println(request.getParameter("json"));
            String ids= mailService.sendFollowUps((String) request.getParameter("json"));
            System.out.println("all messages sent");
            System.out.println("redirecting to initiatedFollowUp.do");
        return new ModelAndView("redirect:initiatedFollowUp.do","ids",ids);

    }

    private SimpleMailMessage getMailMessage(String address, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(address);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        return mailMessage;
    }

    @Autowired
    CheckMailService checkMailService ;
    @RequestMapping(value = "/readMail",method = RequestMethod.GET)
    public ModelAndView readMail(HttpServletRequest request, final HttpServletResponse response){
        String data = "";
        try {
            System.out.println("I'm here");
            System.out.println("checking ");
            responses = new ArrayList<>();
            responses = checkMailService.checkMail();
            System.out.println(responses);
            System.out.println("sending to updateMailResponses");

            for (String resp : responses) {
                data += resp + " ";
            }
            checkMailService.closeResources();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ModelAndView("redirect:updateMailResponses.do","mailResponses",data);
    }
    @RequestMapping(value = "reply" , method = RequestMethod.GET)
    public ModelAndView reply(HttpServletRequest request,HttpServletResponse response){
        String statuses ="";

        try {

            String userInfo = request.getParameter("json");
            String data[] = userInfo.split(",");

                Long leadId = Long.parseLong(data[0]);
                String status = data[1];
                int count = Integer.parseInt(data[2]);
                System.out.println(status);
                String address = data[3];

                if(status.trim().equalsIgnoreCase("Yes")){
                    statuses+=sendForPositiveResponse(leadId,count,address)+",";
                }if(status.trim().equalsIgnoreCase("No")){
                    statuses+=sendForNegativeResponse(leadId,count,address)+",";
                }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return new ModelAndView("redirect:updateMailResponses","mailResponses",statuses);
    }

    private String sendForNegativeResponse(Long leadId, int count,String address) {
        if(count==1){
            if (sendThankYouMsg(leadId,address)) return "" + leadId;
        }
        return "";
    }

    private boolean sendThankYouMsg(Long leadId,String address) {
        NegativeFollowUp followUp = new NegativeFollowUp();

        boolean b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage());
        return b;
    }

    private String sendForPositiveResponse(Long leadId, int count,String address) {
        if(count==1){
            if (sendReqAckMsg(address)) return "" + leadId;
        }else if(count==2){
            if (sendOfferMsg(address)) return "" + leadId;
        }
    return "";
    }

    private boolean sendOfferMsg(String address) {
        OfferFollowUp followUp = new OfferFollowUp();

        boolean b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage());
        return b;

    }

    private boolean sendReqAckMsg(String address) {
        boolean b =false;
        try {
            PositiveFollowUp followUp = new PositiveFollowUp();

            b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage("Pooja", "her", "1234567"));
        }catch(Exception e){
            e.printStackTrace();
        }
            return b;
    }
}*/
