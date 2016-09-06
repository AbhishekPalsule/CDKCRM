package com.cdk.crm.domain;

import javax.persistence.*;

@Entity
@Table(name = "followup_t")
public class FollowUp {

    @Id
    @Column(name = "lead_id")
    private int leadId;
    @Column
    private String currentFollowUpStatus;
    @Column
    private String currentResponse;
    @Column
    private int followUpCount;

    public FollowUp() {
    }

    public FollowUp(int leadId, String currentFollowUpStatus, String currentResponse, int followUpCount) {
        this.leadId = leadId;
        this.currentFollowUpStatus = currentFollowUpStatus;
        this.currentResponse = currentResponse;
        this.followUpCount = followUpCount;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public String getCurrentFollowUpStatus() {
        return currentFollowUpStatus;
    }

    public void setCurrentFollowUpStatus(String currentFollowUpStatus) {
        this.currentFollowUpStatus = currentFollowUpStatus;
    }

    public String getCurrentResponse() {
        return currentResponse;
    }

    public void setCurrentResponse(String currentResponse) {
        this.currentResponse = currentResponse;
    }

    public int getFollowUpCount() {
        return followUpCount;
    }

    public void setFollowUpCount(int followUpCount) {
        this.followUpCount = followUpCount;
    }
}