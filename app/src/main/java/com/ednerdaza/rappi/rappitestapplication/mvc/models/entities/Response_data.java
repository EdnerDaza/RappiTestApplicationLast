package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response_data implements Serializable{
    //@SerializedName("modhash")
    public String modhash;
    //@SerializedName("children")
    public ArrayList<Children> children;
    //@SerializedName("after")
    public String after;
    //@SerializedName("before")
    public String before;

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public ArrayList<Children> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Children> children) {
        this.children = children;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }
}
