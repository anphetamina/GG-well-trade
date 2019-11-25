package main;

import java.text.SimpleDateFormat;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */
public class Scheduler implements Runnable {

    SimpleDateFormat sdf;
    TradeFinder manager;

    public Scheduler(TradeFinder manager){
        this.manager=manager;
        sdf = new SimpleDateFormat("HH:mm:ss");
        //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
        if(manager.updateTrades()){
            System.out.println("\n\n\n\n\n"+sdf.format(System.currentTimeMillis())+" CACHE AND TRADE LIST CLEARED.\n\n\n\n\n");
            System.out.println("--------------------------");
        }
    }
}
