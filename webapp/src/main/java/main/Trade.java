package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */
public class Trade {
    private String username;
    private String platform;
    private String url;
    private ArrayList<Item> have;
    private ArrayList<Item> want;
    private String notes;
    private String site;
    private long time;

    public Trade(String username, String platform, String url, String notes, String site, long time){
        this.username=username;
        this.platform=platform;
        this.url=url;
        this.notes=notes;
        this.site=site;
        this.time=time;
        have = new ArrayList<Item>();
        want = new ArrayList<Item>();
    }

    public void addHave(Item i){
        have.add(i);
    }

    public void addWant(Item i){
        want.add(i);
    }

    public String getUsername(){
        return username;
    }

    public String getPlatform(){
        return platform;
    }

    public String getUrl(){
        return url;
    }

    public String getNotes(){
        return notes;
    }

    public String getSite(){
        return site;
    }

    public String getHave(){
        String list = "";
        int i = 0;
        for(Item item : have){
            list += (item.getCertification()+" "+item.getPaint()+" "+item.getName()).trim().replace("  ", " ");
            if(i!=have.size()-1)
                list += ", ";
            i++;
        }

        return list.trim().replace("  ", " ");
    }

    public String getWant(){
        String list = "";
        int i = 0;
        for(Item item : want){
            list += (item.getCertification()+" "+item.getPaint()+" "+item.getName()).trim().replace("  ", " ");
            if(i!=want.size()-1)
                list += ", ";
            i++;
        }

        return list;
    }

    public long getTime(){
        return time;
    }

    public void print(int index){
        System.out.println("Username: "+username);
        System.out.println("Platform: "+platform);
        System.out.println("URL: "+url);
        System.out.println("Site: "+site);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Time: "+sdf.format(time));
        System.out.println("Index: "+index);
        System.out.print("[H] ");
        for(int k=0; k<have.size(); k++){
            Item current = have.get(k);
            System.out.print((current.getCertification()+" "+current.getPaint()+" "+current.getName()).trim().replace("  ", " "));
            if(k < have.size()-1)
                System.out.print(", ");
        }
        System.out.println("");
        System.out.print("[W] ");
        for(int l=0; l<want.size(); l++){
            Item current = want.get(l);
            System.out.print((current.getCertification()+" "+current.getPaint()+" "+current.getName()).trim().replace("  ", " "));
            if(l < want.size()-1)
                System.out.print(", ");
        }
        System.out.println("");
        System.out.println("Notes: "+notes);
        System.out.println("--------------------------");
    }

    public boolean equals(Object obj){
        if(this.getUrl().equalsIgnoreCase(((Trade) obj).getUrl()))
            return true;
        else
            return false;
    }
}
