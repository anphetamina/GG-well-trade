package main;

import service.IFinder;

import java.util.ArrayList;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */
public class TradeFinderThread implements Runnable {

    public volatile boolean running;

    IFinder finder;
    TradeFinder manager;

    private ArrayList<Trade> trades;

    public TradeFinderThread(IFinder f, TradeFinder m){
        finder=f;
        manager=m;
        trades = new ArrayList<Trade>();
        running = true;
        //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    }

    public void run(){
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
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
