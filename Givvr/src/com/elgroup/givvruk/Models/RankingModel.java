package com.elgroup.givvruk.Models;

/**
 * Created by Mangal on 11/7/2015.
 */
public class RankingModel {

    String user_id;
    String name;
    String profile_img_url;
    String city;
    String fb_id;
    String usertotal;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_img_url() {
        return profile_img_url;
    }

    public void setProfile_img_url(String profile_img_url) {
        this.profile_img_url = profile_img_url;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getUsertotal() {
        return usertotal;
    }

    public void setUsertotal(String usertotal) {
        this.usertotal = usertotal;
    }
}
