/*
package com.cdk.crm.Controller;

import com.cdk.crm.dao.*;
import com.cdk.crm.domain.FollowUp;
import com.cdk.crm.dto.ResponseData;
import com.cdk.crm.util.CSVUtility;
import com.cdk.crm.domain.Lead;
import com.cdk.crm.domain.SearchInfo;
import com.cdk.crm.domain.User;
import com.cdk.crm.util.DateUtility;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DataController {

    @Autowired
    private UserDAO userDAO ;
    @Autowired
    private LeadDAO leadDAO ;
    @Autowired
    private SearchInfoDAO searchInfoDAO ;

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

    @RequestMapping(value = "/insertData", method = RequestMethod.POST)
    public ModelAndView insertData(HttpServletRequest request, HttpServletResponse response){
        String fileName = getFileName(request);
        System.out.println("Fetching uploaded csv");
        String delimiter = ",";
        ArrayList<String> lines = CSVUtility.readLine(fileName);

            for (String line : lines) {
                String[] splitLine = line.split(delimiter);
                SearchInfo searchInfo = getSearchInfo(splitLine);
                System.out.println(searchInfoDAO.add(searchInfo));
                Lead lead = getLead(splitLine[3], searchInfo);
                System.out.println(leadDAO.add(lead));
                User user = getUser(splitLine, lead);
                System.out.println(userDAO.add(user));
            }
            System.out.println("Sending for conversion");
            //request.getRequestDispatcher("getJSONConvertedData.do").forward(request,response);
        return new ModelAndView("redirect:getJSONConvertedData.do");
    }

    private User getUser(String[] splitLine, Lead lead) {
        User user = new User();
        user.setName(splitLine[0]);
        user.setContact_no(splitLine[1]);
        user.setEmail(splitLine[2]);
        user.setLeadId(lead);
        return user;
    }

    private Lead getLead(String dateInString, SearchInfo searchInfo) {
        Lead lead = new Lead();
        lead.setDate(DateUtility.stringToDate(dateInString, "yyyy/MM/dd"));
        lead.setPromising(false);
        lead.setSearchInfoId(searchInfo);
        return lead;
    }

    private SearchInfo getSearchInfo(String[] splitLine) {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setMake(splitLine[4]);
        searchInfo.setModel(splitLine[5]);
        searchInfo.setCost(Float.parseFloat(splitLine[6]));
        searchInfo.setContent(splitLine[7]);
        searchInfo.setWebsite(splitLine[8]);
        return searchInfo;
    }

    private String getFileName(HttpServletRequest request) {
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

    @Autowired
    ResponseDAO responseDAO ;

    @RequestMapping(value = "/getJSONConvertedData", method = RequestMethod.GET)
    public ModelAndView getJSONConvertedData(HttpServletRequest request , HttpServletResponse response) {

        List<ResponseData> responseList = responseDAO.getAllDetailsOfLeadsByDate();
       // System.out.println(responseList.size());
        String jsonString="[";
        for(ResponseData responseData:responseList){
            jsonString += new Gson().toJson(responseData)+",";
        }
        jsonString = jsonString.substring(0,jsonString.length()-1);
        jsonString+="]";
        System.out.println(jsonString);

        System.out.println("Sending for mail");
        try {

            return new ModelAndView("redirect:sendEmail.do","json",jsonString);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Autowired
    FollowUpDAO followUpDAO = null;

    @RequestMapping(value = "/updateMailResponses", method = RequestMethod.GET )
    public ModelAndView updateMailResponses(HttpServletRequest request, HttpServletResponse response){
        String data = request.getParameter("mailResponses");
                if (data!=null && !data.equals("")){
                String leads[] = data.split(" ");
                for (String lead : leads) {
                    String leadInfo[] = lead.split(",");
                    FollowUp followUp = new FollowUp();
                    followUp.setLeadId(Integer.parseInt(leadInfo[0]));
                    followUp.setCurrentResponse(leadInfo[1]);
                    followUp.setFollowUpCount(followUp.getFollowUpCount() + 1);
                    followUpDAO.update(followUp);

                }
                }else{
                    return new ModelAndView("redirect:readMail.do");
                }
        return new ModelAndView("redirect:fetchUpdatedFollowUp.do");
    }

    @RequestMapping(value = "/initiatedFollowUp", method = RequestMethod.GET)
    public ModelAndView initiatedFollowUpUpdate(HttpServletRequest request, HttpServletResponse response){
        System.out.println("done");
        String ids = request.getParameter("ids");
        String idArray[] = ids.split(",");
        for (String id: idArray){
          FollowUp followUp = new FollowUp();
            followUp.setLeadId(Integer.parseInt(id));
            followUp.setCurrentFollowUpStatus("Initiated");
            followUpDAO.update(followUp);
        }
        System.out.println("I'm going there");
        return new ModelAndView("redirect:readMail.do");
    }
    @RequestMapping(value = "/fetchUpdatedFollowUp", method = RequestMethod.GET)
    public ModelAndView fetchUpdatedFollowUp(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("A");
        List<Object> followUps = followUpDAO.getAllLead();
        String userInfo="";
         userInfo += getUserInfo(followUps);

        //System.out.println(jsonString);
        System.out.println("sending for reply");
        return new ModelAndView("redirect:reply.do","json",userInfo);
    }

    public static String getUserInfo(List<Object> objects) {
        String userInfo="";
        try{
        for (Object object : objects) {
            Object []obj = (Object[])object;
            for(Object values:obj){
                userInfo+= values + ",";
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(userInfo);
        return userInfo;
    }
}
*/
