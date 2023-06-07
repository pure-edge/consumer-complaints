package com.example.logingps.models;

import java.io.Serializable;
import java.util.Map;

public class MemberConsumerOwner implements Serializable {
    private String account_number;
    private String address;
    private String contact_number;
    private String name;

    public MemberConsumerOwner() {
    }

    public MemberConsumerOwner(String accountNumber, String address, String contactNumber, String name) {
        this.account_number = accountNumber;
        this.address = address;
        this.contact_number = contactNumber;
        this.name = name;
    }

    public MemberConsumerOwner(Object map) {
        Map<String, Object> member_owner_consumer = (Map<String, Object>) map;
        account_number = (String) member_owner_consumer.get("account_number");
        address = (String) member_owner_consumer.get("address");
        contact_number = (String) member_owner_consumer.get("contact_number");
        name = (String) member_owner_consumer.get("name");
    }

    public String getAccountNumber() {
        return account_number;
    }

    public void setAccountNumber(String accountNumber) {
        this.account_number = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contact_number;
    }

    public void setContactNumber(String contactNumber) {
        this.contact_number = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
