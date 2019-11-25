/*******************************************************************************
 * Copyright (c) 2016 Antonio Santoro.
 *******************************************************************************/
package window;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import mail.Email;
import main.Item;
import main.Main;
import main.Trade;
import main.TradeFinder;

public class MainFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TradeFinder finder;
	
	ExecutorService workers;
	
	@SuppressWarnings("rawtypes")
	JList tradesList;
	TradesListModel lm;
	JButton startButton;
	
	private String haveName;
	private String havePaint;
	private String haveCert;
	private String wantName;
	private String wantPaint;
	private String wantCert;
	private String platform;
	private boolean update;
	private ArrayList<String> items;
	
	class Version{
		Boolean update;
		String version;
	}
	
	public Boolean checkUpdates(){
		return true;
	}
	
	private String readAll(Reader rd){
		try {
			StringBuilder sb = new StringBuilder();
			int cp;
			while((cp = rd.read()) != -1)
				sb.append((char) cp);
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public MainFrame(){
		super("GG Well Trade (Beta)");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/about/LOGO.png")));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		items = new ArrayList<String>();
		populate();
		GUI();
		workers = Executors.newFixedThreadPool(2);
		update = false;
		// crea finestra di dialogo
		if(this.checkUpdates()){
			
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void GUI(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(1280,920));
		this.setLocationRelativeTo(null);
		Container pane = this.getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		pane.setLayout(gbl);
		gbl.rowWeights = new double[]{0,0,1};
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		
		
		// MENU BAR
		JMenuBar menu = new JMenuBar();
		JMenu menuMenu = new JMenu("Menu");
		JMenu menuHelp = new JMenu("Help");
		JMenuItem menuItemAbout = new JMenuItem("About...");
		JMenuItem menuItemUpdate = new JMenuItem("Update items");
		JMenuItem menuItemTutorial = new JMenuItem("How to");
		menu.add(menuMenu);
		menu.add(menuHelp);
		menuMenu.add(menuItemUpdate);
		menuHelp.add(menuItemTutorial);
		menuHelp.addSeparator();
		menuHelp.add(menuItemAbout);
		
		this.setJMenuBar(menu);
		
		
		// HOW TO
		menuItemTutorial.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog tutorialDialog = new JDialog();
				JPanel mainPanel = new JPanel();
				mainPanel.setBorder(new EmptyBorder(10,10,10,10));
				tutorialDialog.add(mainPanel);
				tutorialDialog.setSize(510,270);
				GridBagLayout gbl = new GridBagLayout();
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.insets = new Insets(10,10,10,10);
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1;
				gbc.weighty = 1;
				mainPanel.setLayout(gbl);
				tutorialDialog.setModal(true);
				tutorialDialog.setTitle("How to...");
				tutorialDialog.setResizable(false);
				tutorialDialog.setLocationRelativeTo(pane);
				tutorialDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				JTextPane tutorialText = new JTextPane();
				tutorialText.setEditable(false);
				tutorialText.setBackground(tutorialDialog.getBackground());
				tutorialText.setFont(new Font("Tahoma", Font.PLAIN, 12));
				tutorialText.setText("To perform a new search please follow these easy steps: "
						+ "\n\n1. Put in the [H]ave panel the item you WANT "
						+ "\n2. Put in the [W]ant panel the item you HAVE "
						+ "\n3. To run press the Start button"
						+ "\n4. Press the Stop button to cancel the current search"
						+ "\n5. Double-click on a trade to open it"
						+ "\n\nIf new items are added to the game please use the \"Update items\" function under the Menu and expect a new update."
						+ "\nRemember that you don't need to Start Stop the current search to find new trades as new ones are added automatically."
						+ "\nFor newer versions please refer to the main repository page or to my profiles."
						+ "\nENJOY!");
				
				mainPanel.add(tutorialText, gbc);
				
				
				tutorialDialog.setVisible(true);
			}
			
		});
		
		
		
		// INFO DIALOG
		menuItemAbout.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog aboutDialog = new JDialog();
				JPanel mainPanel = new JPanel();
				mainPanel.setBackground(new Color(235,235,235));
				aboutDialog.add(mainPanel);
				aboutDialog.setModal(true);
				aboutDialog.setTitle("About GG Well Trade");
				aboutDialog.setSize(310, 300);
				aboutDialog.setResizable(false);
				aboutDialog.setLocationRelativeTo(pane);
				aboutDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				GridBagLayout gbl = new GridBagLayout();
				mainPanel.setLayout(gbl);
				mainPanel.setBorder(new EmptyBorder(10,10,10,10));
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.anchor = GridBagConstraints.WEST;
				gbc.weighty = 1;
				gbc.insets = new Insets(0,2,0,0);
				
				JLayeredPane infoPanel = new JLayeredPane();
				infoPanel.setLayout(new GridBagLayout());
				infoPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
				
				gbc.weightx = 1;
				gbc.gridwidth = 4;
				gbc.gridx = 0;
				gbc.gridy = 0;
				JLabel titleLabel = new JLabel();
				titleLabel.setText("<html><a href=\"https://github.com/anphetamina/GG-well-trade\">GG Well Trade repository page</a></html>");
				infoPanel.add(titleLabel, gbc);
				titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				titleLabel.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						try {
							Desktop.getDesktop().browse(new URI("https://github.com/anphetamina/GG-well-trade"));
						} catch (IOException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				
				gbc.gridy++;
				JLabel authorLabel = new JLabel();
				authorLabel.setText("Author: Antonio Santoro");
				infoPanel.add(authorLabel, gbc);
				
				gbc.gridy++;
				JLabel versionLabel = new JLabel();
				versionLabel.setText("Version Beta");
				infoPanel.add(versionLabel, gbc);
				
				gbc.weightx = 0;
				gbc.gridwidth = 1;
				gbc.gridy++;
				JLabel profilesLabel = new JLabel();
				profilesLabel.setText("Profiles: ");
				infoPanel.add(profilesLabel, gbc);
				
				gbc.gridx++;
				JLabel steamLabel = new JLabel();
				steamLabel.setIcon(new ImageIcon(Main.class.getResource("/about/steamIcon15.png")));
				infoPanel.add(steamLabel, gbc);
				steamLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				steamLabel.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						try {
							Desktop.getDesktop().browse(new URI("http://steamcommunity.com/id/anphetamina/"));
						} catch (IOException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				
				gbc.gridx++;
				JLabel fbLabel = new JLabel();
				fbLabel.setIcon(new ImageIcon(Main.class.getResource("/about/fbIcon15.png")));
				infoPanel.add(fbLabel, gbc);
				fbLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				fbLabel.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						try {
							Desktop.getDesktop().browse(new URI("https://www.facebook.com/tonyromero93"));
						} catch (IOException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				
				gbc.gridx++;
				JLabel emailLabel = new JLabel();
				emailLabel.setIcon(new ImageIcon(Main.class.getResource("/about/mailIcon15.png")));
				emailLabel.setToolTipText("a.santoro1993@gmail.com");
				infoPanel.add(emailLabel, gbc);
				/*
				gbc.weightx = 1;
				gbc.gridwidth = 4;
				gbc.gridx = 0;
				gbc.gridy++;
				JLabel licenseLabel = new JLabel();
				licenseLabel.setText("<html>License</html>");
				infoPanel.add(licenseLabel, gbc);
				*/
				gbc.anchor = GridBagConstraints.CENTER;
				JLayeredPane donationPanel = new JLayeredPane();
				donationPanel.setLayout(new GridBagLayout());
				donationPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				
				gbc.insets = new Insets(3,0,4,0);
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.fill = GridBagConstraints.NONE;
				JLabel workLabel = new JLabel();
				workLabel.setText("If you like my work, please donate");
				donationPanel.add(workLabel, gbc);
				
				gbc.gridy++;
				JLabel donationLabel = new JLabel();
				donationLabel.setIcon(new ImageIcon(Main.class.getResource("/about/donateButton.gif")));
				donationLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				donationLabel.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						try {
							Desktop.getDesktop().browse(new URI("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=QH56EW28TJGEY"));
						} catch (IOException | URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				donationPanel.add(donationLabel, gbc);
				
				
				GridBagConstraints ac = new GridBagConstraints();
				ac.insets = new Insets(2,2,2,2);
				ac.anchor = GridBagConstraints.CENTER;
				ac.weightx = 1;
				ac.weighty = 1;
				ac.gridx = 0;
				ac.gridy = 0;
				JLabel logoLabel = new JLabel();
				logoLabel.setIcon(new ImageIcon(Main.class.getResource("/about/LOGO.png")));
				mainPanel.add(logoLabel, ac);
				
				ac.fill = GridBagConstraints.BOTH;
				ac.gridy++;
				mainPanel.add(infoPanel, ac);
				
				ac.gridy++;
				mainPanel.add(donationPanel, ac);
				
				aboutDialog.setVisible(true);
			}
			
		});
		
		
		// IDs UPDATE
		menuItemUpdate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
			
		});
		
		
		
		
		// HAVE PANEL
		JPanel havePanel = new JPanel();
		GridBagLayout gbl_h = new GridBagLayout();
		havePanel.setLayout(gbl_h);
		havePanel.setBorder(BorderFactory.createTitledBorder("[H]ave"));
		//gbl_h.columnWeights = new double[]{0,1};
		
		JLabel itemHaveLabel = new JLabel("Item name");
		gbc.gridx = 0;
		gbc.gridy = 0;
		havePanel.add(itemHaveLabel, gbc);
		
		JComboBox<String> haveBoxItems = new JComboBox<String>();
		for(int i=0; i<items.size(); i++){
			haveBoxItems.addItem(items.get(i));
		}
		haveBoxItems.setEditable(true);
		haveBoxItems.setSelectedItem("Any");
		AutoCompleteDecorator.decorate(haveBoxItems);
		gbc.gridx++;
		havePanel.add(haveBoxItems, gbc);
		
		haveBoxItems.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				haveName = haveBoxItems.getSelectedItem().toString();
			}
			
		});
		
		JLabel colorHaveLabel = new JLabel("Color");
		gbc.gridx = 0;
		gbc.gridy++;
		havePanel.add(colorHaveLabel, gbc);
		
		JComboBox<String> haveBoxPaint = new JComboBox<String>();
		for(int i=0; i<colors.length; i++){
			haveBoxPaint.addItem(colors[i]);
		}
		haveBoxPaint.setEditable(true);
		haveBoxPaint.setSelectedItem("Any / None");
		AutoCompleteDecorator.decorate(haveBoxPaint);
		gbc.gridx++;
		havePanel.add(haveBoxPaint, gbc);
		
		haveBoxPaint.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				havePaint = haveBoxPaint.getSelectedItem().toString();
			}
			
		});
		
		JLabel certificationHaveLabel = new JLabel("Certification");
		gbc.gridx = 0;
		gbc.gridy++;
		havePanel.add(certificationHaveLabel, gbc);
		
		JComboBox<String> haveBoxCert = new JComboBox<String>();
		for(int i=0; i<certifications.length; i++){
			haveBoxCert.addItem(certifications[i]);
		}
		haveBoxCert.setEditable(true);
		haveBoxCert.setSelectedItem("Any / None");
		AutoCompleteDecorator.decorate(haveBoxCert);
		gbc.gridx++;
		havePanel.add(haveBoxCert, gbc);
		
		haveBoxCert.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				haveCert = haveBoxCert.getSelectedItem().toString();
			}
			
		});
		
		
		
		
		// WANT PANEL
		JPanel wantPanel = new JPanel();
		GridBagLayout gbl_w = new GridBagLayout();
		wantPanel.setLayout(gbl_w);
		wantPanel.setBorder(BorderFactory.createTitledBorder("[W]ant"));
		//gbl_w.columnWeights = new double[]{0,1};
		
		JLabel itemWantLabel = new JLabel("Item name");
		gbc.gridx = 0;
		gbc.gridy = 0;
		wantPanel.add(itemWantLabel, gbc);
		
		JComboBox<String> wantBoxItems = new JComboBox<String>();
		for(int i=0; i<items.size(); i++){
			wantBoxItems.addItem(items.get(i));
		}
		wantBoxItems.setEditable(true);
		wantBoxItems.setSelectedItem("Any");
		AutoCompleteDecorator.decorate(wantBoxItems);
		gbc.gridx++;
		wantPanel.add(wantBoxItems, gbc);
		
		wantBoxItems.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				wantName = wantBoxItems.getSelectedItem().toString();
			}
			
		});
		
		JLabel colorWantLabel = new JLabel("Color");
		gbc.gridx = 0;
		gbc.gridy++;
		wantPanel.add(colorWantLabel, gbc);
		
		JComboBox<String> wantBoxPaint = new JComboBox<String>();
		for(int i=0; i<colors.length; i++){
			wantBoxPaint.addItem(colors[i]);
		}
		wantBoxPaint.setEditable(true);
		wantBoxPaint.setSelectedItem("Any / None");
		AutoCompleteDecorator.decorate(wantBoxPaint);
		gbc.gridx++;
		wantPanel.add(wantBoxPaint, gbc);
		
		wantBoxPaint.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				wantPaint = wantBoxPaint.getSelectedItem().toString();
			}
			
		});
		
		JLabel certificationWantLabel = new JLabel("Certification");
		gbc.gridx = 0;
		gbc.gridy++;
		wantPanel.add(certificationWantLabel, gbc);
		
		JComboBox<String> wantBoxCert = new JComboBox<String>();
		for(int i=0; i<certifications.length; i++){
			wantBoxCert.addItem(certifications[i]);
		}
		wantBoxCert.setEditable(true);
		wantBoxCert.setSelectedItem("Any / None");
		AutoCompleteDecorator.decorate(wantBoxCert);
		gbc.gridx++;
		wantPanel.add(wantBoxCert, gbc);
		
		wantBoxCert.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				wantCert = wantBoxCert.getSelectedItem().toString();
			}
			
		});
		
		
		
		// BUTTONS PANEL
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Start button pressed.");
				start();
				startButton.setEnabled(false);
			}
			
		});
		buttonsPanel.add(startButton, gbc);
		
		gbc.gridx++;
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Stop button pressed.");
				stop();
				startButton.setEnabled(true);
			}
			
		});
		buttonsPanel.add(stopButton, gbc);
		
		gbc.gridx++;
		JComboBox<String> platformBox = new JComboBox<String>();
		for(int i=0; i<platforms.length; i++){
			platformBox.addItem(platforms[i]);
		}
		platformBox.setEditable(true);
		platformBox.setSelectedItem("Any");
		AutoCompleteDecorator.decorate(platformBox);
		buttonsPanel.add(platformBox, gbc);
		
		platformBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				platform = platformBox.getSelectedItem().toString();
			}
			
		});
		
		
		
		// LIST PANEL
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new GridBagLayout());
		listPanel.setBorder(BorderFactory.createTitledBorder("Trades List"));
		tradesList = new JList();
		tradesList.setFixedCellHeight(80);
		tradesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lm = new TradesListModel();
		tradesList.setCellRenderer(new TradeRenderer());
		tradesList.setModel(lm);
		tradesList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==2){
					try {
						Desktop.getDesktop().browse(new URI(((Trade) tradesList.getSelectedValue()).getUrl()));
					} catch (IOException | URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		/*
		tradesList.addMouseMotionListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				Point p = new Point(e.getPoint());
				int index = tradesList.locationToIndex(p);
				if(index != -1)
					tradesList.setSelectedIndex(index);
			}
		});
		*/
		JScrollPane scrollList = new JScrollPane(tradesList);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		listPanel.add(scrollList, gbc);
		
		GridBagConstraints mc = new GridBagConstraints();
		mc.insets = new Insets(5,5,5,5);
		mc.fill = GridBagConstraints.BOTH;
		
		mc.weightx = 1;
		mc.gridx = 0;
		mc.gridy = 0;
		pane.add(havePanel, mc);
		mc.gridx++;
		pane.add(wantPanel, mc);
		mc.gridx = 0;
		mc.gridwidth = 2;
		mc.gridy++;
		pane.add(buttonsPanel, mc);
		mc.gridy++;
		pane.add(listPanel, mc);
		
		this.setVisible(true);
	}
	
	public void start(){
		
		if(platform==null || platform=="Any")
			platform = "";
		if(haveName==null || haveName=="Any / None")
			haveName = "";
		if(havePaint==null || havePaint=="Any / None")
			havePaint = "";
		if(haveCert==null || haveCert=="Any / None")
			haveCert = "";
		if(wantName==null || wantName=="Any / None")
			wantName = "";
		if(wantPaint==null || wantPaint=="Any / None")
			wantPaint= "";
		if(wantCert==null || wantCert=="Any / None")
			wantCert= "";
		
		finder = new TradeFinder(new Item(wantName, wantCert, wantPaint), new Item(haveName, haveCert, havePaint), platform, lm, update);
		lm.clear();
		System.out.println("--------------------------");
		System.out.print("Searching: ");
		System.out.print((haveCert+" "+havePaint+" "+haveName).trim());
		System.out.println("");
		System.out.print("Offering: ");
		System.out.print((wantCert+" "+wantPaint+" "+wantName).trim());
		System.out.println("");
		System.out.println("Platform: "+platform);
		System.out.println("Update: "+update);
		System.out.println("checks = "+TradesListModel.checks);
		workers.execute(new Runnable(){

			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName()+" started.");
				finder.start();
			}
			
		});
	}
	
	public void stop(){
		workers.execute(new Runnable(){

			@Override
			public void run() {
				finder.stop();
				System.out.println(Thread.currentThread().getName()+" started.");
				TradesListModel.reset();
			}
			
		});
		
	}
	
	public void update(){
		stop();
		update = true;
		workers.execute(new Runnable(){

			@Override
			public void run() {
				startButton.setEnabled(false);
				items.clear();
				ArrayList<String> new_items = (new TradeFinder()).update();
				for(int i=0; i<new_items.size(); i++){
					if(new_items.get(i) != "Crate Offers" || new_items.get(i) != "Key Offers" || new_items.get(i) != "Any Offers" || new_items.get(i) != "Wheel Offers"){
						items.add(new_items.get(i));
					}
				}
				JOptionPane.showMessageDialog(null, "Items updated");
				System.out.println("Items updated.");
				startButton.setEnabled(true);
			}
			
		});
	}
	
	public void populate(){
		items.add("Any");
		items.add("Champion Series I Crate");
		items.add("Champion Series II Crate");
		items.add("Key");
		items.add("Champion Series III Crate");
		items.add("Labyrinth");
		items.add("Parallax");
		items.add("Slipstream");
		items.add("Heatwave");
		items.add("Gold nugget");
		items.add("Blitzen");
		items.add("Calavera");
		items.add("Candy Cane");
		items.add("Candy Corn");
		items.add("Carriage");
		items.add("Christmas Tree");
		items.add("Fuzzy Brute");
		items.add("Fuzzy Vampire");
		items.add("Gold Cap");
		items.add("Gold Rush");
		items.add("Goldstone");
		items.add("Holiday Gift");
		items.add("Pumpkin");
		items.add("Sad Sapling");
		items.add("Santa");
		items.add("White Hat");
		items.add("Xmas");
		items.add("Fuzzy Skull");
		items.add("Bone King");
		items.add("Ghost");
		items.add("Netherworld");
		items.add("Lightning Wheel");
		items.add("Lobo");
		items.add("Looper");
		items.add("Photon");
		items.add("Pulsus");
		items.add("Discotheque");
		items.add("X-Devil Mk2");
		items.add("Dominus GT");
		items.add("Pixel Fire");
		items.add("Road Hog XL");
		items.add("Takumi RX-T");
		items.add("Trinity");
		items.add("Breakout Type-S");
		items.add("Dark Matter");
		items.add("Hypernova");
		items.add("Frostbite");
		items.add("Hearts");
		items.add("Lightning");
		items.add("Toon Smoke");
		items.add("Ink");
		items.add("Treasure");
		items.add("Anubis (Takumi Decal)");
		items.add("Arcana (Dominus Decal)");
		items.add("Carbonated (Road Hog Decal)");
		items.add("Chakram");
		items.add("Combo (Takumi Decal)");
		items.add("Distortion (Octane Decal)");
		items.add("Dot Matrix (Breakout Decal)");
		items.add("Dragon Lord (Octane Decal)");
		items.add("Narwhal (Merc Decal)");
		items.add("Nine Lives (Venom Decal)");
		items.add("Pollo Caliente (Dominus Decal)");
		items.add("Polygonal");
		items.add("Shibuya (Breakout Decal)");
		items.add("Snakeskin (Dominus Decal)");
		items.add("Snake Skin (X-Devil Decal)");
		items.add("Vice (Breakout Decal)");
		items.add("Warlock (Merc Decal)");
		items.add("Whizzle (Takumi Decal)");
		items.add("Octane: MG-88");
		items.add("Breakout: Snakeskin");
		items.add("Takumi: Distortion");
		items.add("Troika");
		items.add("Royalty (Dominus Decal)");
		items.add("Drink Helmet");
		items.add("Flex (Venom Decal)");
		items.add("Junk Food (Breakout)");
		items.add("Racer (Octane Decal)");
		items.add("Robo-Visor");
		items.add("Wildcat Ears");
		items.add("Masamune: Oni");
		items.add("Octane: Shisa");
		items.add("Clamshell");
		items.add("Breakout: Falchion");
		items.add("Breakout: Turbo");
		items.add("Dominus: Mondo");
		items.add("Alien");
		items.add("Biker Cap");
		items.add("Antlers");
		items.add("Balloon Dog");
		items.add("Baseball Cap [B]");
		items.add("Baseball Cap [F]");
		items.add("Beret");
		items.add("Birthday Cake");
		items.add("Derby");
		items.add("Little Bow");
		items.add("Brodie Helmet");
		items.add("Candle");
		items.add("Captain's Hat");
		items.add("Chainsaw");
		items.add("Chef's Hat");
		items.add("Chick Magnet");
		items.add("Cockroach");
		items.add("Cattleman");
		items.add("Cupcake");
		items.add("Deadmau5");
		items.add("Deerstalker");
		items.add("Brown Derby");
		items.add("Disco Ball");
		items.add("Donut");
		items.add("Foam Finger");
		items.add("Fruit Hat");
		items.add("Genie Lamp");
		items.add("Ivy Cap");
		items.add("Graduation Cap");
		items.add("Homburg");
		items.add("Hula Girl");
		items.add("Light Bulb");
		items.add("Mouse Trap");
		items.add("Paper Boat");
		items.add("Parrot");
		items.add("Party Hat");
		items.add("Pork Pie");
		items.add("Pigeon");
		items.add("Pinata");
		items.add("Plunger");
		items.add("Police Hat");
		items.add("Rainbow Flag");
		items.add("Rhino Horns");
		items.add("Rocket");
		items.add("Rasta");
		items.add("Rose");
		items.add("Rubber Duckie");
		items.add("Shuriken");
		items.add("Sunflower");
		items.add("Tiara");
		items.add("Traffic Cone");
		items.add("Trucker Hat");
		items.add("Unicorn");
		items.add("Venus Flytrap");
		items.add("Visor");
		items.add("Waffle");
		items.add("Work Boot");
		items.add("Zeta");
		items.add("Asterias");
		items.add("Harpoon");
		items.add("Trident");
		items.add("Starfish");
		items.add("Bobby Helmet");
		items.add("Brunnehilde");
		items.add("Cherry Top");
		items.add("Devil Horns");
		items.add("Fez");
		items.add("Fire Helmet");
		items.add("Foam Hat");
		items.add("Halo");
		items.add("Hard Hat");
		items.add("Lowrider");
		items.add("Mariachi Hat");
		items.add("Pirate's Hat");
		items.add("Pizza Topper");
		items.add("Portal - Cake");
		items.add("Propellerhead");
		items.add("Rat Rod");
		items.add("Royal Crown");
		items.add("Sombrero");
		items.add("Sunburst");
		items.add("Taxi Topper");
		items.add("Top Hat");
		items.add("Trahere");
		items.add("Tunica");
		items.add("Witch's Hat");
		items.add("Wizard Hat");
		items.add("Alchemist");
		items.add("Almas");
		items.add("Dieci");
		items.add("Falco");
		items.add("Invader");
		items.add("Neptune");
		items.add("Octavian");
		items.add("OEM");
		items.add("Spyder");
		items.add("Stern");
		items.add("Veloce");
		items.add("Vortex");
	}
	
	@SuppressWarnings("unused")
	private String[] items2 = {
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
	};
	private String[] colors = {
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
			"Pink"
	};
	private String[] certifications = {
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
			"Victor"
	};
	
	private String[] platforms = {
		"Any",
		"Steam",
		"Playstation 4",
		"Xbox One"
	};

}
