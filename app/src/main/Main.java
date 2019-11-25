/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Main {

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		Item have = new Item("Wildcat Ears", "", "Pink"); // da cercare
		Item want = new Item("", "", ""); // da offrire
		String platform = "Steam";
		System.out.print("Searching: ");
		System.out.print((have.getCertification()+" "+have.getPaint()+" "+have.getName()).trim());
		System.out.println("");
		System.out.print("Offering: ");
		System.out.print((want.getCertification()+" "+want.getPaint()+" "+want.getName()).trim());
		System.out.println("");
		TradeFinder manager = new TradeFinder(have, want, platform, null, false);
		manager.start();
		System.out.println("Manager started.");
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Scheduler(manager), 3, 3, TimeUnit.HOURS);
		System.out.println("Scheduler started.");
		System.out.println("----------------------------------");
		
		//manager.stop();
	}
}
