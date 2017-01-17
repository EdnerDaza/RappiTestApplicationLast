package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

import java.io.Serializable;

/**
 * Created by administrador on 15/01/17.
 */
public class ItemEntityResponse implements Serializable{

    public String kind;
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
