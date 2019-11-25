/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import mail.Email;
import reddit.Reddit;
import rltracker.RLTracker;
import rltrading.RLTrading;
import window.TradesListModel;

public class TradeFinder {
	
	 public static final long TIME = 12;
	 
	 SimpleDateFormat sdf;
	
	 private ArrayList<Trade> trades;
	 ArrayList<String> cache;
	 
	 ExecutorService finders;
	 ScheduledExecutorService scheduler;
	 private int i;
	 private int index;
	 
	 //private Semaphore semProd;
	 private Semaphore semCons;
	 
	 private Item have;
	 private Item want;
	 private String platform;
	 private boolean update;
	 
	 private TradesListModel lm;
	 
	 public TradeFinder(){
		 java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	 }
	 
	 public TradeFinder(Item have, Item want, String platform, TradesListModel lm, boolean update){
		 trades = new ArrayList<Trade>();
		 cache = new ArrayList<String>(50);
		 //semProd = new Semaphore(50);
		 semCons = new Semaphore(0);
		 this.have = have;
		 this.want = want;
		 this.platform = platform;
		 this.update = update;
		 sdf = new SimpleDateFormat("HH:mm:ss");
		 this.lm = lm;
		 i = 0;
		 index = 0;
		 Email.add("Main - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName()
				 +"\nMain - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName()
				 +"\nMain - [P] "+platform
				 +"\n\n");
	 }
	 
	 public void start(){
		 
		cache.clear();
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		 
		finders = Executors.newFixedThreadPool(3);
		 
		finders.execute(new TradeFinderThread(new RLTracker(have, want, platform), this, update));
		System.out.println("RL-Tracker finder started.");
		finders.execute(new TradeFinderThread(new RLTrading(have, want, platform), this, update));
		System.out.println("RL-Trading finder started.");
		finders.execute(new TradeFinderThread(new Reddit(have, want, platform), this, update));
		System.out.println("Reddit finder started.");
		
		/*
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Scheduler(this), TIME, TIME, TimeUnit.HOURS);
		System.out.println("Scheduler started.");
		*/
		
	 }
	 
	 public void stop(){
		 if(finders.shutdownNow() != null && scheduler.shutdownNow() != null)
			 System.out.println("Finders & scheduler shutted down.");
	 }
	 
	 public ArrayList<String> update(){
		 ArrayList<String> new_items = new ArrayList<String>((new RLTracker().downloadItems()));
		 return new_items;
	 }
	 
	 public void addTrade(Trade trade){
		 try {
			//semProd.acquire();
			synchronized(this){
				trades.add(trade);
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {
						lm.add(trade);
					}
					
				});
				trade.print(i);
				i++;
			}
		} finally{
			semCons.release();
		}
	 }
	 
	 public boolean updateTrades(){
		 Iterator<Trade> it = trades.iterator();
		 try {
			semCons.acquire();
			synchronized(this){
				index = 0;
				cache.clear();
				while(it.hasNext()){
					Trade current = it.next();
					if(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()-current.getTime())>TIME){
						it.remove();
						System.out.println("------------------------ "+current.getUsername()+" "+current.getUrl()+" removed. {"+index+"}");
						SwingUtilities.invokeLater(new Runnable(){

							@Override
							public void run() {
								lm.remove();
							}
							
						});
					}
					index++;
				}
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} finally{
			//semProd.release();
		}
		 return false;
	 }
	 
	 public void print(){
		 for(Trade trade : trades){
			 trade.print(i);
		 }
	 }
}
