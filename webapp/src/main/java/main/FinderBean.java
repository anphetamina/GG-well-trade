package main;

import jdk.nashorn.internal.objects.annotations.Constructor;
import main.Item;
import main.Trade;
import main.TradeFinder;
import org.openqa.selenium.lift.find.Finder;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONEncoder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */

@ManagedBean(name = "finderBean", eager = true)
@SessionScoped
@PushEndpoint("/add")
public class FinderBean implements Serializable {

    private List<Trade> trades;
    private TradeFinder finder;
    //private Executor thread = Executors.newSingleThreadExecutor();

    private Item have;
    private Item want;
    private String platform;

    private boolean disabled;

    @PostConstruct
    public void init(){
        trades = new ArrayList<Trade>();
        finder = new TradeFinder(this);
        have = new Item("","","");
        want = new Item("","","");
        platform = "";
        disabled = false;
        System.out.println("finderBean created.");
    }

    public void start(){
        disabled = true;
        trades.clear();
        finder.start(have, want, platform);
        System.out.println("start invoked.");
    }

    public void stop(){
        finder.stop();
        disabled = false;
        System.out.println("stop invoked.");
    }

    public void add(Trade trade){
        trades.add(0, trade);
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish("/add", trade);
        System.out.println(trade.getUrl() + " added.");
    }

    @OnMessage(encoders = {JSONEncoder.class})
    public Trade onMessage(Trade trade){
        return trade;
    }

    public List<Trade> getTrades(){
        return trades;
    }

    public Item getHave() {
        return have;
    }

    public void setHave(Item have) {
        this.have = have;
    }

    public Item getWant() {
        return want;
    }

    public void setWant(Item want) {
        this.want = want;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean start) {
        this.disabled = start;
    }

    private List<String> items = Arrays.asList(
            "Any",
            "Champion Series I Crate",
            "Champion Series II Crate",
            "Key",
            "Champion Series III Crate",
            "Labyrinth",
            "Parallax",
            "Slipstream",
            "Heatwave",
            "Gold nugget",
            "Blitzen",
            "Calavera",
            "Candy Cane",
            "Candy Corn",
            "Carriage",
            "Christmas Tree",
            "Fuzzy Brute",
            "Fuzzy Vampire",
            "Gold Cap",
            "Gold Rush",
            "Goldstone",
            "Holiday Gift",
            "Pumpkin",
            "Sad Sapling",
            "Santa",
            "White Hat",
            "Xmas",
            "Fuzzy Skull",
            "Bone King",
            "Ghost",
            "Netherworld",
            "Lightning Wheel",
            "Lobo",
            "Looper",
            "Photon",
            "Pulsus",
            "Discotheque",
            "X-Devil Mk2",
            "Dominus GT",
            "Pixel Fire",
            "Road Hog XL",
            "Takumi RX-T",
            "Trinity",
            "Breakout Type-S",
            "Dark Matter",
            "Hypernova",
            "Frostbite",
            "Hearts",
            "Lightning",
            "Toon Smoke",
            "Ink",
            "Treasure",
            "Anubis (Takumi Decal)",
            "Arcana (Dominus Decal)",
            "Carbonated (Road Hog Decal)",
            "Chakram",
            "Combo (Takumi Decal)",
            "Distortion (Octane Decal)",
            "Dot Matrix (Breakout Decal)",
            "Dragon Lord (Octane Decal)",
            "Narwhal (Merc Decal)",
            "Nine Lives (Venom Decal)",
            "Pollo Caliente (Dominus Decal)",
            "Polygonal",
            "Shibuya (Breakout Decal)",
            "Snakeskin (Dominus Decal)",
            "Snake Skin (X-Devil Decal)",
            "Vice (Breakout Decal)",
            "Warlock (Merc Decal)",
            "Whizzle (Takumi Decal)",
            "Octane: MG-88",
            "Breakout: Snakeskin",
            "Takumi: Distortion",
            "Troika",
            "Royalty (Dominus Decal)",
            "Drink Helmet",
            "Flex (Venom Decal)",
            "Junk Food (Breakout)",
            "Racer (Octane Decal)",
            "Robo-Visor",
            "Wildcat Ears",
            "Masamune: Oni",
            "Octane: Shisa",
            "Clamshell",
            "Breakout: Falchion",
            "Breakout: Turbo",
            "Dominus: Mondo",
            "Alien",
            "Biker Cap",
            "Antlers",
            "Balloon Dog",
            "Baseball Cap [B]",
            "Baseball Cap [F]",
            "Beret",
            "Birthday Cake",
            "Derby",
            "Little Bow",
            "Brodie Helmet",
            "Candle",
            "Captain's Hat",
            "Chainsaw",
            "Chef's Hat",
            "Chick Magnet",
            "Cockroach",
            "Cattleman",
            "Cupcake",
            "Deadmau5",
            "Deerstalker",
            "Brown Derby",
            "Disco Ball",
            "Donut",
            "Foam Finger",
            "Fruit Hat",
            "Genie Lamp",
            "Ivy Cap",
            "Graduation Cap",
            "Homburg",
            "Hula Girl",
            "Light Bulb",
            "Mouse Trap",
            "Paper Boat",
            "Parrot",
            "Party Hat",
            "Pork Pie",
            "Pigeon",
            "Pinata",
            "Plunger",
            "Police Hat",
            "Rainbow Flag",
            "Rhino Horns",
            "Rocket",
            "Rasta",
            "Rose",
            "Rubber Duckie",
            "Shuriken",
            "Sunflower",
            "Tiara",
            "Traffic Cone",
            "Trucker Hat",
            "Unicorn",
            "Venus Flytrap",
            "Visor",
            "Waffle",
            "Work Boot",
            "Zeta",
            "Asterias",
            "Harpoon",
            "Trident",
            "Starfish",
            "Bobby Helmet",
            "Brunnehilde",
            "Cherry Top",
            "Devil Horns",
            "Fez",
            "Fire Helmet",
            "Foam Hat",
            "Halo",
            "Hard Hat",
            "Lowrider",
            "Mariachi Hat",
            "Pirate's Hat",
            "Pizza Topper",
            "Portal - Cake",
            "Propellerhead",
            "Rat Rod",
            "Royal Crown",
            "Sombrero",
            "Sunburst",
            "Taxi Topper",
            "Top Hat",
            "Trahere",
            "Tunica",
            "Witch's Hat",
            "Wizard Hat",
            "Alchemist",
            "Almas",
            "Dieci",
            "Falco",
            "Invader",
            "Neptune",
            "Octavian",
            "OEM",
            "Spyder",
            "Stern",
            "Veloce",
            "Vortex"
    );

    public List<String> getItems(){
        return items;
    }

    private List<String> colors = Arrays.asList(
            "Any / None",
            "Black",
            "Burnt Sienna",
            "Cobalt",
            "Crimson",
            "Forest Green",
            "Grey",
            "Lime",
            "Orange",
            "Purple",
            "Saffron",
            "Sky Blue",
            "Titanium White",
            "Pink");

    public List<String> getColors(){
        return colors;
    }

    private List<String> certifications = Arrays.asList(
            "Any / None",
            "Acrobat",
            "Aviator",
            "Goalkeeper",
            "Guardian",
            "Juggler",
            "Paragon",
            "Playmaker",
            "Scorer",
            "Show-Off",
            "Sniper",
            "Striker",
            "Sweeper",
            "Tactician",
            "Turtle",
            "Victor");

    public List<String> getCertifications(){
        return certifications;
    }
}
