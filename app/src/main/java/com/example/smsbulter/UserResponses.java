package com.example.smsbulter;

public class UserResponses {
    private String message;
    private boolean active;

    public UserResponses(String message, boolean active){
        this.message = message;
        this.active = active;
    }

    public String getMessage(){
        return message;
    }

    public boolean isActive(){
        return active;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}
