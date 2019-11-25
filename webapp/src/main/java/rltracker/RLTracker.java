package rltracker;

import mail.Email;
import main.Item;
import main.Trade;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import service.IFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */
public class RLTracker implements IFinder{

    private WebDriver driver;
    private List<WebElement> containers;

    private ArrayList<Trade> trades;

    private HashMap<String,Integer> items;
    private HashMap<String,Integer> certifications;
    private HashMap<String,Integer> colors;

    private Item have;
    private Item want;
    private String platform;

    private boolean first = true;

    public RLTracker(){

    }

    public RLTracker(Item have, Item want, String platform){
        trades = new ArrayList<Trade>();
        driver = new HtmlUnitDriver();
        this.have=have;
        this.want=want;
        this.platform=platform;
        items = new HashMap<String,Integer>();
        certifications = new HashMap<String,Integer>();
        colors = new HashMap<String,Integer>();
        populateItems();
        System.out.println("RL-Tracker - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName());
        System.out.println("RL-Tracker - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName());
        System.out.println("RL-Tracker - [P] "+platform);
        Email.add("RL-Tracker - [H] "+have.getCertification()+" "+have.getPaint()+" "+have.getName()
                +"\nRL-Tracker - [W] "+want.getCertification()+" "+want.getPaint()+" "+want.getName()
                +"\nRL-Tracker - [P] "+platform
                +"\n\n");
    }

    @Override
    public ArrayList<Trade> getTrades() {
        boolean refresh = false;
        boolean found = false;
        boolean found1 = false;
        boolean found2 = false;
        do{
            try{
                if(have.getName()=="" && want.getName()==""){
                    driver.get("http://rltracker.pro/trades");
                    System.out.println("http://rltracker.pro/trades");
                    if(first)
                        Email.add("RL-Tracker - URL: http://rltracker.pro/trades\n");
                    containers = driver.findElements(By.cssSelector("div.col-xs-12.whole_trade_wrapper"));
                    for(WebElement trade : containers){
                        String url = trade.findElement(By.cssSelector("div.col-lg-6.the_text a.pull-right")).getAttribute("href");
                        String username = trade.findElement(By.cssSelector("div.pull-left.user_summary a")).getText();
                        String notes = trade.findElement(By.cssSelector("div.col-lg-6.the_text")).getText().replace("Text Show Details", "").replace("\n", "");
                        Trade current_trade = new Trade(username, "Steam", url, notes, "RLTracker", System.currentTimeMillis());

                        WebElement left_container = trade.findElement(By.xpath(".//div[@class='left_trade pull-left']"));
                        List<WebElement> have_items = left_container.findElements(By.xpath(".//div[@class='item_col pull-left']"));
                        for(WebElement item : have_items){
                            String amount = item.findElement(By.cssSelector("span.inline_amount")).getText();
                            if(Integer.parseInt(amount)!=1)
                                amount=amount+"x ";
                            else
                                amount="";
                            Item current = new Item(amount+item.findElement(By.cssSelector("div.name")).getText(), item.findElement(By.cssSelector("div.cert")).getText(), item.findElement(By.cssSelector("div.color")).getText());
                            current_trade.addHave(current);
                            if(current.getName().contains(have.getName()) && current.getPaint().contains(have.getPaint()) && current.getCertification().contains(have.getCertification()))
                                found1 = true;
                        }

                        WebElement right_container = trade.findElement(By.xpath("(.//div[@class='right_trade pull-right'])"));
                        List<WebElement> want_items = right_container.findElements(By.xpath("(.//div[@class='item_col pull-left'])"));
                        for(WebElement item : want_items){
                            String amount = item.findElement(By.cssSelector("span.inline_amount")).getText();
                            if(Integer.parseInt(amount)!=1)
                                amount=amount+"x ";
                            else
                                amount="";
                            Item current = new Item(amount+item.findElement(By.cssSelector("div.name")).getText(), item.findElement(By.cssSelector("div.cert")).getText(), item.findElement(By.cssSelector("div.color")).getText());
                            current_trade.addWant(current);
                            if(current.getName().contains(want.getName()) && current.getPaint().contains(want.getPaint()) && current.getCertification().contains(want.getCertification()))
                                found2 = true;
                        }

                        if(found1 && found2 && (this.platform=="Steam" || this.platform.contains("")))
                            trades.add(current_trade);
                        found = false;
                        found1 = false;
                        found2 = false;
                    }
                    first = false;
                }

                else if(have.getName()==""){
                    String url_paint = "";
                    if(want.getPaint()!="")
                        url_paint = "?query[inv_color_id]="+colors.get(want.getPaint());
                    String url_cert = "";
                    if(want.getCertification()!="")
                        url_cert = "?query[inv_cert_id]="+certifications.get(want.getCertification());
                    driver.get("http://rltracker.pro/trades/search/request/"+items.get(want.getName())+url_paint+url_cert);
                    System.out.println("http://rltracker.pro/trades/search/request/"+items.get(want.getName())+url_paint+url_cert);
                    if(first)
                        Email.add("RL-Tracker - URL: http://rltracker.pro/trades/search/request/"+items.get(want.getName())+url_paint+url_cert+"\n");
                    containers = driver.findElements(By.cssSelector("div.col-xs-12.whole_trade_wrapper"));
                    for(WebElement trade : containers){
                        String url = trade.findElement(By.cssSelector("div.col-lg-6.the_text a.pull-right")).getAttribute("href");
                        String username = trade.findElement(By.cssSelector("div.pull-left.user_summary a")).getText();
                        String notes = trade.findElement(By.cssSelector("div.col-lg-6.the_text")).getText().replace("Text Show Details", "").replace("\n", "");
                        Trade current_trade = new Trade(username, "Steam", url, notes, "RLTracker", System.currentTimeMillis());

                        WebElement left_container = trade.findElement(By.xpath(".//div[@class='left_trade pull-left']"));
                        List<WebElement> have_items = left_container.findElements(By.xpath(".//div[@class='item_col pull-left']"));
                        for(WebElement item : have_items){
                            String amount = item.findElement(By.cssSelector("span.inline_amount")).getText();
                            if(Integer.parseInt(amount)!=1)
                                amount=amount+"x ";
                            else
                                amount="";
                            Item current = new Item(amount+item.findElement(By.cssSelector("div.name")).getText(), item.findElement(By.cssSelector("div.cert")).getText(), item.findElement(By.cssSelector("div.color")).getText());
                            current_trade.addHave(current);
                            if(current.getName().contains(have.getName()) && current.getPaint().contains(have.getPaint()) && current.getCertification().contains(have.getCertification()))
                                found = true;
                        }

                        WebElement right_container = trade.findElement(By.xpath("(.//div[@class='right_trade pull-right'])"));
                        List<WebElement> want_items = right_container.findElements(By.xpath("(.//div[@class='item_col pull-left'])"));
                        for(WebElement item : want_items){
                            String amount = item.findElement(By.cssSelector("span.inline_amount")).getText();
                            if(Integer.parseInt(amount)!=1)
                                amount=amount+"x ";
                            else
                                amount="";
                            Item current = new Item(amount+item.findElement(By.cssSelector("div.name")).getText(), item.findElement(By.cssSelector("div.cert")).getText(), item.findElement(By.cssSelector("div.color")).getText());
                            current_trade.addWant(current);
                        }

                        if(found && (this.platform=="Steam" || this.platform.contains("")))
                            trades.add(current_trade);
                        found = false;
                    }
                    first = false;
                }

                else{
                    String url_paint = "";
                    if(have.getPaint()!="")
                        url_paint = "?query[inv_color_id]="+colors.get(have.getPaint());
                    String url_cert = "";
                    if(have.getCertification()!="")
                        url_cert = "?query[inv_cert_id]="+certifications.get(have.getCertification());
                    driver.get("http://rltracker.pro/trades/search/offer/"+items.get(have.getName())+url_paint+url_cert);
                    System.out.println("http://rltracker.pro/trades/search/offer/"+items.get(have.getName())+url_paint+url_cert);
                    if(first)
                        Email.add("RL-Tracker - URL: http://rltracker.pro/trades/search/offer/"+items.get(have.getName())+url_paint+url_cert+"\n");
                    containers = driver.findElements(By.cssSelector("div.col-xs-12.whole_trade_wrapper"));
                    for(WebElement trade : containers){
                        String url = trade.findElement(By.cssSelector("div.col-lg-6.the_text a.pull-right")).getAttribute("href");
                        String username = trade.findElement(By.cssSelector("div.pull-left.user_summary a")).getText();
                        String notes = trade.findElement(By.cssSelector("div.col-lg-6.the_text")).getText().replace("Text Show Details", "").replace("\n", "");
                        Trade current_trade = new Trade(username, "Steam", url, notes, "RLTracker", System.currentTimeMillis());

                        WebElement left_container = trade.findElement(By.xpath(".//div[@class='left_trade pull-left']"));
                        List<WebElement> have_items = left_container.findElements(By.xpath(".//div[@class='item_col pull-left']"));
                        for(WebElement item : have_items){
                            String amount = item.findElement(By.cssSelector("span.inline_amount")).getText();
                            if(Integer.parseInt(amount)!=1)
                                amount=amount+"x ";
                            else
                                amount="";
                            Item current = new Item(amount+item.findElement(By.cssSelector("div.name")).getText(), item.findElement(By.cssSelector("div.cert")).getText(), item.findElement(By.cssSelector("div.color")).getText());
                            current_trade.addHave(current);
                        }

                        WebElement right_container = trade.findElement(By.xpath("(.//div[@class='right_trade pull-right'])"));
                        List<WebElement> want_items = right_container.findElements(By.xpath("(.//div[@class='item_col pull-left'])"));
                        for(WebElement item : want_items){
                            String amount = item.findElement(By.cssSelector("span.inline_amount")).getText();
                            if(Integer.parseInt(amount)!=1)
                                amount=amount+"x ";
                            else
                                amount="";
                            Item current = new Item(amount+item.findElement(By.cssSelector("div.name")).getText(), item.findElement(By.cssSelector("div.cert")).getText(), item.findElement(By.cssSelector("div.color")).getText());
                            current_trade.addWant(current);
                            if(current.getName().contains(want.getName()) && current.getPaint().contains(want.getPaint()) && current.getCertification().contains(want.getCertification()))
                                found = true;
                        }

                        if(found && (this.platform=="Steam" || this.platform.contains("")))
                            trades.add(current_trade);
                        found = false;
                    }
                    first = false;
                }
                refresh = false;
            } catch(NoSuchElementException e){
                refresh = true;
                found = false;
                found1 = false;
                found2 = false;
                System.out.println("-----------------------------------  NO ELEMENT FOUND (RL-Tracker)  -----------------------------------");
                e.printStackTrace();
                try {
                    Thread.sleep(4000);
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
        } while(refresh);

        return trades;
    }

    public boolean updateItems(){

        items.clear();
        colors.clear();
        certifications.clear();

        driver.get("http://rltracker.pro/trades/search");
        List<WebElement> items_dl = driver.findElements(By.cssSelector("div.col-sm-2.item_col"));
        for(WebElement item : items_dl){
            items.put(item.getText(), Integer.parseInt(item.findElement(By.cssSelector("div.item.no-select-bruh")).getAttribute("data-id")));
        }

        colors.put("Any", 14);
        colors.put("Black", 1);
        colors.put("Burnt Sienna", 2);
        colors.put("Cobalt", 3);
        colors.put("Crimson", 4);
        colors.put("Forest Green", 5);
        colors.put("Grey", 6);
        colors.put("Lime", 7);
        colors.put("Orange", 8);
        colors.put("Purple", 9);
        colors.put("Saffron", 10);
        colors.put("Sky Blue", 11);
        colors.put("Titanium White", 12);
        colors.put("Pink", 13);

        certifications.put("Any", 16);
        certifications.put("Acrobat", 1);
        certifications.put("Aviator", 2);
        certifications.put("Goalkeeper", 3);
        certifications.put("Guardian", 4);
        certifications.put("Juggler", 5);
        certifications.put("Paragon", 6);
        certifications.put("Playmaker", 7);
        certifications.put("Scorer", 8);
        certifications.put("Show-Off", 9);
        certifications.put("Sniper", 10);
        certifications.put("Striker", 11);
        certifications.put("Sweeper", 12);
        certifications.put("Tactician", 13);
        certifications.put("Turtle", 14);
        certifications.put("Victor", 15);

        if(items.size()>0)
            return true;
        return false;
    }

    public ArrayList<String> downloadItems(){
        ArrayList<String> new_items = new ArrayList<String>();
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://rltracker.pro/trades/search");
        List<WebElement> items = driver.findElements(By.cssSelector("div.col-sm-2.item_col"));
        for(WebElement item : items){
            new_items.add(item.getText());
        }
        return new_items;
		/*
		try {
			driver.get("http://rltracker.pro/trades/search");
			List<WebElement> items = driver.findElements(By.cssSelector("div.col-sm-2.item_col"));
			BufferedWriter file = new BufferedWriter(new FileWriter("RL-TRACKER-ITEMS-COMBOBOX.txt",false));
			for(WebElement item : items){
				file.write("\""+item.getText()+"\",");
				//file.write("items.put(\""+item.getText()+"\", "+Integer.parseInt(item.findElement(By.cssSelector("div.item.no-select-bruh")).getAttribute("data-id"))+");");
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
		*/
    }

    public void populateItems(){

        items.put("Crate Offers", 158);
        items.put("Key Offers", 159);
        items.put("Any Offers", 160);
        items.put("Wheel Offers", 161);
        items.put("Champion Series I Crate", 34);
        items.put("Champion Series II Crate", 35);
        items.put("Key", 157);
        items.put("Champion Series III Crate", 180);
        items.put("Labyrinth", 79);
        items.put("Parallax", 92);
        items.put("Slipstream", 129);
        items.put("Heatwave", 130);
        items.put("Gold nugget", 1);
        items.put("Blitzen", 18);
        items.put("Calavera", 24);
        items.put("Candy Cane", 26);
        items.put("Candy Corn", 27);
        items.put("Carriage", 30);
        items.put("Christmas Tree", 39);
        items.put("Fuzzy Brute", 63);
        items.put("Fuzzy Vampire", 64);
        items.put("Gold Cap", 66);
        items.put("Gold Rush", 67);
        items.put("Goldstone", 68);
        items.put("Holiday Gift", 74);
        items.put("Pumpkin", 109);
        items.put("Sad Sapling", 122);
        items.put("Santa", 123);
        items.put("White Hat", 149);
        items.put("Xmas", 156);
        items.put("Fuzzy Skull", 198);
        items.put("Bone King", 199);
        items.put("Ghost", 200);
        items.put("Netherworld", 201);
        items.put("Lightning Wheel", 82);
        items.put("Lobo", 84);
        items.put("Looper", 85);
        items.put("Photon", 96);
        items.put("Pulsus", 162);
        items.put("Discotheque", 163);
        items.put("X-Devil Mk2", 2);
        items.put("Dominus GT", 51);
        items.put("Pixel Fire", 100);
        items.put("Road Hog XL", 114);
        items.put("Takumi RX-T", 133);
        items.put("Trinity", 140);
        items.put("Breakout Type-S", 164);
        items.put("Dark Matter", 165);
        items.put("Hypernova", 166);
        items.put("Frostbite", 61);
        items.put("Hearts", 73);
        items.put("Lightning", 81);
        items.put("Toon Smoke", 136);
        items.put("Ink", 184);
        items.put("Treasure", 185);
        items.put("Anubis (Takumi Decal)", 10);
        items.put("Arcana (Dominus Decal)", 11);
        items.put("Carbonated (Road Hog Decal)", 29);
        items.put("Chakram", 33);
        items.put("Combo (Takumi Decal)", 41);
        items.put("Distortion (Octane Decal)", 50);
        items.put("Dot Matrix (Breakout Decal)", 53);
        items.put("Dragon Lord (Octane Decal)", 54);
        items.put("Narwhal (Merc Decal)", 89);
        items.put("Nine Lives (Venom Decal)", 90);
        items.put("Pollo Caliente (Dominus Decal)", 104);
        items.put("Polygonal", 105);
        items.put("Shibuya (Breakout Decal)", 124);
        items.put("Snakeskin (Dominus Decal)", 126);
        items.put("Snake Skin (X-Devil Decal)", 127);
        items.put("Vice (Breakout Decal)", 145);
        items.put("Warlock (Merc Decal)", 148);
        items.put("Whizzle (Takumi Decal)", 150);
        items.put("Octane: MG-88", 167);
        items.put("Breakout: Snakeskin", 168);
        items.put("Takumi: Distortion", 169);
        items.put("Troika", 172);
        items.put("Royalty (Dominus Decal)", 6);
        items.put("Drink Helmet", 55);
        items.put("Flex (Venom Decal)", 58);
        items.put("Junk Food (Breakout)", 78);
        items.put("Racer (Octane Decal)", 110);
        items.put("Robo-Visor", 115);
        items.put("Wildcat Ears", 151);
        items.put("Masamune: Oni", 170);
        items.put("Octane: Shisa", 171);
        items.put("Clamshell", 176);
        items.put("Breakout: Falchion", 177);
        items.put("Breakout: Turbo", 178);
        items.put("Dominus: Mondo", 179);
        items.put("Alien", 7);
        items.put("Biker Cap", 8);
        items.put("Antlers", 9);
        items.put("Balloon Dog", 12);
        items.put("Baseball Cap [B]", 13);
        items.put("Baseball Cap [F]", 14);
        items.put("Beret", 15);
        items.put("Birthday Cake", 17);
        items.put("Derby", 20);
        items.put("Little Bow", 21);
        items.put("Brodie Helmet", 22);
        items.put("Candle", 25);
        items.put("Captain's Hat", 28);
        items.put("Chainsaw", 32);
        items.put("Chef's Hat", 36);
        items.put("Chick Magnet", 38);
        items.put("Cockroach", 40);
        items.put("Cattleman", 42);
        items.put("Cupcake", 43);
        items.put("Deadmau5", 45);
        items.put("Deerstalker", 46);
        items.put("Brown Derby", 47);
        items.put("Disco Ball", 49);
        items.put("Donut", 52);
        items.put("Foam Finger", 59);
        items.put("Fruit Hat", 62);
        items.put("Genie Lamp", 65);
        items.put("Ivy Cap", 69);
        items.put("Graduation Cap", 70);
        items.put("Homburg", 75);
        items.put("Hula Girl", 76);
        items.put("Light Bulb", 80);
        items.put("Mouse Trap", 88);
        items.put("Paper Boat", 91);
        items.put("Parrot", 93);
        items.put("Party Hat", 94);
        items.put("Pork Pie", 95);
        items.put("Pigeon", 97);
        items.put("Pinata", 98);
        items.put("Plunger", 102);
        items.put("Police Hat", 103);
        items.put("Rainbow Flag", 111);
        items.put("Rhino Horns", 113);
        items.put("Rocket", 116);
        items.put("Rasta", 117);
        items.put("Rose", 118);
        items.put("Rubber Duckie", 121);
        items.put("Shuriken", 125);
        items.put("Sunflower", 132);
        items.put("Tiara", 135);
        items.put("Traffic Cone", 138);
        items.put("Trucker Hat", 141);
        items.put("Unicorn", 143);
        items.put("Venus Flytrap", 144);
        items.put("Visor", 146);
        items.put("Waffle", 147);
        items.put("Work Boot", 154);
        items.put("Zeta", 173);
        items.put("Asterias", 175);
        items.put("Harpoon", 181);
        items.put("Trident", 182);
        items.put("Starfish", 183);
        items.put("Bobby Helmet", 19);
        items.put("Brunnehilde", 23);
        items.put("Cherry Top", 37);
        items.put("Devil Horns", 48);
        items.put("Fez", 56);
        items.put("Fire Helmet", 57);
        items.put("Foam Hat", 60);
        items.put("Halo", 71);
        items.put("Hard Hat", 72);
        items.put("Lowrider", 86);
        items.put("Mariachi Hat", 87);
        items.put("Pirate's Hat", 99);
        items.put("Pizza Topper", 101);
        items.put("Portal - Cake", 107);
        items.put("Propellerhead", 108);
        items.put("Rat Rod", 112);
        items.put("Royal Crown", 119);
        items.put("Sombrero", 128);
        items.put("Sunburst", 131);
        items.put("Taxi Topper", 134);
        items.put("Top Hat", 137);
        items.put("Trahere", 139);
        items.put("Tunica", 142);
        items.put("Witch's Hat", 152);
        items.put("Wizard Hat", 153);
        items.put("Alchemist", 186);
        items.put("Almas", 187);
        items.put("Dieci", 188);
        items.put("Falco", 189);
        items.put("Invader", 190);
        items.put("Neptune", 191);
        items.put("Octavian", 192);
        items.put("OEM", 193);
        items.put("Spyder", 194);
        items.put("Stern", 195);
        items.put("Veloce", 196);
        items.put("Vortex", 197);

        colors.put("Any", 14);
        colors.put("Black", 1);
        colors.put("Burnt Sienna", 2);
        colors.put("Cobalt", 3);
        colors.put("Crimson", 4);
        colors.put("Forest Green", 5);
        colors.put("Grey", 6);
        colors.put("Lime", 7);
        colors.put("Orange", 8);
        colors.put("Purple", 9);
        colors.put("Saffron", 10);
        colors.put("Sky Blue", 11);
        colors.put("Titanium White", 12);
        colors.put("Pink", 13);

        certifications.put("Any", 16);
        certifications.put("Acrobat", 1);
        certifications.put("Aviator", 2);
        certifications.put("Goalkeeper", 3);
        certifications.put("Guardian", 4);
        certifications.put("Juggler", 5);
        certifications.put("Paragon", 6);
        certifications.put("Playmaker", 7);
        certifications.put("Scorer", 8);
        certifications.put("Show-Off", 9);
        certifications.put("Sniper", 10);
        certifications.put("Striker", 11);
        certifications.put("Sweeper", 12);
        certifications.put("Tactician", 13);
        certifications.put("Turtle", 14);
        certifications.put("Victor", 15);
    }
}
