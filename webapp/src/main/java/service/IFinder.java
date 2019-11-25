package service;

import main.Trade;

import java.util.ArrayList;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */
public interface IFinder {
    public ArrayList<Trade> getTrades();
    public boolean updateItems();
}
