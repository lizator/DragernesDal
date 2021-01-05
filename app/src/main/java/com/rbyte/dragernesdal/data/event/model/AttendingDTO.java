package com.rbyte.dragernesdal.data.event.model;

public class AttendingDTO {
    private int idChar;
    private int idEvent;

    public AttendingDTO() {

    }

    public AttendingDTO(int idChar, int idEvent) {
        this.idChar = idChar;
        this.idEvent = idEvent;
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

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }
}
