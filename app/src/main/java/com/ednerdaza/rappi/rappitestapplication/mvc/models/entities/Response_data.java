package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by administrador on 15/01/17.
 */
public class Response_data implements Serializable{
    public String modhash;
    public ArrayList<Children> children;
    public String after;
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
