package com.project.kutaxi;

import android.graphics.drawable.Drawable;

public class ChatListItem {

    private String monthString;
    private String dayString;
    private String startString;
    private String endString;
    private String routeString;
    private String currentString;
    private String maxString;
    private String contentString;
    private Drawable genderDrawable;
    private String idString;
    private String hostString;
    private String guest1String;
    private String guest2String;
    private String dateString;

    public void setMonthString(String month) {
        monthString = month;
    }
    public void setDayString(String day) {
        dayString = day;
    }
    public void setStartString(String starttime) {
        startString = starttime;
    }
    public void setEndString(String endtime) {
        endString = endtime;
    }
    public void setRouteString(String route) {
        routeString = route;
    }
    public void setCurrentString(String currentmember) {
        currentString = currentmember;
    }
    public void setMaxString(String maxmember) {
        maxString = maxmember;
    }
    public void setContentString(String content) {
        contentString = content;
    }
    public void setGenderDrawable(Drawable gender) {
        genderDrawable = gender;
    }
    public void setIdString(String room_id) { idString = room_id; }
    public void setHostString(String room_host) { hostString = room_host; }
    public void setGuest1String(String guest1) { guest1String = guest1; }
    public void setGuest2String(String guest2) { guest2String = guest2; }
    public void setDateString(String date) { dateString = date; }

    public String getMonthString() {
        return this.monthString;
    }
    public String getDayString() {
        return this.dayString;
    }
    public String getStartString() {
        return this.startString;
    }
    public String getEndString() {
        return this.endString;
    }
    public String getRouteString() {
        return this.routeString;
    }
    public String getCurrentString() {
        return this.currentString;
    }
    public String getMaxString() {
        return this.maxString;
    }
    public String getContentString() {
        return this.contentString;
    }
    public Drawable getGenderDrawable() {
        return this.genderDrawable;
    }
    public String getIdString() { return this.idString; }
    public String getHostString() { return this.hostString; }
    public String getGuest1String() { return this.guest1String; }
    public String getGuest2String() { return this.guest2String; }
    public String getDateString() { return this.dateString; }

}