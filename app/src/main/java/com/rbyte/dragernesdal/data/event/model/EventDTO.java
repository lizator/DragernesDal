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
    private int eventID;

    public EventDTO(){

    }

    public EventDTO(String name, String startDate, String endDate, String address, String info, int eventID){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.info = info;
        this.eventID = eventID;
    }
    public EventDTO(String name, String startDate, String endDate, String address, String info){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getStartDate() {
        System.out.println(LocalDateTime.parse(startDate) + "LocalDateTime");
        return LocalDateTime.parse(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getEndDate() {
        return LocalDateTime.parse(endDate);
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

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}