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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Spinner;

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
	private Text worldNameBox;
	
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		shlProperties = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.APPLICATION_MODAL);
		shlProperties.setSize(471, 348);
		shlProperties.setText("Server Properties");
		
		try {
			File propertiesFile = new File("server.properties");
			if (propertiesFile.exists()) {
				properties.load(new FileInputStream(propertiesFile));
			} else {
				ErrorHandler.displayError("Server.properties was not found. Start the server to create a new one.");
				return;
			}
		} catch (Exception e) {
			ErrorHandler.displayError("Could not read the server.properties file! Is it corrupted?", e);
		}
		
		for (String key : properties.stringPropertyNames()) {
			
			String value = properties.getProperty(key);
			
			// this is not needed, but I had it in the previous method and so rather than rework everything after it,
			//	I just kept it
			String[] p = {key, value};
			
			propsHash.put(p[0], p[1]);
		}

		createContents();
		shlProperties.open();
		shlProperties.layout();
		while (!shlProperties.isDisposed()) {
			if (!Display.getDefault().readAndDispatch()) {
				Display.getDefault().sleep();
			}
		}
	}
	
	
	protected void createContents() {
		
		Label lblMotd = new Label(shlProperties, SWT.NONE);
		lblMotd.setBounds(10, 10, 55, 15);
		lblMotd.setText("MOTD");
		
		motdBox = new Text(shlProperties, SWT.BORDER);
		motdBox.setBounds(10, 31, 451, 21);
		motdBox.setText(propsHash.get("motd"));

		worldNameBox = new Text(shlProperties, SWT.BORDER);
		worldNameBox.setBounds(10, 78, 451, 19);
		worldNameBox.setText(propsHash.get("level-name"));
		
		Group grpGameSettings = new Group(shlProperties, SWT.NONE);
		grpGameSettings.setText("Game Settings");
		grpGameSettings.setBounds(10, 103, 315, 213);
		
		Button btnPvp = new Button(grpGameSettings, SWT.CHECK);
		btnPvp.setToolTipText("When turned on, players can hurt each other.");
		btnPvp.setBounds(10, 21, 263, 16);
		btnPvp.setText("PVP");
		setState(btnPvp, "pvp");
		
		Button btnMobsCanSpawn = new Button(grpGameSettings, SWT.CHECK);
		btnMobsCanSpawn.setToolTipText("If disabled, monsters won't spawn even if the difficulty isn't peaceful.");
		btnMobsCanSpawn.setBounds(10, 43, 263, 16);
		btnMobsCanSpawn.setText("Spawn monsters");
		setState(btnMobsCanSpawn, "spawn-monsters");
		
		
		Button btnGenStructures = new Button(grpGameSettings, SWT.CHECK);
		btnGenStructures.setBounds(10, 65, 285, 16);
		btnGenStructures.setText("Generate structures (villages, etc.)");
		setState(btnGenStructures, "generate-structures");
		
		Button btnSpawnAnimals = new Button(grpGameSettings, SWT.CHECK);
		btnSpawnAnimals.setText("Spawn animals");
		btnSpawnAnimals.setBounds(10, 87, 263, 16);
		setState(btnSpawnAnimals, "spawn-animals");
		
		Button btnAnnounceAchievements = new Button(grpGameSettings, SWT.CHECK);
		btnAnnounceAchievements.setBounds(10, 109, 275, 16);
		btnAnnounceAchievements.setText("Announce player achievements");
		setState(btnAnnounceAchievements, "announce-player-achievements");
		
		Button btnWhitelist = new Button(grpGameSettings, SWT.CHECK);
		btnWhitelist.setToolTipText("If this is turned on, don't forget to add yourself to the list!");
		btnWhitelist.setBounds(10, 131, 120, 16);
		btnWhitelist.setText("Use whitelist");
		setState(btnWhitelist, "white-list");
		
		Link whitelistLink = new Link(grpGameSettings, SWT.NONE);
		whitelistLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				MessageBox msg = new MessageBox(shlProperties, SWT.ICON_INFORMATION);
				msg.setText("How to edit the whitelist:");
				msg.setMessage("To add yourself to the white list, use the \"whitelist add [name]\" command. Use \"whitelist remove [name]\" to remove someone. If you can't join after adding yourself, try \"whitelist reload\".");
				msg.open();
			}
		});
		whitelistLink.setBounds(136, 132, 137, 15);
		whitelistLink.setText("<a>How to edit whitelist</a>");
		
		Button btnAllowNether = new Button(grpGameSettings, SWT.CHECK);
		btnAllowNether.setBounds(10, 153, 275, 16);
		btnAllowNether.setText("Allow nether (recommended)");
		setState(btnAllowNether, "allow-nether");
		
		Button btnCMDBlocks = new Button(grpGameSettings, SWT.CHECK);
		btnCMDBlocks.setBounds(10, 175, 275, 16);
		btnCMDBlocks.setText("Enable command blocks");
		setState(btnCMDBlocks, "enable-command-block");
		
		Combo modeBox = new Combo(shlProperties, SWT.NONE);
		modeBox.setItems(new String[] {"Survival", "Creative", "Adventure", "Spectator"});
		modeBox.setBounds(331, 123, 130, 28);
		modeBox.select(0);
		switch(propsHash.get("gamemode")) {
		case "0":
			modeBox.select(0);
			break;
		case "1":
			modeBox.select(1);
			break;
		case "2":
			modeBox.select(2);
			break;
		case "3":
			modeBox.select(3);
			break;
		default:
			modeBox.select(0);
			break;
		}
		
		Spinner portSpinner = new Spinner(shlProperties, SWT.BORDER);
		portSpinner.setMaximum(65534);
		portSpinner.setMinimum(1);
		portSpinner.setSelection(25565);
		portSpinner.setBounds(331, 171, 75, 22);
		if (propsHash.get("server-port") != null) {
			try {
				portSpinner.setSelection(Integer.parseInt(propsHash.get("server-port")));
			} catch (NumberFormatException e) {
				portSpinner.setSelection(25566);
			}
		}
		
		Button btnSave = new Button(shlProperties, SWT.NONE);
		btnSave.setBounds(331, 291, 130, 25);
		btnSave.setText("Save and close");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// Save the data before closing
				try {
					properties.load(new FileInputStream("server.properties"));
					
					// set motd
					properties.setProperty("motd", motdBox.getText());
					// set world name
					properties.setProperty("level-name", worldNameBox.getText());
					// set gamemode
					properties.setProperty("gamemode", Integer.toString(modeBox.getSelectionIndex()));
					// set server port
					properties.setProperty("server-port", Integer.toString(portSpinner.getSelection()));
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
					ErrorHandler.displayError("Could not open the server.properties file for writing.", ex);
				}
			}
		});
		
		Button btnAdvanced = new Button(shlProperties, SWT.NONE);
		btnAdvanced.setBounds(331, 260, 130, 25);
		btnAdvanced.setText("Advanced...");
		
		Button btnJavaOptions = new Button(shlProperties, SWT.NONE);
		btnJavaOptions.setBounds(331, 226, 130, 28);
		btnJavaOptions.setText("Java Settings...");
		
		Label lblWorld = new Label(shlProperties, SWT.NONE);
		lblWorld.setBounds(10, 58, 130, 14);
		lblWorld.setText("World name");
		
		Label lblNewLabel = new Label(shlProperties, SWT.NONE);
		lblNewLabel.setBounds(331, 151, 59, 14);
		lblNewLabel.setText("Port");
		
		Label lblGamemode = new Label(shlProperties, SWT.NONE);
		lblGamemode.setBounds(331, 103, 107, 14);
		lblGamemode.setText("Gamemode");
			
		btnJavaOptions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// open the java settings panel
				new JavaSettingsPanel().open();
			}
		});
		
		btnAdvanced.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				new AdvancedPropertiesPanel().open();
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
			//System.out.println("Property '" + pName + "' is true, enabling checkbox.");
		}
		else {
			b.setSelection(false);
			//System.out.println("Property '" + pName + "' is false, disabling checkbox.");
		}
	}
}