package com.rbyte.dragernesdal.data.event.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class EventDTO {
    private String name;
    private String startDate;
    private String endDate;
    private String address;
    private String info;
    private String hyperlink;
    private int eventID;

    public EventDTO(){

    }

    public EventDTO(String name, String startDate, String endDate, String address, String info, String hyperlink, int eventID) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.info = info;
        this.hyperlink = hyperlink;
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        //System.out.println(LocalDateTime.parse(startDate) + "LocalDateTime");
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}