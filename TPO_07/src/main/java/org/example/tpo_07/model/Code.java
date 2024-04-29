package org.example.tpo_07.model;

import java.io.Serializable;
import java.util.Calendar;

public class Code implements Serializable {

    private String id;
    private String code;
    private Calendar time;

    public Code(String id, String code){
        this.id = id;
        this.code = code;
        this.time = Calendar.getInstance();
    }

    public Code(){}

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Calendar getTime(){
        return time;
    }

    public void setTime(Calendar time){
        this.time = time;
    }

    @Override
    public String toString() {
        return code;
    }
}
