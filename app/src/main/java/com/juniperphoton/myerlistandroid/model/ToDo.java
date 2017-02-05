package com.juniperphoton.myerlistandroid.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ToDo extends RealmObject implements Cloneable {
    public static final String ID_KEY = "id";
    public static final String SID_KEY = "sid";
    public static final String ISDONE_KEY = "";
    public static final String CATE_KEY = "cate";
    public static final String DELETED_KEY = "deleted";
    public static final String POSITION_KEY = "position";
    public static final String IS_DONE = "1";
    public static final String IS_NOT_DONE = "0";

    @PrimaryKey
    private String id;

    private String sid;
    private String time;
    private String content;
    private String isdone;
    private String cate;
    private boolean deleted;
    private int position;

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public Object clone() {
        ToDo newToDo = new ToDo();
        newToDo.setDeleted(deleted);
        newToDo.setTime(time);
        newToDo.setIsdone(isdone);
        newToDo.setCate(cate);
        newToDo.setContent(content);
        newToDo.setId(id);
        newToDo.setSid(sid);
        return newToDo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
