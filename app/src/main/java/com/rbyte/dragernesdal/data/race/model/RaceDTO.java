package com.rbyte.dragernesdal.data.race.model;


public class RaceDTO {
    private int id;
    private String racename;
    private int start;
    private int ep2;
    private int ep3;
    private int ep4;

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public String getRacename() {
        return racename;
    }

    public void setRacename(String racename) {
        this.racename = racename;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEp2() {
        return ep2;
    }

    public void setEp2(int ep2) {
        this.ep2 = ep2;
    }

    public int getEp3() {
        return ep3;
    }

    public void setEp3(int ep3) {
        this.ep3 = ep3;
    }

    public int getEp4() {
        return ep4;
    }

    public void setEp4(int ep4) {
        this.ep4 = ep4;
    }
}

