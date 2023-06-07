package com.example.logingps.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class CrewWorkOrder implements Serializable {
    private String id;  // document id from account/{crewId}/work_orders subcollection
    private String assigned_by;
    @ServerTimestamp
    private Date date_assigned;
    private Date date_completed;
    private String description;
    private MemberConsumerOwner member_consumer_owner;
    private String work_order_id;   // document id from worker_order collection

    public CrewWorkOrder() {
    }

    public CrewWorkOrder(String id, String assignedBy, Date dateCreated, Date dateCompleted, String description, MemberConsumerOwner memberConsumerOwner, String workOrderId) {
        this.id = id;
        this.assigned_by = assignedBy;
        this.date_assigned = dateCreated;
        this.date_completed = dateCompleted;
        this.description = description;
        this.member_consumer_owner = memberConsumerOwner;
        this.work_order_id = workOrderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssigned_by() {
        return assigned_by;
    }

    public void setAssigned_by(String assigned_by) {
        this.assigned_by = assigned_by;
    }

    public Date getDate_assigned() {
        return date_assigned;
    }

    public void setDate_assigned(Date date_assigned) {
        this.date_assigned = date_assigned;
    }

    public Date getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(Date date_completed) {
        this.date_completed = date_completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MemberConsumerOwner getMember_consumer_owner() {
        return member_consumer_owner;
    }

    public void setMember_consumer_owner(MemberConsumerOwner member_consumer_owner) {
        this.member_consumer_owner = member_consumer_owner;
    }

    public String getWork_order_id() {
        return work_order_id;
    }

    public void setWork_order_id(String work_order_id) {
        this.work_order_id = work_order_id;
    }
}
