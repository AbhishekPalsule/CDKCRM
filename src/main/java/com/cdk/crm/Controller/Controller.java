package com.cdk.crm.Controller;

import com.cdk.crm.dao.*;
import com.cdk.crm.domain.FollowUp;
import com.cdk.crm.domain.Lead;
import com.cdk.crm.domain.SearchInfo;
import com.cdk.crm.domain.User;
import com.cdk.crm.service.CheckMailService;
import com.cdk.crm.service.MailService;
import com.cdk.crm.util.CSVUtility;
import com.cdk.crm.util.DateUtility;
import com.cdk.crm.util.JSONUtil;
import com.cdk.crm.util.mailUtility.NegativeFollowUp;
import com.cdk.crm.util.mailUtility.OfferFollowUp;
import com.cdk.crm.util.mailUtility.PositiveFollowUp;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by palsulea on 9/4/2016.
 */
@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private UserDAO userDAO ;
    @Autowired
    private LeadDAO leadDAO ;
    @Autowired
    private SearchInfoDAO searchInfoDAO ;

    @Autowired
    private ResponseDAO responseDAO ;

    @Autowired
    private MailService mailService;

    @Autowired
    private FollowUpDAO followUpDAO;

    @Autowired
    CheckMailService checkMailService ;



    public FollowUpDAO getFollowUpDAO() {
        return followUpDAO;
    }

    public void setFollowUpDAO(FollowUpDAO followUpDAO) {
        this.followUpDAO = followUpDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public LeadDAO getLeadDAO() {
        return leadDAO;
    }

    public void setLeadDAO(LeadDAO leadDAO) {
        this.leadDAO = leadDAO;
    }

    public SearchInfoDAO getSearchInfoDAO() {
        return searchInfoDAO;
    }

    public void setSearchInfoDAO(SearchInfoDAO searchInfoDAO) {
        this.searchInfoDAO = searchInfoDAO;
    }

    public ResponseDAO getResponseDAO() {
        return responseDAO;
    }

    public void setResponseDAO(ResponseDAO responseDAO) {
        this.responseDAO = responseDAO;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public  String getFileName(HttpServletRequest request) {
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
        String fileName = null;
        File file = null;
        try {
            List<FileItem> list = servletFileUpload.parseRequest(request);
            for (FileItem f: list){
                file = new File(f.getName());
                f.write(file);
            }
            System.out.println(file.getAbsolutePath());
            fileName = file.getAbsolutePath();
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @RequestMapping(value = "/insertDataInDB", method = RequestMethod.POST)
    public ModelAndView insertData(HttpServletRequest request, final HttpServletResponse response){
        final  long DELAY = 1000l;
        final  long INTERVAL= 1000l;
        String fileName = getFileName(request);
        System.out.println("Fetching uploaded csv");
        String delimiter = ",";
        ArrayList<String> lines = CSVUtility.readLine(fileName);

        for (String line : lines) {
            String[] splitLine = line.split(delimiter);
            SearchInfo searchInfo = getSearchInfo(splitLine);
            searchInfoDAO.add(searchInfo);
            Lead lead = getLead(splitLine[3], searchInfo);
            leadDAO.add(lead);
            User user = getUser(splitLine, lead);
            userDAO.add(user);
        }
        System.out.println("Sending for conversion");
        String jsonString = JSONUtil.getJSONConvertedData(responseDAO.getAllDetailsOfLeadsByDate());
        System.out.println("initiate mailing");
        String ids = doSendEmail(jsonString);
        followUpUpdate(ids);
        TimerTask checkMailTask = new TimerTask() {
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
        new Timer().schedule(checkMailTask,DELAY,INTERVAL);
        return null;
    }
    public String reply(String userInfo){
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
                    System.out.println("STATUS:"+status);
                    statuses += sendForPositiveResponse(leadId, count, address) + ",";
                }
                if (status.trim().equalsIgnoreCase("No")) {
                    System.out.println("STATUS:"+status);
                    statuses += sendForNegativeResponse(leadId, count, address) + ",";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statuses;
    }
    public void cleanDatabase(int leadId){
            followUpDAO.delete(leadId);
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
        }
        return "";
    }

    private void sendOffers(){
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

    private void sendThankYou(){
        List<Object> followUps = followUpDAO.getNegativeResponses();
        String userInfo = getUserInfo(followUps);
        if(!userInfo.equals("")) {
            String[] eligibleCustomers = userInfo.split(" ");
            for (String eachCustomer : eligibleCustomers) {
                String[] customerInfo = eachCustomer.split(",");
                sendThankYouMsg(Long.valueOf((customerInfo[0])), customerInfo[3]);
                cleanDatabase(Integer.parseInt(customerInfo[0]));
            }
        }
    }


    private boolean sendOfferMsg(String address) {
        OfferFollowUp followUp = new OfferFollowUp();
        System.out.println("send Offer Mail to "+ address);
        boolean b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage());
        return b;

    }

    private boolean sendReqAckMsg(String address) {
        boolean b =false;
        try {
            System.out.println("send Request Ack Mail to "+ address);
            PositiveFollowUp followUp = new PositiveFollowUp();
            b = mailService.sendMail(address, followUp.getSubject(), followUp.getMailMessage("Pooja", "her", "1234567"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return b;
    }
    public String fetchUpdatedFollowUp() {
        List<Object> followUps = followUpDAO.getAllLead();
        System.out.println("Fetch from followUp_t");
        String userInfo="";
        userInfo += getUserInfo(followUps);
        System.out.println("sending for reply");
        return userInfo;
    }

    public List<String> readMail(){
        List<String> responses =null;
        try {
            System.out.println("checking");
            responses = checkMailService.checkMail();
            System.out.println("updatingMailResponses");
            checkMailService.closeResources();
        }catch (Exception e){
            e.printStackTrace();
        }
        return responses;
    }
    public void updateMailResponses(List<String> responses){
        System.out.println("Updating Responses");
        for (String lead : responses) {
                String leadInfo[] = lead.split(",");
                FollowUp followUp = followUpDAO.get(Integer.parseInt(leadInfo[0]));
                followUp.setCurrentResponse(leadInfo[1].trim());
                followUpDAO.update(followUp);
            }
        System.out.println("Updated");
    }
    public void followUpUpdate(String ids){
        System.out.println("initiating follow up");
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
        System.out.println("db updated");

    }

    private void updateExistingFollowUp(FollowUp followUp) {
        followUp.setFollowUpCount(followUp.getFollowUpCount() + 1);
        followUp.setCurrentFollowUpStatus("Ongoing");
        followUpDAO.update(followUp);
    }

    private void createNewFollowUp(String id) {
        FollowUp followUp = new FollowUp();
        followUp.setLeadId(Integer.parseInt(id));
        followUp.setFollowUpCount(1);
        System.out.println(followUp.getFollowUpCount());
        followUp.setCurrentFollowUpStatus("Initiated");
        followUpDAO.update(followUp);
    }

    public String doSendEmail(String jsonString){
        String ids= mailService.sendFollowUps(jsonString);
        System.out.println("all messages sent");
        return ids;
    }

    public  String getUserInfo(List<Object> objects) {
        String userInfo="";
        try{
            for (Object object : objects) {
                Object []obj = (Object[])object;
                for(Object values:obj){
                    userInfo+= values + ",";
                }
                userInfo = userInfo.substring(0,userInfo.length() - 1);
                userInfo += " ";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(userInfo);
        return userInfo;
    }
    public SearchInfo getSearchInfo(String[] splitLine) {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setMake(splitLine[4]);
        searchInfo.setModel(splitLine[5]);
        searchInfo.setCost(Float.parseFloat(splitLine[6]));
        searchInfo.setContent(splitLine[7]);
        searchInfo.setWebsite(splitLine[8]);
        return searchInfo;
    }

    public User getUser(String[] splitLine, Lead lead) {
        User user = new User();
        user.setName(splitLine[0]);
        user.setContact_no(splitLine[1]);
        user.setEmail(splitLine[2]);
        user.setLeadId(lead);
        return user;
    }

    public Lead getLead(String dateInString, SearchInfo searchInfo) {
        Lead lead = new Lead();
        lead.setDate(DateUtility.stringToDate(dateInString, "yyyy/MM/dd"));
        lead.setPromising(false);
        lead.setSearchInfoId(searchInfo);
        return lead;
    }
}
