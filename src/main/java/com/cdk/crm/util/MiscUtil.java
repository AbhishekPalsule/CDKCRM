package com.cdk.crm.util;

import com.cdk.crm.domain.Lead;
import com.cdk.crm.domain.SearchInfo;
import com.cdk.crm.domain.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Created by palsulea on 9/6/2016.
 */
public class MiscUtil {
    public static String getFileName(HttpServletRequest request) {
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
    public static String getUserInfo(List<Object> objects) {
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
    public static SearchInfo getSearchInfo(String[] splitLine) {
        SearchInfo searchInfo = new SearchInfo();
        searchInfo.setMake(splitLine[4]);
        searchInfo.setModel(splitLine[5]);
        searchInfo.setCost(Float.parseFloat(splitLine[6]));
        searchInfo.setContent(splitLine[7]);
        searchInfo.setWebsite(splitLine[8]);
        return searchInfo;
    }

    public static User getUser(String[] splitLine, Lead lead) {
        User user = new User();
        user.setName(splitLine[0]);
        user.setContact_no(splitLine[1]);
        user.setEmail(splitLine[2]);
        user.setLeadId(lead);
        return user;
    }

    public static Lead getLead(String dateInString, SearchInfo searchInfo) {
        Lead lead = new Lead();
        lead.setDate(DateUtility.stringToDate(dateInString, "yyyy/MM/dd"));
        lead.setPromising(false);
        lead.setSearchInfoId(searchInfo);
        return lead;
    }
}

