package com.cdk.crm.deploy;

import com.cdk.crm.dao.*;
import com.cdk.crm.domain.FollowUp;
import com.cdk.crm.domain.Lead;
import com.cdk.crm.domain.SearchInfo;
import com.cdk.crm.domain.User;
import com.cdk.crm.service.CheckMailService;
import com.cdk.crm.service.MailService;
import com.cdk.crm.util.CSVUtility;
import com.cdk.crm.util.JSONUtil;
import com.cdk.crm.util.MiscUtil;
import com.cdk.crm.util.mailUtility.NegativeFollowUp;
import com.cdk.crm.util.mailUtility.OfferFollowUp;
import com.cdk.crm.util.mailUtility.PositiveFollowUp;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.cdk.crm.util.MiscUtil.getLead;
import static com.cdk.crm.util.MiscUtil.getUser;
import static com.cdk.crm.util.MiscUtil.getUserInfo;

public class Main {

    final static String delimiter =",";
    final static String ONGOING = "Ongoing";
    final static String INITIATED = "Initiated";
    private static UserDAO userDAO ;
    private static LeadDAO leadDAO ;
    private static SearchInfoDAO searchInfoDAO ;
    private static ResponseDAO responseDAO ;
    private static MailService mailService;
    private static FollowUpDAO followUpDAO;
    private static CheckMailService checkMailService ;
    private static long DELAY =1000l;
    private static long DATA_INTERVAL=3000l;
    private static long MAIL_INTERVAL=1000l;


    public static void main(String[] args) {
        initializeBeans();
        TimerTask checkForData = getCheckForDataTask();
        TimerTask checkMail = getCheckMailTask();
        new Timer().schedule(checkForData,DELAY,DATA_INTERVAL);
        new Timer().schedule(checkMail,DELAY,MAIL_INTERVAL);
    }

    private static TimerTask getCheckMailTask() {
        return new TimerTask() {
            @Override
            public void run() {
                List<String> responses = readMail();
                if(responses.size()!=0) {
                    updateMailResponses(responses);
                    String userInfo = fetchUpdatedFollowUp();
                    followUpUpdate(reply(userInfo));
                }
                sendOffers();
                sendThankYou();
            }
        };
    }

    private static void updateMailResponses(List<String> responses){
        for (String lead : responses) {
            try {
                if(!lead.equals("")) {
                    String leadInfo[] = lead.split(",");
                    FollowUp followUp = followUpDAO.get(Integer.parseInt(leadInfo[0]));
                    followUp.setCurrentResponse(leadInfo[1].trim());
                    followUpDAO.update(followUp);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private static String reply(String userInfo){
        String statuses ="";
        try {
            String data[] = userInfo.split(" ");
            for(String eachData: data) {
                String[] info = eachData.split(",");
                Long leadId = Long.parseLong(info[0]);
                String status = info[1];
                int count = Integer.parseInt(info[2]);
                String address = info[3];

                if (status.trim().equalsIgnoreCase("Yes")) {
                    //System.out.println("STATUS:"+status+"\n");
                    statuses += sendForPositiveResponse(leadId, count, address) + ",";
                }
                if (status.trim().equalsIgnoreCase("No")) {
                    //System.out.println("STATUS:"+status+"\n");
                    statuses += sendForNegativeResponse(leadId, count, address) + ",";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statuses;
    }

    private static void cleanDatabase(int leadId){
        followUpDAO.delete(leadId);
    }

    private static String sendForNegativeResponse(Long leadId, int count,String address) {
        if(count==1){
            if (sendThankYouMsg(leadId,address)) return "" + leadId;
        }
        return "";
    }

    private static boolean sendThankYouMsg(Long leadId,String address) {
        NegativeFollowUp followUp = new NegativeFollowUp();
        boolean b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage());
        return b;
    }

    private static String sendForPositiveResponse(Long leadId, int count,String address) {
        if(count==1){
            if (sendReqAckMsg(address)) return "" + leadId;
        }
        return "";
    }

    private static void sendOffers(){
        List<Object> followUps = followUpDAO.getCustomersForOffer();
        String userInfo = getUserInfo(followUps);
        if(!userInfo.equals("")) {
            String[] eligibleCustomers = userInfo.split(" ");
            for (String eachCustomer : eligibleCustomers) {
                String[] customerInfo = eachCustomer.split(",");
                sendOfferMsg(customerInfo[3]);
                cleanDatabase(Integer.parseInt(customerInfo[0]));
            }
        }
    }

    private static void sendThankYou() {
        List<Object> followUps = followUpDAO.getNegativeResponses();
        String userInfo = getUserInfo(followUps);
        if (!userInfo.equals("")) {
            String[] eligibleCustomers = userInfo.split(" ");
            for (String eachCustomer : eligibleCustomers) {
                String[] customerInfo = eachCustomer.split(",");
                sendThankYouMsg(Long.valueOf((customerInfo[0])), customerInfo[3]);
                cleanDatabase(Integer.parseInt(customerInfo[0]));
            }
        }
    }

    private static boolean sendOfferMsg(String address) {
        OfferFollowUp followUp = new OfferFollowUp();
        boolean b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage());
        return b;

    }

    private static boolean sendReqAckMsg(String address) {
        boolean b =false;
        String REPNAME = "Pooja";
        String GENDER = "her";
        String CONTACT = "123456789";
        try {
            PositiveFollowUp followUp = new PositiveFollowUp();
            b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage(REPNAME, GENDER, CONTACT));
        }catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }

    private static String fetchUpdatedFollowUp() {
        List<Object> followUps = followUpDAO.getAllLead();

        String userInfo="";
        userInfo += getUserInfo(followUps);
        // System.out.println("sending for reply\n\n");
        return userInfo;
    }

    private static List<String> readMail(){
        List<String> responses =null;
        try {
            responses = checkMailService.checkMail();
            checkMailService.closeResources();
        }catch (Exception e){
            e.printStackTrace();
        }
        return responses;
    }

    private static TimerTask getCheckForDataTask() {
        return new TimerTask() {
                @Override
                public void run() {
                    File DataDirectory = new File("C:\\Users\\palsulea\\Desktop\\lead_data");
                    if(DataDirectory.exists() && DataDirectory.isDirectory()){
                        File[] files = DataDirectory.listFiles();

                        for(File f:files) {
                            ArrayList<String> lines = CSVUtility.readLine(f.getPath());
                            addToDB(lines);
                            String jsonString = JSONUtil.getJSONConvertedData(responseDAO.getAllDetailsOfLeadsByDate());
                            String ids = doSendEmail(jsonString);
                            followUpUpdate(ids);

                            copyFile(f);
                        }
                        }
                }
            };
    }

    private static void copyFile(File f) {
        try {
            String name = f.getName().replace(".csv","_"+new Date().getTime()+".csv");
            File copy =  new File("C:\\Users\\palsulea\\Desktop\\Archive\\"+name);
            FileUtils.copyFile(f,copy);
        } catch (IOException e) {
            e.printStackTrace();
        }
        f.delete();
    }

    private static void followUpUpdate(String ids){

        String idArray[] = ids.split(",");
        //System.out.println(ids);
        for (String id: idArray){
            if(!id.equals("")){
                FollowUp followUp = followUpDAO.get(Integer.parseInt(id.trim()));
                if(followUp==null){
                    createNewFollowUp(id);
                }else {
                    updateExistingFollowUp(followUp);
                }
            }
        }
    }

    private static void createNewFollowUp(String id) {
        FollowUp followUp = new FollowUp();
        followUp.setLeadId(Integer.parseInt(id));
        followUp.setFollowUpCount(1);
        followUp.setCurrentFollowUpStatus(INITIATED);
        followUpDAO.update(followUp);
    }

    private static void updateExistingFollowUp(FollowUp followUp) {
        followUp.setFollowUpCount(followUp.getFollowUpCount() + 1);
        followUp.setCurrentFollowUpStatus(ONGOING);
        followUpDAO.update(followUp);
    }

    public static String doSendEmail(String jsonString){
        String ids= mailService.sendFollowUps(jsonString);
        return ids;
    }

    private static void initializeBeans() {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        searchInfoDAO = (SearchInfoDAO)context.getBean("searchInfoDAO");
        leadDAO = (LeadDAO)context.getBean("leadDAO");
        userDAO = (UserDAO)context.getBean("userDAO");
        responseDAO = (ResponseDAO)context.getBean("responseDAO");
        mailService = (MailService)context.getBean("mailService");
        followUpDAO = (FollowUpDAO)context.getBean("followUpDAO");
        checkMailService=(CheckMailService)context.getBean("checkMailService");
    }

    private static void addToDB(ArrayList<String> lines) {
        for (String line : lines) {
            String[] splitLine = line.split(delimiter);
            SearchInfo searchInfo = MiscUtil.getSearchInfo(splitLine);
            searchInfoDAO.add(searchInfo);
            Lead lead = getLead(splitLine[3], searchInfo);
            leadDAO.add(lead);
            User user = getUser(splitLine, lead);
            userDAO.add(user);
        }
    }

}
