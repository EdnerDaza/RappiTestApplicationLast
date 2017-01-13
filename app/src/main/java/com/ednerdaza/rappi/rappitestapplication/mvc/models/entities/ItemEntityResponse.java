package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by EDNER on 11/01/2017.
 */

public class ItemEntityResponse implements Serializable{

    //@SerializedName("kind")
    public String kind;
    //@SerializedName("data")
    public Response_data data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Response_data getData() {
        return data;
    }

    public void setData(Response_data data) {
        this.data = data;
    }
}
