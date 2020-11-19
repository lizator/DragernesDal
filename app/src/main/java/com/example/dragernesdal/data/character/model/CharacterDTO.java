package com.example.dragernesdal.data.character.model;

public class CharacterDTO {
    int idcharacter;
    int iduser;
    String name;
    int idrace;
    String raceName;
    int age;
    int currentep;
    String status;
    String date;
    String timestamp;
    int strength;
    int health;
    String background;

    public CharacterDTO() {
    }

    public CharacterDTO(int idcharacter, int iduser, String name, int idrace, int age) {
        this.idcharacter = idcharacter;
        this.iduser = iduser;
        this.name = name;
        this.idrace = idrace;
        this.age = age;
    }

    public CharacterDTO(int idcharacter, int iduser, String name, int idrace, String raceName, int age, int currentep, String status, String date, String timestamp, int strength, int health) {
        this.idcharacter = idcharacter;
        this.iduser = iduser;
        this.name = name;
        this.idrace = idrace;
        this.raceName = raceName;
        this.age = age;
        this.currentep = currentep;
        this.status = status;
        this.date = date;
        this.timestamp = timestamp;
        this.strength = strength;
        this.health = health;
    }

    public int getIdcharacter() {
        return idcharacter;
    }

    public void setIdcharacter(int idcharacter) {
        this.idcharacter = idcharacter;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdrace() {
        return idrace;
    }

    public void setIdrace(int idrace) {
        this.idrace = idrace;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCurrentep() {
        return currentep;
    }

    public void setCurrentep(int currentep) {
        this.currentep = currentep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}