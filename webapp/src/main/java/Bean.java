import main.Item;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.primefaces.push.annotation.OnMessage;
import org.primefaces.push.annotation.PushEndpoint;
import org.primefaces.push.impl.JSONEncoder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Antonio Santoro on 18/11/2016.
 */

@ManagedBean
@ViewScoped
@PushEndpoint("/notify")
public class Bean implements Serializable{

    private String text;

    private List<Item> items = new ArrayList<Item>();
    private ScheduledExecutorService timer;

    /*@PostConstruct
    public void init() {
        items = new ArrayList<Item>();
        timer = Executors.newSingleThreadScheduledExecutor();
    }*/

    public void start() {
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(()->add(),0,3,TimeUnit.SECONDS);
        System.out.println(text + " start invoked.");
    }

    public void stop(){
        timer.shutdownNow();
        items.clear();
        System.out.println("stop invoked.");
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish("/notify", null);
    }

    public void add() {
        Item item = new Item();
        items.add(item);
        System.out.println(Thread.currentThread().getName()+ ": label " + text + " added.");
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish("/notify", item);
    }

    @OnMessage(encoders = {JSONEncoder.class})
    public Item onMessage(Item item){
        return item;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    private List<String> names = Arrays.asList(
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

    public List<String> getNames(){
        return names;
    }
}
