package com.cdk.crm.dto;

import java.util.Date;

public class ResponseData {

    private String userName;
    private String contact_no;
    private String email;
    private Date date;
    private String make;
    private String model;
    private float cost;
    private String content;
    private int lead_id;
    private String website;

    public ResponseData() {
    }

    public ResponseData(String userName, String contact_no, String email, Date date, String make, String model, float cost, String content, int lead_id, String website) {
        this.userName = userName;
        this.contact_no = contact_no;
        this.email = email;
        this.date = date;
        this.make = make;
        this.model = model;
        this.cost = cost;
        this.content = content;
        this.lead_id = lead_id;
        this.website = website;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLead_id() {
        return lead_id;
    }

    public void setLead_id(int lead_id) {
        this.lead_id = lead_id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "userName='" + userName + '\'' +
                ", contact_no='" + contact_no + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", cost=" + cost +
                ", content='" + content + '\'' +
                ", lead_id=" + lead_id +
                ", website='" + website + '\'' +
                '}';
    }
}
