package com.tech.givvruk.utils;

public class Friends {

    private String FRIEND_NAME;
    private String FRIEND_ID;

    public Friends(String fRIEND_NAME, String fRIEND_ID) {
        super();
        FRIEND_NAME = fRIEND_NAME;
        FRIEND_ID = fRIEND_ID;
    }

    public String getFRIEND_NAME() {
        return FRIEND_NAME;
    }

    public void setFRIEND_NAME(String fRIEND_NAME) {
        FRIEND_NAME = fRIEND_NAME;
    }

    public String getFRIEND_ID() {
        return FRIEND_ID;
    }

    public void setFRIEND_ID(String fRIEND_ID) {
        FRIEND_ID = fRIEND_ID;
    }

}
