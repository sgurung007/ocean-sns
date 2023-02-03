package com.example.oceansns.model;

import org.springframework.stereotype.Component;

@Component
public class MYIP {
    private String IP="defult";
    private boolean updated=false;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
