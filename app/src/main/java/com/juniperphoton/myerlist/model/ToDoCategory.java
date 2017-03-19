package com.juniperphoton.myerlist.model;


import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.juniperphoton.myerlist.App;
import com.juniperphoton.myerlist.R;
import com.juniperphoton.myerlist.util.ColorUtil;
import com.juniperphoton.myerlist.util.LocalSettingUtil;
import com.juniperphoton.myerlist.util.Params;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ToDoCategory extends RealmObject {
    public static final String ID_KEY = "id";
    public static final String POSITION_KEY = "position";
    public static final String SID_KEY = "sid";

    public static final int ALL_ID = 0;
    public static final int DELETED_ID = -1;
    public static final int PERSONALIZATION_ID = -2;

    private String name;
    private String color;

    private int position;

    @PrimaryKey
    private int id;

    private String sid;

    public ToDoCategory() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public int getIntColor() {
        return Color.parseColor(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public static ToDoCategory getAllCategory() {
        ToDoCategory category = new ToDoCategory();
        category.setColor(ColorUtil.fromInt(ContextCompat.getColor(App.getInstance(), R.color.MyerListBlue)));
        category.setName(App.getInstance().getString(R.string.all));
        category.setId(ALL_ID);
        category.setSid(LocalSettingUtil.getString(App.getInstance(), Params.SID_KEY));
        return category;
    }

    public static ToDoCategory getDeletedCategory() {
        ToDoCategory category = new ToDoCategory();
        category.setColor(ColorUtil.fromInt(ContextCompat.getColor(App.getInstance(), R.color.DeleteColor)));
        category.setName(App.getInstance().getString(R.string.deleted));
        category.setId(DELETED_ID);
        return category;
    }

    public static ToDoCategory getPersonalizationCategory() {
        ToDoCategory category = new ToDoCategory();
        category.setColor(ColorUtil.fromInt(Color.WHITE));
        category.setName(App.getInstance().getString(R.string.personalization));
        category.setId(PERSONALIZATION_ID);
        return category;
    }

    public ToDoCategory getCopy() {
        ToDoCategory category = new ToDoCategory();
        category.name = name;
        category.id = id;
        category.color = color;
        category.position = position;
        return category;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
