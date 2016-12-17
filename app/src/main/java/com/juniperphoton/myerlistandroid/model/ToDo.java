package com.juniperphoton.myerlistandroid.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ToDo extends RealmObject {
    @PrimaryKey
    private String id;

    private String sid;
    private String time;
    private String content;
    private String isdone;
    private String cate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsdone() {
        return isdone;
    }

    public void setIsdone(String isdone) {
        this.isdone = isdone;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }
}
