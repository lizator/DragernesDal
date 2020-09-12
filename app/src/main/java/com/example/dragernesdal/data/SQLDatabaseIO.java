package com.example.dragernesdal.data;

import java.sql.Statement;
import java.sql.*;

public class SQLDatabaseIO {
    private String DatabaseURL;
    private final String user;
    private final String pass;
    private final String url;
    private final int port;
    private String db_name = "companiondb";
    private boolean connected = false;
    private Connection conn = null;
    private Statement stmt = null;


    public SQLDatabaseIO(String USER, String PASS, String URL, int PORT) {
        this.user = USER;
        this.pass = PASS;
        this.url = URL;
        this.port = PORT;
        this.DatabaseURL = "jdbc:mysql://" + URL + ":"+PORT+"/"+db_name+"?characterEncoding=latin1&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    public boolean isConnected() {
        return connected;
    }

    public void setDB(String db) throws SQLException {
        this.db_name = db;
        this.DatabaseURL = "jdbc:mysql://" + url + ":"+port+"/"+db_name+"?characterEncoding=latin1&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        connect();
        query("use "+db);
        close();
    }

    public void connect() throws SQLException {
        if(!connected){
            String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                throw new SQLException();
            }
            conn = DriverManager.getConnection(DatabaseURL, user, pass);
            connected = true;
        }
    }

    public void update(String query) throws SQLException {
        if(connected){
            stmt = conn.createStatement();
            stmt.executeUpdate("use "+db_name);
            stmt.executeUpdate(query);
        }
    }

    public ResultSet query(String query) throws SQLException {
        ResultSet result = null;
        if(!connected){
            System.out.println("Connect to a DB first");
        } else{
            stmt = conn.createStatement();
            stmt.executeUpdate("use "+db_name);
            result = stmt.executeQuery(query);
        }
        return result;
    }

    public void close() throws SQLException {
        if(connected){
            conn.close();
            connected=false;
        }
    }

}
