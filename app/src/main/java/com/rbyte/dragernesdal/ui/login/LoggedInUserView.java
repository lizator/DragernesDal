package com.rbyte.dragernesdal.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private int id;
    private String email;
    private String passHash;
    private boolean admin = false;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String email, String passHash, int id) {
        this.displayName = displayName;
        this.email = email;
        this.passHash = passHash;
        this.id = id;
    }

    LoggedInUserView(String displayName, String email, String passHash, int id, boolean admin) {
        this.displayName = displayName;
        this.email = email;
        this.passHash = passHash;
        this.id = id;
        this.admin = admin;
    }

    String getDisplayName() {
        return this.displayName;
    }

    String getEmail() {return this.email;}

    String getPassHash() {return this.passHash;}

    int getId() {return this.id;}

    boolean getAdmin() {return this.admin;}
}