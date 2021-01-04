package com.rbyte.dragernesdal.data.user.model;

public class ProfileDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int phone;

    private String passHash;
    private String salt;

    private boolean admin;
    private boolean mailConfirm;

    public ProfileDTO (){
    }

    //Constructer without admin/mailconfirm, for generation
    public ProfileDTO(int id, String firstName, String lastName, String email, int phone, String passHash, String salt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passHash = passHash;
        this.salt = salt;
        this.admin = false;
        this.mailConfirm = false;
    }

    //Constructer with admin/mailconfirm, for getting
    public ProfileDTO(int id, String firstName, String lastName, String email, int phone, String passHash, String salt, boolean admin, boolean mailConfirm) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passHash = passHash;
        this.salt = salt;
        this.admin = admin;
        this.mailConfirm = mailConfirm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isMailConfirm() {
        return mailConfirm;
    }

    public void setMailConfirm(boolean mailConfirm) {
        this.mailConfirm = mailConfirm;
    }
}
