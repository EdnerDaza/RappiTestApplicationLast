package com.ednerdaza.rappi.rappitestapplication.mvc.controllers.interfaces;

import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.Children;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntity;
import com.ednerdaza.rappi.rappitestapplication.mvc.models.entities.ItemEntityResponse;

/**
 * Created by administrador on 8/01/17.
 */
public interface DelegateItemAdapter {

    public void onItemClicked(Children children);

}
