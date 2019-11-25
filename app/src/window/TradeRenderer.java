/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import main.Main;
import main.Trade;

public class TradeRenderer extends JPanel implements ListCellRenderer<Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GridBagConstraints gbc;
	//private JLabel platformLabel;
	private JLabel siteLabel;
	private JLabel userLabel;
	private JLabel haveLabel;
	private JLabel wantLabel;
	private JLabel notesLabel;
	
	
	public TradeRenderer(){
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);
		gbl.columnWeights = new double[]{0,1};
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,10,0,10);
		gbc.anchor = GridBagConstraints.WEST;
		//platformLabel = new JLabel();
		siteLabel = new JLabel();
		userLabel = new JLabel();
		userLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		haveLabel = new JLabel();
		haveLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		wantLabel = new JLabel();
		wantLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		notesLabel = new JLabel();
		notesLabel.setFont(new Font("Tahoma", Font.ITALIC, 10));
	}

	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
		
		
		if(isSelected)
			this.setBackground(Color.LIGHT_GRAY);
		else
			this.setBackground(Color.WHITE);
		
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		
		Trade trade = (Trade) value;
		
		if(trade.getPlatform().equalsIgnoreCase("steam"))
			userLabel.setIcon(new ImageIcon(Main.class.getResource("/platforms/steamicon.png")));
		if(trade.getPlatform().toLowerCase().equalsIgnoreCase("xbox"))
			userLabel.setIcon(new ImageIcon(Main.class.getResource("/platforms/xboxicon.png")));
		if(trade.getPlatform().toLowerCase().equalsIgnoreCase("ps4"))
			userLabel.setIcon(new ImageIcon(Main.class.getResource("/platforms/ps4icon.png")));
		userLabel.setText(trade.getUsername());
		siteLabel.setIcon(new ImageIcon(Main.class.getResource("/sites/"+trade.getSite().toLowerCase()+".png")));
		
		haveLabel.setText("[H]  "+trade.getHave());
		wantLabel.setText("[W] "+trade.getWant());
		notesLabel.setText("Notes: "+trade.getNotes());
		
		gbc.gridheight = 4;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(siteLabel, gbc);
		
		/*
		gbc.gridx++;
		this.add(platformLabel, gbc);
		*/
		
		gbc.gridheight = 1;
		gbc.gridx++;
		this.add(userLabel, gbc);
		
		gbc.gridy++;
		this.add(haveLabel, gbc);
		
		gbc.gridy++;
		this.add(wantLabel, gbc);
		
		gbc.gridy++;
		this.add(notesLabel, gbc);
		
		setEnabled(true);
		
		return this;
	}
	
}
