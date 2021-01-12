package com.rbyte.dragernesdal.data.event.model;

public class CheckInDTO {
    private int idChar;
    private int idEvent;
    private int checkin;

    public CheckInDTO() {

    }

    public CheckInDTO(int idChar, int idEvent, int checkin) {
        this.idChar = idChar;
        this.idEvent = idEvent;
        this.checkin = checkin;
    }

    public int getIdChar() {
        return idChar;
    }

    public void setIdChar(int idChar) {
        this.idChar = idChar;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public int getCheckin() {
        return checkin;
    }

    public void setCheckin(int checkin) {
        this.checkin = checkin;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
}
