/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package window;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import mail.Email;
import main.Trade;

public class TradesListModel extends AbstractListModel<Object>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Trade> trades;
	
	public static int checks;
	
	public TradesListModel(){
		trades = new ArrayList<Trade>();
		checks = 0;
	}
	
	public static void check(){
		if(checks<3)
			checks++;
	}
	
	public static synchronized boolean reset(){
		if(checks==3)
			if(Email.send())
				System.out.println("Email sent.");
		checks = 0;
		return true;
	}
	
	public void add(Trade trade){
		if(checks<3){
			trades.add(trade);
			System.out.println("Trade_"+trade.getUsername()+" printed down.");
		}
		else
			trades.add(0, trade);
		this.fireIntervalAdded(this, 0, 1);
	}
	
	public void remove(){
		trades.remove(trades.size()-1);
		this.fireIntervalRemoved(this, trades.size(), trades.size());
	}
	
	public void clear(){
		trades.clear();
		this.fireIntervalRemoved(this, 0, trades.size());
	}

	@Override
	public int getSize() {
		return trades.size();
	}

	@Override
	public Object getElementAt(int index) {
		return trades.get(index);
	}

}
