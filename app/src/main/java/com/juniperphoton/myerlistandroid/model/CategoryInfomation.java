package com.juniperphoton.myerlistandroid.model;


import java.util.List;

public class CategoryInfomation {

    private boolean modified;
    private List<ToDoCategory> cates;

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public List<ToDoCategory> getCates() {
        return cates;
    }

    public void setCates(List<ToDoCategory> cates) {
        this.cates = cates;
    }
}
