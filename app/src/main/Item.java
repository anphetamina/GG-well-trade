/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package main;

public class Item {
	
	private String name;
	private String certification;
	private String paint;
	
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
}
