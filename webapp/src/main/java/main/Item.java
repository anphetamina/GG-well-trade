package main;

import javax.faces.bean.ManagedBean;

/**
 * Created by Antonio Santoro on 20/11/2016.
 */

public class Item {

    private String name;
    private String certification;
    private String paint;

    public Item(){}

    public Item(String name, String certification, String paint){
        this.name=name;
        this.certification=certification;
        this.paint=paint;
    }

    public String getName(){
        return name;
    }

    public String getCertification(){
        return certification;
    }

    public String getPaint(){
        return paint;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public void setPaint(String paint) {
        this.paint = paint;
    }
}
