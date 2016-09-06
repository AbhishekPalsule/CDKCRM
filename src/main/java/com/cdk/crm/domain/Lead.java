package com.cdk.crm.domain;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "lead_t")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_id")
    private int leadId;
    @Column
    private boolean isPromising;
    @Column
    private Date date;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)

    private SearchInfo searchInfoId;

    public Lead() {
    }


    public Lead(boolean isPromising, Date date) {
        this.isPromising = isPromising;
        this.date = date;
    }

    public SearchInfo getSearchInfoId() {
        return searchInfoId;
    }

    public void setSearchInfoId(SearchInfo searchInfoId) {
        this.searchInfoId = searchInfoId;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public boolean isPromising() {
        return isPromising;
    }

    public void setPromising(boolean promising) {
        isPromising = promising;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Lead{" +
                "leadId=" + leadId +
                ", isPromising=" + isPromising +
                ", date=" + date +
                ", searchInfoId=" + searchInfoId +
                '}';
    }
}
