package com.rbyte.dragernesdal.data.event.model;

import java.sql.Timestamp;
import java.util.Date;

public class EventDTO {
    private String name;
    private Timestamp startDate;
    private Timestamp endDate;
    private String address;
    private String info;
    private int eventID;

    public EventDTO(){

    }

    public EventDTO(String name, Timestamp startDate, Timestamp endDate, String address, String info, int eventID){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.info = info;
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}