package com.example.oceansns.pureJava;

import java.net.UnknownHostException;

import static java.net.InetAddress.getLocalHost;
public class HostAddressFinder {
    public static void getHostAddress(){
        String hostAddress = null;
        try {
            hostAddress = getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("error in static block, error message is: "+ e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("myIpAddress: "+hostAddress);
        hostAddress = hostAddress.replace(".", "_");
        System.setProperty("surajQueue", hostAddress);
    }
}
