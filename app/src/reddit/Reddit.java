/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import mail.Email;
import main.Item;
import main.Trade;
import service.IFinder;

public class Reddit implements IFinder{
	
	private WebDriver driver;
	private List<WebElement> posts;
	
	private ArrayList<Trade> trades;
	
	private Item have;
	private Item want;
	private String platform; // STEAM ps4 Xbox
	
	private boolean first = true;
	
	public Reddit(Item have, Item want, String platform){
		trades = new ArrayList<Trade>();
		driver = new HtmlUnitDriver();
		this.have=have;
		this.want=want;
		this.platform=platform;
		if(platform.equalsIgnoreCase("Playstation 4"))
			this.platform="ps4";
		if(platform.equalsIgnoreCase("Xbox One"))
			this.platform="Xbox";
		System.out.println("Reddit - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName());
		System.out.println("Reddit - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName());
		System.out.println("Reddit - [P] "+platform);
		Email.add("Reddit - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName()
		 +"\nReddit - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName()
		 +"\nReddit - [P] "+platform
		 +"\n\n");
	}
	
	public boolean updateItems(){
		return true;
	}

	@Override
	public ArrayList<Trade> getTrades() {
		boolean refresh = false;
		boolean found1 = false;
		boolean found2 = false;
		do{
			try{
				driver.get("https://www.reddit.com/r/RocketLeagueExchange/new/");
				//driver.get("https://www.reddit.com/r/RocketLeagueExchange/search?q=photon&restrict_sr=on&sort=new&t=all");
				posts = driver.findElements(By.cssSelector("div.thing"));
				
				for(WebElement post : posts){
					String have = "";
					String want = "";
					String url = post.findElement(By.cssSelector("p.title a.title")).getAttribute("href");
					String title = post.findElement(By.cssSelector("p.title a.title")).getText();
					String platform = post.findElement(By.xpath("(.//span[@class='linkflairlabel'])")).getAttribute("title");
					String username = post.getAttribute("data-author");
					String t = title.toLowerCase();
					Trade trade = new Trade(username, platform, url, title, "Reddit", System.currentTimeMillis());
					
					if(t.contains("[h]") || t.contains("[w]")){
						if(t.indexOf("[h]") < t.indexOf("[w]")){
							have = title.substring(t.indexOf("[h]")+3, t.indexOf("[w]")).trim();
							want = title.substring(t.indexOf("[w]")+3).trim();
							trade.addHave(new Item(have, "", ""));
							trade.addWant(new Item(want, "", ""));
						}
						
						else{
							have = title.substring(t.indexOf("[h]")+3).trim();
							want = title.substring(t.indexOf("[w]")+3, t.indexOf("[h]")).trim();
							trade.addHave(new Item(have, "", ""));
							trade.addWant(new Item(want, "", ""));
						}
					}
					
					else if(t.contains("(h)") || t.contains("(w)")){
						if(t.indexOf("(h)") < t.indexOf("(w)")){
							have = title.substring(t.indexOf("(h)")+3, t.indexOf("(w)")).trim();
							want = title.substring(t.indexOf("(w)")+3).trim();
							trade.addHave(new Item(have, "", ""));
							trade.addWant(new Item(want, "", ""));
						}
						
						else{
							have = title.substring(t.indexOf("(h)")+3).trim();
							want = title.substring(t.indexOf("(w)")+3, t.indexOf("(h)")).trim();
							trade.addHave(new Item(have, "", ""));
							trade.addWant(new Item(want, "", ""));
						}
					}
					
					//String searching = (this.have.getCertification()+" "+this.have.getPaint()+" "+this.have.getName()).toLowerCase().trim();
					List<String> searching = new ArrayList<String>(Arrays.asList(this.have.getName().toLowerCase().split(" ")));
					if(this.have.getName().equalsIgnoreCase("Champion Series I Crate")){
						searching.clear();
						searching.add("crate1");
						searching.add("crate");
						searching.add("cc1");
						searching.add("c1");
					}
					if(this.have.getName().equalsIgnoreCase("Champion Series II Crate")){
						searching.clear();
						searching.add("crate2");
						searching.add("crate");
						searching.add("cc2");
						searching.add("c2");
					}
					if(this.have.getName().equalsIgnoreCase("Champion Series III Crate")){
						searching.clear();
						searching.add("crate3");
						searching.add("crate");
						searching.add("cc3");
						searching.add("c3");
					}
					if(first)
						System.out.println("Searching: "+searching);
					List<String> wanting = new ArrayList<String>(Arrays.asList(this.want.getName().toLowerCase().split(" ")));
					if(this.want.getName().equalsIgnoreCase("Champion Series I Crate")){
						wanting.clear();
						wanting.add("crate1");
						wanting.add("crate");
						wanting.add("cc1");
						wanting.add("c1");
					}
					if(this.want.getName().equalsIgnoreCase("Champion Series II Crate")){
						wanting.clear();
						wanting.add("crate2");
						wanting.add("crate");
						wanting.add("cc2");
						wanting.add("c2");
					}
					if(this.want.getName().equalsIgnoreCase("Champion Series III Crate")){
						wanting.clear();
						wanting.add("crate3");
						wanting.add("crate");
						wanting.add("cc3");
						wanting.add("c3");
					}
					if(first)
						System.out.println("Wanting: "+wanting);
					
					for(String s : searching){
						if(have.contains(s))
							found1 = true;
					}
					for(String s : wanting){
						if(want.contains(s))
							found2 = true;
					}
					if(found1 && found2 && platform.toLowerCase().contains(this.platform))
						trades.add(trade);
					
					found1 = false;
					found2 = false;
					
					if(first){
						Email.add("Reddit - Searching: "+searching
								+"\nReddit - Wanting: "+wanting
								+"\n");
					}
					first = false;
				}
				
				refresh = false;
			} catch(NoSuchElementException e){
				refresh = true;
				found1 = false;
				found2 = false;
				System.out.println("-----------------------------------  NO ELEMENT FOUND (Reddit)  -----------------------------------");
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					refresh = false;
					found1 = false;
					found2 = false;
					e1.printStackTrace();
				} finally{
					//TradesListModel.reset();
				}
			}
			 catch(StringIndexOutOfBoundsException e){
				 refresh = true;
				 found1 = false;
				 found2 = false;
				 e.printStackTrace();
			 }
		} while(refresh);
		
		return trades;
	}

}
