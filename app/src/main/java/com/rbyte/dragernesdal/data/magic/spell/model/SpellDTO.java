package com.rbyte.dragernesdal.data.magic.spell.model;


public class SpellDTO {
    private int id;
    private String spellname;
    private String desc;
    private String item;
    private String duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpellname() {
        return spellname;
    }

    public void setSpellname(String spellname) {
        this.spellname = spellname;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

