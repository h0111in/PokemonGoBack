package Listeners;

import Model.Card;

import java.util.EventListener;

/**
 * Created by Hosein on 6/12/2017.
 */
public interface CardEventListener extends EventListener {

    void cardModified(String property, Card card);
}
