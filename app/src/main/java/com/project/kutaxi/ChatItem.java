package com.project.kutaxi;

public class ChatItem  {
    private String nickname; // 닉네임
    private String message; // 메시지
    private String time; // 시간
    private String host;

    public ChatItem() {
    }

    public void setNickName(String nickname) {this.nickname = nickname;}
    public String getNickname() {return nickname;}
    public void setMessage(String message) {this.message = message;}
    public String getMessage() {return message;}
    public void setTime(String time) {this.time=time;}
    public String getTime() {return time;}
    public void setHost(String host) {this.host=host;}
    public String getHost() {return host;}

}
