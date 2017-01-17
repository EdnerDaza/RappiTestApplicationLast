package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

import java.io.Serializable;

/**
 * Created by administrador on 15/01/17.
 */
public class Children implements Serializable {
    public String kind;
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
