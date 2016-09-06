package com.cdk.crm.domain;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "searchInfo_t")
@Entity
public class SearchInfo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "searchId")
    private int searchId;
    @Column
    private String make;
    @Column
    private float cost;
    @Column
    private String content;
    @Column
    private String model;
    @Column
    private String website;

    public SearchInfo(String make, float cost, String content, String model,String website) {
        this.make = make;
        this.cost = cost;
        this.content = content;
        this.model = model;
        this.website = website;
    }

    public SearchInfo() {
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
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

    @Override
    public String toString() {
        return "SearchInfo{" +
                "searchId=" + searchId +
                ", make='" + make + '\'' +
                ", cost=" + cost +
                ", content='" + content + '\'' +
                ", model='" + model + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
