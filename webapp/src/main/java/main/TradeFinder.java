package main;

import mail.Email;
import reddit.Reddit;
import rltracker.RLTracker;
import rltrading.RLTrading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */

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

    private FinderBean bean;

    public TradeFinder(FinderBean bean){
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        trades = new ArrayList<Trade>();
        cache = new ArrayList<String>(50);
        //semProd = new Semaphore(50);
        semCons = new Semaphore(0);
        sdf = new SimpleDateFormat("HH:mm:ss");
        i = 0;
        index = 0;
        this.bean = bean;
    }

    public TradeFinder(Item have, Item want, String platform){
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        trades = new ArrayList<Trade>();
        cache = new ArrayList<String>(50);
        //semProd = new Semaphore(50);
        semCons = new Semaphore(0);
        sdf = new SimpleDateFormat("HH:mm:ss");
        i = 0;
        index = 0;

    }

    public void start(Item have, Item want, String platform){

        Email.add("Main - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName()
                +"\nMain - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName()
                +"\nMain - [P] "+platform
                +"\n\n");

        cache.clear();

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

        finders = Executors.newFixedThreadPool(3);

        finders.execute(new TradeFinderThread(new RLTracker(have, want, platform), this));
        System.out.println("RL-Tracker finder started.");
        finders.execute(new TradeFinderThread(new RLTrading(have, want, platform), this));
        System.out.println("RL-Trading finder started.");
        finders.execute(new TradeFinderThread(new Reddit(have, want, platform), this));
        System.out.println("Reddit finder started.");

		/*
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Scheduler(this), TIME, TIME, TimeUnit.HOURS);
		System.out.println("Scheduler started.");
		*/

    }

    public void stop(){
        if(finders.shutdownNow() != null /*&& scheduler.shutdownNow() != null*/)
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
                bean.add(trade);
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
