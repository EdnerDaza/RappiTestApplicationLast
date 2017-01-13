package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces;

/**
 * Created by administrador on 8/01/17.
 */
public interface ItemModelInterface<T> {

    public void completeSuccess(T entity);
    public void completeFail(String message);

}
