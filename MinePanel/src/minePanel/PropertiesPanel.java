package minePanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Link;

/**
 * Properties panel.
 * @author Will Eccles
 *
 */
public class PropertiesPanel {
	/**
	 * @wbp.parser.entryPoint
	 */
	
	// This is a hash of properties to be used when setting values of text boxes, etc.
	// to add to it: propsHash.put("key", "value");
	// to get from it: propsHash.get("hey"); -> "value"
	Map<String, String> propsHash = new HashMap<String, String>();
	
	// used to read the properties file
	Properties properties = new Properties();
	
	public Shell shlProperties;
	private Text motdBox;
	
	public void open() {
		
		
		try {
			properties.load(new FileInputStream("server.properties"));
			for(String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String key : properties.stringPropertyNames()) {
			
			String value = properties.getProperty(key);
			
			// this is not needed, but I had it in the previous method and so rather than rework everything after it,
			//	I just kept it
			String[] p = {key, value};
			
			// this will determine which property it is looking at
			switch(key) {
			case "server-port":
				propsHash.put(p[0], p[1]);
				break;
			case "allow-nether":
				propsHash.put(p[0], p[1]);
				break;
			case "gamemode":
				propsHash.put(p[0], p[1]);
				break;
			case "difficulty":
				propsHash.put(p[0], p[1]);
				break;
			case "spawn-monsters":
				propsHash.put(p[0], p[1]);
				break;
			case "announce-player-achievements":
				propsHash.put(p[0], p[1]);
				break;
			case "pvp":
				propsHash.put(p[0], p[1]);
				break;
			case "enable-command-block":
				propsHash.put(p[0], p[1]);
				break;
			case "spawn-animals":
				propsHash.put(p[0], p[1]);
				break;
			case "white-list":
				propsHash.put(p[0], p[1]);
				break;
			case "motd":
				propsHash.put(p[0], p[1]);
				break;
			case "generate-structures":
				propsHash.put(p[0], p[1]);
			}
		}

		Display display = Display.getDefault();
		createContents();
		shlProperties.open();
		shlProperties.layout();
		while (!shlProperties.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shlProperties = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.APPLICATION_MODAL);
		shlProperties.setSize(471, 292);
		shlProperties.setText("Server Properties");
		
		Label lblMotd = new Label(shlProperties, SWT.NONE);
		lblMotd.setBounds(10, 10, 55, 15);
		lblMotd.setText("MOTD");
		
		motdBox = new Text(shlProperties, SWT.BORDER);
		motdBox.setBounds(10, 31, 440, 21);
		motdBox.setText(propsHash.get("motd"));
		
		Group grpGameSettings = new Group(shlProperties, SWT.NONE);
		grpGameSettings.setText("Game Settings");
		grpGameSettings.setBounds(10, 58, 270, 195);
		
		Button btnPvp = new Button(grpGameSettings, SWT.CHECK);
		btnPvp.setToolTipText("When turned on, players can hurt each other.");
		btnPvp.setBounds(10, 21, 93, 16);
		btnPvp.setText("PVP");
		setState(btnPvp, "pvp");
		
		Button btnMobsCanSpawn = new Button(grpGameSettings, SWT.CHECK);
		btnMobsCanSpawn.setToolTipText("If disabled, monsters won't spawn even if the difficulty isn't peaceful.");
		btnMobsCanSpawn.setBounds(10, 43, 236, 16);
		btnMobsCanSpawn.setText("Spawn monsters");
		setState(btnMobsCanSpawn, "spawn-monsters");
		
		
		Button btnGenStructures = new Button(grpGameSettings, SWT.CHECK);
		btnGenStructures.setBounds(10, 65, 257, 16);
		btnGenStructures.setText("Generate structures (villages, dungeons, etc.)");
		setState(btnGenStructures, "generate-structures");
		
		Button btnSpawnAnimals = new Button(grpGameSettings, SWT.CHECK);
		btnSpawnAnimals.setText("Spawn animals");
		btnSpawnAnimals.setBounds(10, 87, 100, 16);
		setState(btnSpawnAnimals, "spawn-animals");
		
		Button btnAnnounceAchievements = new Button(grpGameSettings, SWT.CHECK);
		btnAnnounceAchievements.setBounds(10, 109, 187, 16);
		btnAnnounceAchievements.setText("Announce player achievements");
		setState(btnAnnounceAchievements, "announce-player-achievements");
		
		Button btnWhitelist = new Button(grpGameSettings, SWT.CHECK);
		btnWhitelist.setToolTipText("If this is turned on, don't forget to add yourself to the list!");
		btnWhitelist.setBounds(10, 131, 93, 16);
		btnWhitelist.setText("Use whitelist");
		setState(btnWhitelist, "white-list");
		
		Link whitelistLink = new Link(grpGameSettings, SWT.NONE);
		whitelistLink.setBounds(109, 132, 76, 15);
		whitelistLink.setText("<a>Edit whitelist</a>");
		
		Button btnAllowNether = new Button(grpGameSettings, SWT.CHECK);
		btnAllowNether.setBounds(10, 153, 187, 16);
		btnAllowNether.setText("Allow nether (recommended)");
		setState(btnAllowNether, "allow-nether");
		
		Button btnCMDBlocks = new Button(grpGameSettings, SWT.CHECK);
		btnCMDBlocks.setBounds(10, 175, 157, 16);
		btnCMDBlocks.setText("Enable command blocks");
		setState(btnCMDBlocks, "enable-command-block");
		
		Button btnSave = new Button(shlProperties, SWT.NONE);
		btnSave.setBounds(342, 227, 113, 25);
		btnSave.setText("Save and close");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// Save the data before closing
				try {
					properties.load(new FileInputStream("server.properties"));
					
					// set motd
					properties.setProperty("motd", motdBox.getText());
					// set pvp
					properties.setProperty("pvp", btnPvp.getSelection()?"true":"false");
					// set mob spawning
					properties.setProperty("spawn-monsters", btnMobsCanSpawn.getSelection()?"true":"false");
					// set structure generation
					properties.setProperty("generate-structures", btnGenStructures.getSelection()?"true":"false");
					// set animal spawning
					properties.setProperty("spawn-animals", btnSpawnAnimals.getSelection()?"true":"false");
					// set achievement announcing
					properties.setProperty("announce-player-achievements", btnAnnounceAchievements.getSelection()?"true":"false");
					// set white list
					properties.setProperty("white-list", btnWhitelist.getSelection()?"true":"false");
					// set allow nether
					properties.setProperty("allow-nether", btnAllowNether.getSelection()?"true":"false");
					// set allow command blocks
					properties.setProperty("enable-command-block", btnCMDBlocks.getSelection()?"true":"false");
					
					// store properties
					properties.store(new FileOutputStream("server.properties"), "Minecraft server properties");
					
					shlProperties.close();
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		Button btnAdvanced = new Button(shlProperties, SWT.NONE);
		btnAdvanced.setBounds(342, 196, 113, 25);
		btnAdvanced.setText("Advanced...");
		btnAdvanced.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				AdvancedPropertiesPanel aPP = new AdvancedPropertiesPanel();
				aPP.open();
			}
		});
	}
	
	/**
	 * Set a check button based on the value of a true/false property.
	 * @param b Button to be checked/unchecked.
	 * @param pName Name of the property to be checked. Must be t/f or it will not work correctly.
	 */
	private void setState(Button b, String pName) {
		if (propsHash.get(pName).equals("true")) {
			b.setSelection(true);
			System.out.println("Property '" + pName + "' is true, enabling checkbox.");
		}
		else {
			b.setSelection(false);
			System.out.println("Property '" + pName + "' is false, disabling checkbox.");
		}
	}
}