/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package service;

import java.util.ArrayList;

import main.Trade;

public interface IFinder {
	public ArrayList<Trade> getTrades();
	public boolean updateItems();
}
