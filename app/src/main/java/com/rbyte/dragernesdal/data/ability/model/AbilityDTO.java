package com.rbyte.dragernesdal.data.ability.model;

public class AbilityDTO {
    private int id;
    private String name;
    private int cost;
    private String desc;
    private String type;
    private int pagenumber;
    private String command;

    public AbilityDTO (String name, String desc){
        this.name = name;
        this.desc = desc;
    }

    public AbilityDTO (int id, String name, int cost, String desc, String type, int pagenumber, String command) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.desc = desc;
        this.type = type;
        this.pagenumber = pagenumber;
        this.command = command;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}