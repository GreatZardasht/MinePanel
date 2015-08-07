package minePanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
	// to get: propsHash.get("hey"); -> "value"
	Map<String, String> propsHash = new HashMap<String, String>();
	
	public Shell shlProperties;
	private Text motdBox;
	
	public void open() {
		/*// test if it can open (if there's a properties file or not)
		if (!new File("server.properties").exists()) {
			//MessageBox messageBox = new MessageBox(shlProperties, SWT.ICON_WARNING | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
			
			//messageBox.setText("Error");
			//messageBox.setMessage("No server.properties file was found. Run the server to correct this issue, then come back after stopping the server.");
			return;
		}
		*/
		
		// first, load all of the properties into a hashmap
		
		// string in which to store all of the lines of the properties file
		String forArray = "";
		try {
			Scanner s = new Scanner(new BufferedReader(new FileReader("server.properties")));
			
			while (s.hasNext()) {
				System.out.println(s.nextLine());
				
				// this will cut out the comments at the top of the file
				if (!s.nextLine().startsWith("#")) forArray += s.nextLine() + "`";
			}
			
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String[] properties = forArray.split("`");
		
		System.out.println("\n Starting properties array");
		
		for (String prop : properties) {
			
			System.out.println(prop);
			
			String[] p = prop.split("=");
			
			// this will determine which property it is looking at
			switch(p[0]) {
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
		shlProperties = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlProperties.setSize(471, 292);
		shlProperties.setText("Server Properties");
		shlProperties.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// when the shell closes, we must save the data:
				
			}
		});
		
		Label lblMotd = new Label(shlProperties, SWT.NONE);
		lblMotd.setBounds(10, 10, 55, 15);
		lblMotd.setText("MOTD");
		
		motdBox = new Text(shlProperties, SWT.BORDER);
		motdBox.setBounds(10, 31, 440, 21);
		
		Group grpGameSettings = new Group(shlProperties, SWT.NONE);
		grpGameSettings.setText("Game Settings");
		grpGameSettings.setBounds(10, 58, 270, 194);
		
		Button btnPvp = new Button(grpGameSettings, SWT.CHECK);
		btnPvp.setToolTipText("When turned on, players can hurt each other.");
		btnPvp.setBounds(10, 21, 93, 16);
		btnPvp.setText("PVP");
		
		Button btnMobsCanSpawn = new Button(grpGameSettings, SWT.CHECK);
		btnMobsCanSpawn.setToolTipText("If disabled, monsters won't spawn even if the difficulty isn't peaceful.");
		btnMobsCanSpawn.setBounds(10, 43, 236, 16);
		btnMobsCanSpawn.setText("Spawn monsters");
		
		Button btnGenStructures = new Button(grpGameSettings, SWT.CHECK);
		btnGenStructures.setBounds(10, 65, 257, 16);
		btnGenStructures.setText("Generate structures (villages, dungeons, etc.)");
		
		Button btnSpawnAnimals = new Button(grpGameSettings, SWT.CHECK);
		btnSpawnAnimals.setText("Spawn animals");
		btnSpawnAnimals.setBounds(10, 87, 100, 16);
		
		Button btnAnnounceAchievements = new Button(grpGameSettings, SWT.CHECK);
		btnAnnounceAchievements.setBounds(10, 109, 187, 16);
		btnAnnounceAchievements.setText("Announce player achievements");
		
		Button btnWhitelist = new Button(grpGameSettings, SWT.CHECK);
		btnWhitelist.setToolTipText("If this is turned on, don't forget to add yourself to the list!");
		btnWhitelist.setBounds(10, 131, 93, 16);
		btnWhitelist.setText("Use whitelist");
		
		Link whitelistLink = new Link(grpGameSettings, SWT.NONE);
		whitelistLink.setBounds(109, 132, 76, 15);
		whitelistLink.setText("<a>Edit whitelist</a>");
		
		Button btnAllowNether = new Button(grpGameSettings, SWT.CHECK);
		btnAllowNether.setBounds(10, 153, 187, 16);
		btnAllowNether.setText("Allow nether (recommended)");
		
		Button btnCMDBlocks = new Button(grpGameSettings, SWT.CHECK);
		btnCMDBlocks.setBounds(10, 175, 157, 16);
		btnCMDBlocks.setText("Enable command blocks");
		
	}
}

/**
 * basically a simple way to store the properties that I need
 * while this is no longer used, I keep it just in case. most likely removed in another update later on.
 * @author will
 *
 */
class Property {
	public String value;
	public String name;
	
	/**
	 * Constructor for the Property class
	 * @param _name property name
	 * @param _value property value
	 */
	public Property(String _name, String _value) {
		this.name = _name;
		this.value = _value;
	}
}
