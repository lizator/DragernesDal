package com.example.dragernesdal.data;

public class WebServerPointer {

    /**
     * Class for development to easily be able to switch between using a local server and using main server
     * Change "useLocalServer" to true and change LOCAL_WEB_SERVER_IP to point correctly before running
     *
     * Make sure useLocalServer = false when pushing!!!!
     */

    private static final boolean useLocalServer = false;


    private static final String MAIN_WEB_SERVER_IP = "http://80.197.112.212:25572";
    private static final String LOCAL_WEB_SERVER_IP = "http://10.16.172.233:25572";

    public static String getServerIP(){
        if (useLocalServer) return LOCAL_WEB_SERVER_IP;
        return MAIN_WEB_SERVER_IP;
    }
}