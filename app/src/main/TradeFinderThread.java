/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package main;

import java.util.ArrayList;

import service.IFinder;
import window.TradesListModel;

public class TradeFinderThread implements Runnable{
	
	public volatile boolean running;
	
	IFinder finder;
	TradeFinder manager;
	private boolean update;
	
	private ArrayList<Trade> trades;
	
	public TradeFinderThread(IFinder f, TradeFinder m, boolean update){
		finder=f;
		manager=m;
		trades = new ArrayList<Trade>();
		running = true;
		//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	}
	
	public void run(){
		if(update)
			finder.updateItems();
		while(running){
			try {
				Thread.sleep(3000);
				trades = finder.getTrades();
				for(Trade trade : trades){
					if(!manager.cache.contains(trade.getUrl())){
						manager.cache.add(trade.getUrl());
						manager.addTrade(trade);
					}
				}
				TradesListModel.check();
			} catch (InterruptedException e) {
				running = false;
				if(TradesListModel.reset())
					System.out.println("Reset.");
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

}
