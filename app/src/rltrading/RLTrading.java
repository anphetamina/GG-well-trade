/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package rltrading;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import mail.Email;
import main.Item;
import main.Trade;
import service.IFinder;

public class RLTrading implements IFinder{
	
	private WebDriver driver;
	private List<WebElement> containers;
	
	private ArrayList<Trade> trades;
	
	private Item have;
	private Item want;
	private String platform;
	
	private HashMap<String,Integer> items;
	private HashMap<String,Integer> certifications;
	private HashMap<String,Integer> colors;
	private HashMap<String,String> platforms; // steam psn xbox
	
	private int haveNameID;
	private int haveCertID;
	private int havePaintID;
	private int wantNameID;
	private int wantCertID;
	private int wantPaintID;
	private String platformID;
	
	private boolean first = true;
	
	public RLTrading(Item have, Item want, String platform){
		trades = new ArrayList<Trade>();
		driver = new HtmlUnitDriver();
		this.have=have;
		this.want=want;
		this.platform=platform;
		items = new HashMap<String,Integer>();
		certifications = new HashMap<String,Integer>();
		colors = new HashMap<String,Integer>();
		platforms = new HashMap<String,String>();
		populateItems();
		System.out.println("RL-Trading - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName());
		System.out.println("RL-Trading - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName());
		System.out.println("RL-Trading - [P] "+platform);
		Email.add("RL-Trading - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName()
		 +"\nRL-Trading - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName()
		 +"\nRL-Trading - [P] "+platform
		 +"\n\n");
		
		Set<String> keys = items.keySet();
		
		if(items.get(have.getName())!=null)
			haveNameID = items.get(have.getName());
		else{
			for(String key : keys){
				if(have.getName().contains(key))
					haveNameID = items.get(key);
			}
		}
		haveCertID = certifications.get(have.getCertification());
		havePaintID = colors.get(have.getPaint());
		if(items.get(want.getName())!=null)
			wantNameID = items.get(want.getName());
		else{
			for(String key : keys){
				if(want.getName().equalsIgnoreCase(key))
					wantNameID = items.get(key);
			}
		}
		wantCertID = certifications.get(want.getCertification());
		wantPaintID = colors.get(want.getPaint());
		platformID = platforms.get(this.platform);
	}

	@Override
	public ArrayList<Trade> getTrades() {
		boolean refresh = false;
		boolean found = false;
		do{
			try{
				if(have.getName()==""){
					driver.get("https://rocket-league.com/trading?filterItem="+wantNameID+"&filterCertification="+wantCertID+"&filterPaint="+wantPaintID+"&filterName=&filterPlatform="+platformID+"&filterSearchType=2");
					System.out.println("https://rocket-league.com/trading?filterItem="+wantNameID+"&filterCertification="+wantCertID+"&filterPaint="+wantPaintID+"&filterName=&filterPlatform="+platformID+"&filterSearchType=2");
					if(first)
						Email.add("RL-Trading - URL: https://rocket-league.com/trading?filterItem="+wantNameID+"&filterCertification="+wantCertID+"&filterPaint="+wantPaintID+"&filterName=&filterPlatform="+platformID+"&filterSearchType=2\n");
					driver.findElement(By.xpath("//*[@id='rlg-theiritems']/a[1]/div/img")).isDisplayed();
					
					containers = driver.findElements(By.cssSelector("div.rlg-trade-display-container"));
					for(WebElement trade : containers){
						String url = trade.findElement(By.cssSelector("div.rlg-trade-display-header a")).getAttribute("href");
						String platform = trade.findElement(By.cssSelector("span")).getAttribute("class").split("networks-")[1];
						String username = trade.findElement(By.cssSelector("div.rlg-trade-display-header a")).getText().split("wants")[0].replace(" ", "");
						String notes = "";
						if(trade.getAttribute("innerHTML").contains("rlg-trade-note-container"))
							notes = trade.findElement(By.cssSelector("div.rlg-trade-note-container p")).getText();
						Trade current_trade = new Trade(username, platform.replace("psn", "ps4"), url, notes, "RLTrading", System.currentTimeMillis());
						for(WebElement item : trade.findElements(By.cssSelector("div#rlg-youritems a"))){
							String certification = "";
							String paint = "";
							if(!item.getAttribute("href").contains("filterPaint=0"))
								paint = item.findElement(By.className("rlg-trade-display-item-paint")).getAttribute("data-name");
							if(!item.getAttribute("href").contains("filterCertification=0"))
								certification = item.findElement(By.cssSelector("span")).getText();
							Item current = new Item(item.findElement(By.cssSelector("h2")).getText(), certification, paint);
							current_trade.addHave(current);
							if(current.getName().contains(have.getName()) && current.getPaint().contains(have.getPaint()) && current.getCertification().contains(have.getCertification()))
								found = true;
						}
						for(WebElement item : trade.findElements(By.cssSelector("div#rlg-theiritems a"))){
							String certification = "";
							String paint = "";
							if(!item.getAttribute("href").contains("filterPaint=0"))
								paint = item.findElement(By.className("rlg-trade-display-item-paint")).getAttribute("data-name");
							if(!item.getAttribute("href").contains("filterCertification=0"))
								certification = item.findElement(By.cssSelector("span")).getText();
							Item current = new Item(item.findElement(By.cssSelector("h2")).getText(), certification, paint);
							current_trade.addWant(current);
						}
						if(found)
							trades.add(current_trade);
						found = false;
					}
					first = false;
				}
					
				else{
					driver.get("https://rocket-league.com/trading?filterItem="+haveNameID+"&filterCertification="+haveCertID+"&filterPaint="+havePaintID+"&filterName=&filterPlatform="+platformID+"&filterSearchType=1");
					System.out.println("https://rocket-league.com/trading?filterItem="+haveNameID+"&filterCertification="+haveCertID+"&filterPaint="+havePaintID+"&filterName=&filterPlatform="+platformID+"&filterSearchType=1");
					if(first)
						Email.add("RL-Trading - URL: https://rocket-league.com/trading?filterItem="+haveNameID+"&filterCertification="+haveCertID+"&filterPaint="+havePaintID+"&filterName=&filterPlatform="+platformID+"&filterSearchType=1\n");
					driver.findElement(By.xpath("//*[@id='rlg-theiritems']/a[1]/div/img")).isDisplayed();
					
					containers = driver.findElements(By.cssSelector("div.rlg-trade-display-container"));
					for(WebElement trade : containers){
						String url = trade.findElement(By.cssSelector("div.rlg-trade-display-header a")).getAttribute("href");
						String platform = trade.findElement(By.cssSelector("span")).getAttribute("class").split("networks-")[1];
						String username = trade.findElement(By.cssSelector("div.rlg-trade-display-header a")).getText().split("wants")[0].replace(" ", "");
						String notes = "";
						if(trade.getAttribute("innerHTML").contains("rlg-trade-note-container"))
							notes = trade.findElement(By.cssSelector("div.rlg-trade-note-container p")).getText();
						Trade current_trade = new Trade(username, platform, url, notes, "RLTrading", System.currentTimeMillis());
						for(WebElement item : trade.findElements(By.cssSelector("div#rlg-youritems a"))){
							String certification = "";
							String paint = "";
							if(!item.getAttribute("href").contains("filterPaint=0"))
								paint = item.findElement(By.className("rlg-trade-display-item-paint")).getAttribute("data-name");
							if(!item.getAttribute("href").contains("filterCertification=0"))
								certification = item.findElement(By.cssSelector("span")).getText();
							Item current = new Item(item.findElement(By.cssSelector("h2")).getText(), certification, paint);
							current_trade.addHave(current);
						}
						for(WebElement item : trade.findElements(By.cssSelector("div#rlg-theiritems a"))){
							String certification = "";
							String paint = "";
							if(!item.getAttribute("href").contains("filterPaint=0"))
								paint = item.findElement(By.className("rlg-trade-display-item-paint")).getAttribute("data-name");
							if(!item.getAttribute("href").contains("filterCertification=0"))
								certification = item.findElement(By.cssSelector("span")).getText();
							Item current = new Item(item.findElement(By.cssSelector("h2")).getText(), certification, paint);
							current_trade.addWant(current);
							if(current.getName().contains(want.getName()) && current.getPaint().contains(want.getPaint()) && current.getCertification().contains(want.getCertification()))
								found = true;
						}
						if(found)
							trades.add(current_trade);
						found = false;
					}
					first = false;
				}
				refresh = false;
			} catch(NoSuchElementException e){
				refresh = true;
				found = false;
				System.out.println("-----------------------------------  NO ELEMENT FOUND (RL-Trading)  -----------------------------------");
				e.printStackTrace();
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					refresh = false;
					found = false;
					e1.printStackTrace();
				} finally{
					//TradesListModel.reset();
				}
			}
		} while(refresh);
		
		return trades;
	}
	
	public boolean updateItems(){
		/*
		File ids = new File("RL-TRADING-ITEMS");
		f(downloadIds(ids))
		System.out.println("RL-Trading IDs downloaded.");
		
		BufferedReader file = new BufferedReader(new FileReader(ids));
		String currentLine;
		while((currentLine = file.readLine()) != null){
			items.put(currentLine.split(" ",2)[1], Integer.parseInt(currentLine.split(" ",2)[0]));
		}
		file.close();
		*/
		
		items.clear();
		colors.clear();
		certifications.clear();
		
		driver.get("https://rocket-league.com/items/antennas");
		List<WebElement> items_dl = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
		for(WebElement item : items_dl){
			items.put(item.findElement(By.cssSelector("h2")).getText(), Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id")));
		}
		items_dl.clear();
		driver.get("https://rocket-league.com/items/bodies");
		items_dl = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
		for(WebElement item : items_dl){
			items.put(item.findElement(By.cssSelector("h2")).getText(), Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id")));
		}
		items_dl.clear();
		driver.get("https://rocket-league.com/items/boosts");
		items_dl = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
		for(WebElement item : items_dl){
			items.put(item.findElement(By.cssSelector("h2")).getText(), Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id")));
		}
		items_dl.clear();
		driver.get("https://rocket-league.com/items/decals");
		items_dl = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
		for(WebElement item : items_dl){
			items.put(item.findElement(By.cssSelector("h2")).getText(), Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id")));
		}
		items_dl.clear();
		driver.get("https://rocket-league.com/items/toppers");
		items_dl = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
		for(WebElement item : items_dl){
			items.put(item.findElement(By.cssSelector("h2")).getText(), Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id")));
		}
		items_dl.clear();
		driver.get("https://rocket-league.com/items/wheels");
		items_dl = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
		for(WebElement item : items_dl){
			items.put(item.findElement(By.cssSelector("h2")).getText(), Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id")));
		}
		items.put("", 0);
		items.put("Lightning", 383);
		items.put("Lightning Wheel", 458);
		items.put("Champion Series I Crate", 494);
		items.put("Champion Series II Crate", 495);
		items.put("Champion Series III Crate", 502);
		items.put("Key", 496);
		
		colors.put("", 0);
		colors.put("Burnt Sienna", 1);
		colors.put("Lime", 2);
		colors.put("Titanium White", 3);
		colors.put("Cobalt", 4);
		colors.put("Crimson", 5);
		colors.put("Forest Green", 6);
		colors.put("Grey", 7);
		colors.put("Orange", 8);
		colors.put("Pink", 9);
		colors.put("Purple", 10);
		colors.put("Saffron", 11);
		colors.put("Sky Blue", 12);
		colors.put("Black", 13);
		
		certifications.put("", 0);
		certifications.put("Playmaker", 1);
		certifications.put("Acrobat", 2);
		certifications.put("Aviator", 3);
		certifications.put("Goalkeeper", 4);
		certifications.put("Guardian", 5);
		certifications.put("Juggler", 6);
		certifications.put("Paragon", 7);
		certifications.put("Scorer", 8);
		certifications.put("Show-Off", 9);
		certifications.put("Sniper", 10);
		certifications.put("Striker", 11);
		certifications.put("Sweeper", 12);
		certifications.put("Tactician", 13);
		certifications.put("Turtle", 14);
		certifications.put("Victor", 15);
		
		platforms.put("", "0");
		platforms.put("Steam", "pc");
		platforms.put("Playstation 4", "ps4");
		platforms.put("Xbox One", "xbox");
		
		/*
		for (HashMap.Entry<Integer, String> entry : items.entrySet()) {
		    Integer key = entry.getKey();
		    String value = entry.getValue();
		    System.out.println(key+" "+value);
		}
		*/
		
		/*
		if(ids.delete())
			System.out.println("ids deleted.");
		else
			System.out.println("ids not deleted.");
			*/
		if(items.size()>0)
			return true;
		return false;
	}
	
	public boolean downloadItems(){
		driver.get("https://rocket-league.com/trading/new");
		try{
			driver.get("https://rocket-league.com/items/antennas");
			List<WebElement> items = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
			BufferedWriter file = new BufferedWriter(new FileWriter("RL-TRADING-ITEMS-FORMATTED.txt",false));
			for(WebElement item : items){
				//file.write("\""+item.findElement(By.cssSelector("h2")).getText()+"\",");
				file.write("items.put(\""+item.findElement(By.cssSelector("h2")).getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id"))+");");
				file.newLine();
				file.flush();
			}
			items.clear();
			//file.newLine();
			
			
			driver.get("https://rocket-league.com/items/bodies");
			items = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
			for(WebElement item : items){
				//file.write("\""+item.findElement(By.cssSelector("h2")).getText()+"\",");
				file.write("items.put(\""+item.findElement(By.cssSelector("h2")).getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id"))+");");
				file.newLine();
				file.flush();
			}
			items.clear();
			//file.newLine();
			
			
			driver.get("https://rocket-league.com/items/boosts");
			items = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
			for(WebElement item : items){
				//file.write("\""+item.findElement(By.cssSelector("h2")).getText()+"\",");
				file.write("items.put(\""+item.findElement(By.cssSelector("h2")).getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id"))+");");
				file.newLine();
				file.flush();
			}
			items.clear();
			//file.newLine();
			
			
			driver.get("https://rocket-league.com/items/decals");
			items = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
			for(WebElement item : items){
				//file.write("\""+item.findElement(By.cssSelector("h2")).getText()+"\",");
				file.write("items.put(\""+item.findElement(By.cssSelector("h2")).getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id"))+");");
				file.newLine();
				file.flush();
			}
			items.clear();
			//file.newLine();
			
			
			driver.get("https://rocket-league.com/items/toppers");
			items = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
			for(WebElement item : items){
				//file.write("\""+item.findElement(By.cssSelector("h2")).getText()+"\",");
				file.write("items.put(\""+item.findElement(By.cssSelector("h2")).getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id"))+");");
				file.newLine();
				file.flush();
			}
			items.clear();
			//file.newLine();
			
			
			driver.get("https://rocket-league.com/items/wheels");
			items = driver.findElements(By.cssSelector("div.item-omg-wtf-bbq"));
			for(WebElement item : items){
				//file.write("\""+item.findElement(By.cssSelector("h2")).getText()+"\",");
				file.write("items.put(\""+item.findElement(By.cssSelector("h2")).getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.rlg-items-item")).getAttribute("data-id"))+");");
				file.newLine();
				file.flush();
			}
			file.close();
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void populateItems(){
		
		items.put("", 0);
		items.put("Champion Series I Crate", 494);
		items.put("Champion Series II Crate", 495);
		items.put("Champion Series III Crate", 502);
		items.put("Key", 496);
		items.put("8-Ball", 105);
		items.put("Blacklight: Retribution", 106);
		items.put("Blue Chequered Flag", 107);
		items.put("Camo Flag", 108);
		items.put("Chroma", 109);
		items.put("Dollar Sign", 110);
		items.put("Edge of Space", 111);
		items.put("Excalibur", 112);
		items.put("Fenix Rage", 113);
		items.put("Gingerbread Man", 114);
		items.put("Gold Nugget (Beta Reward)", 115);
		items.put("Heart", 116);
		items.put("Horseshoe", 117);
		items.put("Jolly Roger", 118);
		items.put("Lightning Bolt", 119);
		items.put("Loki", 120);
		items.put("Peace", 121);
		items.put("Safety Flag", 122);
		items.put("Saturn", 123);
		items.put("Shadowgate", 124);
		items.put("Skull", 125);
		items.put("Smiley", 126);
		items.put("Snowman", 127);
		items.put("Soccer Ball", 128);
		items.put("Star", 129);
		items.put("Strike Vector EX", 130);
		items.put("Team Fat", 131);
		items.put("Tennis Ball", 132);
		items.put("Twitch", 133);
		items.put("UFO", 134);
		items.put("Unreal", 135);
		items.put("Warframe", 136);
		items.put("Country Flags", 137);
		items.put("Blacklight", 138);
		items.put("Sweet Tooth", 141);
		items.put("White Flag", 226);
		items.put("Disconnect", 227);
		items.put("Chivalry - Agatha Knights", 229);
		items.put("Chivalry - Mason Order", 230);
		items.put("Retro Ball - Urban", 245);
		items.put("Retro Ball - Utopia", 246);
		items.put("Calavera", 259);
		items.put("Dave's Bread", 260);
		items.put("Fuzzy Brute", 261);
		items.put("Fuzzy Vamp", 262);
		items.put("Reddit Snoo", 263);
		items.put("The Game Awards - Statue", 264);
		items.put("Nosgoth - Human", 265);
		items.put("Nosgoth - Vampire", 266);
		items.put("Nosgoth", 267);
		items.put("Oddworld - Necrum", 268);
		items.put("Unreal Tournament", 269);
		items.put("Bomb Pole", 272);
		items.put("Radioactive", 273);
		items.put("Retro Ball - Wasteland", 274);
		items.put("RL Garage", 294);
		items.put("Candy Cane", 296);
		items.put("Holiday Gift", 297);
		items.put("Portal - Companion Cube", 298);
		items.put("Portal - PotatOS", 299);
		items.put("Rainbow Flag", 320);
		items.put("Waffle", 338);
		items.put("Rubber Duckie", 339);
		items.put("Rocket", 340);
		items.put("Piñata", 341);
		items.put("Parrot", 342);
		items.put("Moai", 343);
		items.put("deadmau5", 344);
		items.put("Hula Girl", 345);
		items.put("Genie Lamp", 346);
		items.put("Foam Finger", 347);
		items.put("Venus Flytrap", 348);
		items.put("Sunflower", 349);
		items.put("Rose", 350);
		items.put("Donut", 351);
		items.put("Unreal Tournament - Flak Shell", 352);
		items.put("Balloon Dog", 353);
		items.put("Disco Ball", 354);
		items.put("Cupcake", 355);
		items.put("Chick Magnet", 356);
		items.put("Candle", 357);
		items.put("Alien", 358);
		items.put("Batman", 366);
		items.put("Superman", 367);
		items.put("Wonder Woman", 368);
		items.put("9GAG", 371);
		items.put("DIV", 464);
		items.put("Wakeup Ship", 465);
		items.put("Discord", 466);
		items.put("PAX", 467);
		items.put("Penny Arcade", 468);
		items.put("ScrewAttack!", 469);
		items.put("Harpoon", 517);
		items.put("Seastar", 518);
		items.put("Trident", 524);
		items.put("Fuzzy Skull", 541);
		//items.put("Octane", 1);
		items.put("Sweet Tooth", 2);
		items.put("Backfire", 3);
		items.put("Breakout", 4);
		items.put("Gizmo", 5);
		items.put("Hotshot", 6);
		items.put("Merc", 7);
		items.put("Paladin", 8);
		items.put("Road Hog", 9);
		items.put("Venom", 10);
		items.put("X-Devil", 11);
		items.put("Takumi", 144);
		items.put("Dominus", 145);
		items.put("Scarab", 231);
		items.put("Zippy", 232);
		items.put("DeLorean Time Machine", 255);
		items.put("Grog", 270);
		items.put("Ripper", 271);
		items.put("Batmobile", 363);
		items.put("Aftershock", 403);
		items.put("Marauder", 404);
		items.put("Masamune", 405);
		items.put("Esper", 406);
		items.put("Dominus GT", 430);
		items.put("Road Hog XL", 431);
		items.put("Takumi RX-T", 432);
		items.put("X-Devil MK2", 433);
		items.put("Breakout Type-S", 506);
		items.put("Proteus", 525);
		items.put("Triton", 526);
		items.put("Confetti", 51);
		items.put("Datastream", 52);
		items.put("Flamethrower", 53);
		items.put("Flamethrower Blue", 54);
		items.put("Flamethrower Green", 55);
		items.put("Flamethrower Purple", 56);
		items.put("Flamethrower Red", 57);
		items.put("Flowers", 58);
		items.put("Gold Rush (Alpha Reward)", 59);
		items.put("Grass", 60);
		items.put("Hydro", 61);
		items.put("Ion Blue", 62);
		items.put("Ion Green", 63);
		items.put("Ion Pink", 64);
		items.put("Ion Purple", 65);
		items.put("Ion Red", 66);
		items.put("Ion Yellow", 67);
		items.put("Money", 68);
		items.put("Plasma", 69);
		items.put("Rainbow", 70);
		items.put("Sacred", 71);
		items.put("Slime", 72);
		items.put("Snowflakes", 73);
		items.put("Sparkles", 74);
		items.put("Standard Blue", 75);
		items.put("Standard Pink", 76);
		items.put("Standard Purple", 77);
		items.put("Standard Red", 78);
		items.put("Standard Yellow", 79);
		items.put("Thermal", 80);
		items.put("Thermal Blue", 81);
		items.put("Thermal Green", 82);
		items.put("Thermal Pink", 83);
		items.put("Thermal Purple", 84);
		items.put("Thermal Yellow", 85);
		items.put("Bubbles", 142);
		items.put("Flamethrower Pink", 143);
		items.put("Nitrous", 146);
		items.put("Burnout", 147);
		items.put("Accelerato", 247);
		items.put("Battle-Stars", 248);
		items.put("Candy Corn", 257);
		items.put("Nuts & Bolts", 275);
		items.put("Sandstorm", 276);
		items.put("Portal - Conversion Gel", 300);
		items.put("Portal - Propulsion Gel", 301);
		items.put("Portal - Repulsion Gel", 302);
		items.put("Xmas", 303);
		items.put("Batmobile", 364);
		items.put("OutaTime", 370);
		items.put("Season 2 - Challenger", 376);
		items.put("Season 2 - Prospect", 377);
		items.put("Season 2 - Star", 378);
		items.put("Season 2 - Champion", 379);
		items.put("Toon Smoke", 380);
		items.put("Frostbite", 381);
		items.put("Hearts", 382);
		items.put("Lightning", 383);
		items.put("Polygonal", 435);
		items.put("Pixel Fire", 436);
		items.put("Trinity", 437);
		items.put("Dark Matter", 515);
		items.put("Hypernova", 516);
		items.put("Ink", 519);
		items.put("Treasure", 520);
		items.put("Netherworld", 543);
		items.put("Slipstream", 428);
		items.put("Parallax", 438);
		items.put("Labyrinth", 462);
		items.put("Heatwave", 463);
		items.put("Copycat", 407);
		items.put("Tribal", 408);
		items.put("MIRV", 409);
		items.put("Seismic", 410);
		items.put("Tiger", 411);
		items.put("Wings", 499);
		items.put("Tech", 48);
		items.put("Flames", 163);
		//items.put("Lightning", 171);
		items.put("Skulls", 179);
		items.put("Stars", 187);
		items.put("Stripes", 195);
		items.put("Wings", 216);
		items.put("Flames", 164);
		//items.put("Lightning", 172);
		items.put("Skulls", 180);
		items.put("Stars", 188);
		items.put("Stripes", 196);
		items.put("Tech", 203);
		items.put("Wings", 217);
		items.put("Junk Food", 373);
		items.put("Shibuya", 441);
		items.put("Dot Matrix", 442);
		items.put("Vice", 443);
		items.put("Snakeskin", 505);
		items.put("Falchion", 508);
		items.put("Turbo", 512);
		items.put("Flames", 150);
		items.put("Scorpions", 151);
		items.put("Skulls", 152);
		items.put("Stripes", 153);
		items.put("Tats", 154);
		items.put("Wings", 155);
		items.put("Royalty", 372);
		items.put("Arcana", 444);
		items.put("Pollo Caliente", 446);
		items.put("Snakeskin", 447);
		items.put("Mondo", 510);
		items.put("Techno", 429);
		items.put("Flames", 476);
		items.put("Protractor", 477);
		items.put("Stripes", 478);
		items.put("Sunburst", 479);
		items.put("Wings", 480);
		items.put("Shank", 422);
		items.put("Super F3", 423);
		items.put("Kaiju", 424);
		items.put("Mouse Cat", 425);
		items.put("Neo", 426);
		items.put("Pegasus", 427);
		items.put("Flames", 165);
		//items.put("Lightning", 173);
		items.put("Skulls", 181);
		items.put("Stars", 189);
		items.put("Stripes", 197);
		items.put("Tech", 204);
		items.put("Wings", 218);
		items.put("Bomber", 282);
		items.put("Cyclops", 283);
		items.put("Lepus", 284);
		items.put("Stripes", 285);
		items.put("Tagged", 286);
		items.put("Tribal", 287);
		items.put("Flames", 166);
		//items.put("Lightning", 174);
		items.put("Skulls", 182);
		items.put("Stars", 190);
		items.put("Stripes", 198);
		items.put("Tech", 205);
		items.put("Wings", 219);
		items.put("Vagabond", 412);
		items.put("Big Buck", 413);
		items.put("Ruffian", 414);
		items.put("Stripes", 415);
		items.put("Safari", 416);
		items.put("Wings", 498);
		items.put("Wildfire", 417);
		items.put("Otaku", 418);
		items.put("DJ Sushi", 419);
		items.put("Stripes", 420);
		items.put("Road Rage", 421);
		items.put("Stars", 497);
		items.put("Oni", 523);
		items.put("Dots", 41);
		items.put("Flames", 42);
		//items.put("Lightning", 43);
		items.put("Skulls", 44);
		items.put("Stars", 45);
		items.put("Stripes", 46);
		items.put("Wings", 49);
		items.put("Narwhal", 454);
		items.put("Warlock", 455);
		items.put("Flames", 167);
		//items.put("Lightning", 175);
		items.put("Skulls", 183);
		items.put("Stars", 191);
		items.put("Stripes", 199);
		items.put("Tech", 206);
		items.put("Wings", 220);
		items.put("Racer", 374);
		items.put("Dragon Lord", 451);
		items.put("Distortion", 500);
		items.put("MG-88", 509);
		items.put("Sisha", 511);
		items.put("Flames", 168);
		//items.put("Lightning", 176);
		items.put("Skulls", 184);
		items.put("Stars", 192);
		items.put("Stripes", 200);
		items.put("Tech", 207);
		items.put("Wings", 221);
		items.put("Crash Dive", 527);
		items.put("Cuttle Time", 528);
		items.put("Fathom", 529);
		items.put("Ladybug", 530);
		items.put("Stripes", 531);
		items.put("Tiger Shark", 532);
		items.put("Bomber", 288);
		items.put("Flames", 289);
		items.put("Ockie", 290);
		items.put("Shot Fox", 291);
		items.put("Spikes", 292);
		items.put("Tribal", 293);
		items.put("Flames", 169);
		//items.put("Lightning", 177);
		items.put("Skulls", 185);
		items.put("Stars", 193);
		items.put("Stripes", 201);
		items.put("Tech", 208);
		items.put("Wings", 222);
		items.put("Carbonated", 501);
		items.put("Playtime", 470);
		items.put("Arsenal", 471);
		items.put("Horus", 472);
		items.put("Muerto", 473);
		items.put("Space Trip", 474);
		items.put("Wildfire", 475);
		items.put("Derby Girl", 233);
		items.put("Bomani", 234);
		items.put("Flames", 235);
		items.put("Hearts", 236);
		items.put("Tiger", 237);
		items.put("Tribal", 238);
		items.put("Chaser", 157);
		items.put("Copycat", 158);
		items.put("Crazy8", 159);
		items.put("Gaki", 160);
		items.put("Reiko", 161);
		items.put("Stripes", 162);
		items.put("Combo", 439);
		items.put("Anubis", 440);
		items.put("Whizzle", 453);
		items.put("Distortion", 507);
		items.put("Blackwork", 481);
		items.put("Inked", 482);
		items.put("Party-Chan", 483);
		items.put("Rockat", 484);
		items.put("Skullface", 485);
		items.put("Stripes", 486);
		items.put("Daddy-O", 533);
		items.put("Fugu", 534);
		items.put("Makai", 535);
		items.put("Modus Bestia", 536);
		items.put("Ragnarok", 537);
		items.put("Stripes", 538);
		items.put("Tagged", 47);
		items.put("Dots", 210);
		items.put("Flames", 211);
		//items.put("Lightning", 212);
		items.put("Skulls", 213);
		items.put("Wings", 215);
		items.put("Stripes", 224);
		items.put("Flex", 375);
		items.put("Nine Lives", 452);
		items.put("Flames", 170);
		//items.put("Lightning", 178);
		items.put("Skulls", 186);
		items.put("Stars", 194);
		items.put("Stripes", 202);
		items.put("Tech", 209);
		items.put("Wings", 223);
		items.put("Snakeskin", 456);
		items.put("Debugged", 487);
		items.put("Hyper", 488);
		items.put("Stars", 489);
		items.put("Stripes", 490);
		items.put("Wings", 491);
		items.put("X", 492);
		items.put("Caboodle", 239);
		items.put("Callous", 240);
		items.put("Flames", 241);
		items.put("Hearts", 242);
		items.put("Leopard", 243);
		items.put("Tiger", 244);
		items.put("Bobby Helmet", 86);
		items.put("Brünnehilde", 87);
		items.put("Cherry Top", 88);
		items.put("Devil Horns", 89);
		items.put("Fez", 90);
		items.put("Fire Helmet", 91);
		items.put("Gold Cap (Alpha Reward)", 92);
		items.put("Halo", 93);
		items.put("Hard Hat", 94);
		items.put("Mariachi Hat", 95);
		items.put("Pirate's Hat", 96);
		items.put("Pizza Topper", 97);
		items.put("Propellerhead", 98);
		items.put("Royal Crown", 99);
		items.put("Sombrero", 100);
		items.put("Taxi Topper", 101);
		items.put("Top Hat", 102);
		items.put("Witch's Hat", 103);
		items.put("Wizard Hat", 104);
		items.put("Cavalier", 251);
		items.put("Locomotive", 252);
		items.put("Pixelated Shades", 253);
		items.put("Shark Fin", 254);
		items.put("Pumpkin", 258);
		items.put("Boombox", 277);
		items.put("Cow Skull", 278);
		items.put("Mohawk", 279);
		items.put("Portal - Cake", 295);
		items.put("Blitzen", 304);
		items.put("Christmas Tree", 305);
		items.put("Sad Sapling", 306);
		items.put("Santa", 307);
		items.put("Unicorn", 310);
		items.put("Traffic Cone", 311);
		items.put("Tiara", 312);
		items.put("Shuriken", 313);
		items.put("Rhino Horns", 314);
		items.put("Mouse Trap", 315);
		items.put("Rasta", 316);
		items.put("Police Hat", 317);
		items.put("Plunger", 318);
		items.put("Paper Boat", 319);
		items.put("Graduation Cap", 321);
		items.put("Bowler", 322);
		items.put("Fruit Hat", 323);
		items.put("Brodie Helmet", 324);
		items.put("Homburg", 325);
		items.put("Derby", 326);
		items.put("Deerstalker", 327);
		items.put("deadmau5", 328);
		items.put("Cockroach", 329);
		items.put("Chef's Hat", 330);
		items.put("Chainsaw", 331);
		items.put("Captain's Hat", 332);
		items.put("Work Boot", 333);
		items.put("Birthday Cake", 334);
		items.put("Biker Cap", 335);
		items.put("Beret", 336);
		items.put("Antlers", 337);
		items.put("Season 1 - Bronze", 359);
		items.put("Season 1 - Silver", 360);
		items.put("Season 1 - Gold", 361);
		items.put("Season 1 - Platinum", 362);
		items.put("ROBO-Visor", 384);
		items.put("Wildcat Ears", 385);
		items.put("Drink Helmet", 386);
		items.put("Pigeon", 387);
		items.put("Little Bow", 388);
		items.put("Cattleman", 389);
		items.put("Pork Pie", 390);
		items.put("Ivy Cap", 391);
		items.put("Light Bulb", 392);
		items.put("Party Hat", 393);
		items.put("Trucker Hat", 394);
		items.put("Visor", 395);
		items.put("White Hat", 396);
		items.put("Baseball Cap [F]", 397);
		items.put("Baseball Cap [B]", 398);
		items.put("Day Of The Tentacle", 461);
		items.put("Foam Hat", 493);
		items.put("Clamshell", 504);
		items.put("Bone King", 540);
		items.put("Ghost", 542);
		items.put("Alchemist", 12);
		items.put("Almas", 13);
		items.put("Bender", 14);
		items.put("Dieci", 15);
		items.put("Falco", 16);
		items.put("Foreman", 17);
		items.put("Invader", 18);
		items.put("Lowrider", 19);
		items.put("Lucci", 20);
		items.put("Mountaineer", 21);
		items.put("Neptune", 22);
		items.put("Octavian", 23);
		items.put("OEM", 24);
		items.put("Rat Rod", 25);
		items.put("Spyder", 26);
		items.put("Stallion", 27);
		items.put("Stern", 28);
		items.put("Sunburst", 29);
		items.put("Tempest", 30);
		items.put("Tomahawk", 31);
		items.put("Trahere", 32);
		items.put("Tunica", 33);
		items.put("Veloce", 34);
		items.put("Vortex", 35);
		items.put("Goldstone (Alpha Reward)", 50);
		items.put("Sweet Tooth", 140);
		items.put("Cristiano", 148);
		items.put("Spinner", 149);
		items.put("Servergate", 225);
		items.put("Zippy", 249);
		items.put("Scarab", 250);
		items.put("Carriage", 256);
		items.put("Grog", 280);
		items.put("Ripper", 281);
		items.put("Batmobile", 365);
		items.put("DeLorean Time Machine", 369);
		items.put("Aftershock", 399);
		items.put("Marauder", 400);
		items.put("Masamune", 401);
		items.put("Esper", 402);
		items.put("Chakram", 448);
		items.put("Photon", 449);
		items.put("Looper", 450);
		items.put("Lobo", 457);
		items.put("Lightning Wheel", 458);
		items.put("Triton", 459);
		items.put("Rhino 2", 460);
		items.put("Troika", 503);
		items.put("Discotheque", 513);
		items.put("Pulsus", 514);
		items.put("Asterias", 521);
		items.put("Zeta", 522);
		items.put("Proteus", 539);
		
		colors.put("", 0);
		colors.put("Burnt Sienna", 1);
		colors.put("Lime", 2);
		colors.put("Titanium White", 3);
		colors.put("Cobalt", 4);
		colors.put("Crimson", 5);
		colors.put("Forest Green", 6);
		colors.put("Grey", 7);
		colors.put("Orange", 8);
		colors.put("Pink", 9);
		colors.put("Purple", 10);
		colors.put("Saffron", 11);
		colors.put("Sky Blue", 12);
		colors.put("Black", 13);
		
		certifications.put("", 0);
		certifications.put("Playmaker", 1);
		certifications.put("Acrobat", 2);
		certifications.put("Aviator", 3);
		certifications.put("Goalkeeper", 4);
		certifications.put("Guardian", 5);
		certifications.put("Juggler", 6);
		certifications.put("Paragon", 7);
		certifications.put("Scorer", 8);
		certifications.put("Show-Off", 9);
		certifications.put("Sniper", 10);
		certifications.put("Striker", 11);
		certifications.put("Sweeper", 12);
		certifications.put("Tactician", 13);
		certifications.put("Turtle", 14);
		certifications.put("Victor", 15);
		
		platforms.put("", "0");
		platforms.put("Steam", "pc");
		platforms.put("Playstation 4", "ps4");
		platforms.put("Xbox One", "xbox");
	}

}
