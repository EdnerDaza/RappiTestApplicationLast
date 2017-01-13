package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Children implements Serializable {
    //@SerializedName("kind")
    public String kind;
    //@SerializedName("data")
    public Data data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
