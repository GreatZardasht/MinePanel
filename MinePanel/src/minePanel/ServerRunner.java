package minePanel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The class in charge of running the server, making sure commands work, and so on.
 * @author Will Eccles
 * @version idek anymore
 */
public class ServerRunner {
	
	// enter key (AKA CRLF)
	private final char ENTER = org.eclipse.swt.SWT.CR;
	// up arrow
	private final int UP = org.eclipse.swt.SWT.ARROW_UP;
	// down arrow
	private final int DOWN = org.eclipse.swt.SWT.ARROW_DOWN;
	
	private final String commandSplitter = "          ";
	
	private String jarName;
	private int usedRAM;
	private String javaLocation;
	private boolean force64;
	public static StyledText consoleBox;
	private static Text commandLine;
	private boolean nogui;
	private Button runButton;
	private Button quitButton;
	private Button clrButton;
	private Shell MPShell = Main.panel.shlMinepanel; // the shell, I add a handler later
	private Button propsButton;
	
	// This is being used instead of String[] because the ArrayList is mutable whereas the usual String[] array is not.
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int currentIndex = 0;
	private String currentCommand = "";
	
	private ServerConsoleReader scr;
	
	private HashMap<String, String> customCommands = new HashMap<String, String>();
	
	/**
	 * Class resposible for running the server jar file.
	 * And yes, it has the worst constructor params in the world. Don't judge me.
	 * 
	 * @param jarName The name of the server jar.
	 * @param RAM The amount of RAM in MB to use.
	 * @param javaLoc Location of java.
	 * @param force64bit Whether or not to force 64-bit running of the server.
	 * @param useNogui Use nogui mode or not? (USE ONLY IN TESTING, END USER SHOULD NOT SEE GUI)
	 * @param outputBox Text box to send the output of the server console to.
	 * @param entryBox The text box to use as the command line
	 * @param startButton Button used to start the server. Messy, but should work in theory.
	 * @param stopButton Button used to stop the server. Again, a bit sloppy.
	 * @param clearButton Button used to clear the console.
	 * @param shell The SWT shell holding the app.
	 * @param propertiesButton Button used to open the properties menu.
	 */
	public ServerRunner(String serverJarName, int RAM, String javaLoc, boolean force64bit, boolean useNogui, StyledText outputBox, Text entryBox, Button startButton, Button stopButton, Button clearButton, Shell shell, Button propertiesButton) {
		jarName = serverJarName;
		usedRAM = RAM;
		javaLocation = javaLoc;
		force64 = force64bit;
		commandLine = entryBox;
		consoleBox = outputBox;
		nogui = useNogui;
		quitButton = stopButton;
		runButton = startButton;
		clrButton = clearButton;
		MPShell = shell;
		propsButton = propertiesButton;
	}
	
	/**
	 * Run the server. Enables and disables the appropriate buttons as well.
	 */
	public void startServer() {
		
		// print some stuff and then clear the console before starting the server
		consoleBox.setText("");
		consolePrint("[Minepanel] Starting Minecraft server...");
		
		String[] jarArgs = {javaLocation, "-jar", jarName, ("-Xmx"+usedRAM+"M"), ("-Xms"+usedRAM+"M"), nogui?"nogui":"", force64?"d64":""};
		
		ProcessBuilder pb = new ProcessBuilder(jarArgs);
        try {
            Process p = pb.start();
            scr = new ServerConsoleReader(p.getInputStream(), p.getOutputStream());
            
            // serverconsolereader's thread
            Thread readerThread = new Thread(scr, "ServerConsoleReader");
            readerThread.start();
            
            // set button states
            runButton.setEnabled(false);
            quitButton.setEnabled(true);
            propsButton.setEnabled(false);
            
            // this will load commands at the same time as everything else is happening. pretty handy for long lists of commands.
            // also, a lambda! OOOOOOH
            Display.getDefault().asyncExec(() -> {
            	try {
            		if (new File("MPCommands.txt").exists()) {
            			Scanner s = new Scanner(new BufferedReader(new FileReader("MPCommands.txt")));
            			
            			String commandName = "";
            			String commandValue = "";
            			
            			while (s.hasNext()) {
            				String line = s.nextLine();
            				
            				// if the previous command and command value have been added, add them to the list
            				// this only happens if the next line is a new command
        					if (!commandName.equals("") && !commandValue.equals("") && line.matches("^(!+)?[A-Za-z]+:$")) {
        						customCommands.put(commandName, commandValue);
        					}
            				if (line.matches("^(!+)?[A-Za-z]+:$")) {	
            					// save the new command name and get its value to add to the list
            					commandName = line.replaceAll("[^A-Za-z]", "");
            					commandValue = "";
            				}
            				if (line.matches("^/.+$")) {
            					// add the command to the command value with an additional 10 spaces at the end for splitting later
            					commandValue += line.replaceAll("^/", "").trim() + commandSplitter;
            				}
            			}
            			
            			// clean up the last command, which won't get caught by the while loop
            			if (!commandName.equals("") && !commandValue.equals("")) {
            				customCommands.put(commandName, commandValue);
            			}
            			
            			s.close();
            		}
            	} catch (Exception e) {
            		ErrorHandler.displayError("Could not load the custom commands file", e);
            	}
            	
            });
            
            // listeners being added on another thread asyncronously
            Display.getDefault().asyncExec(new Runnable() {
            	@Override
            	public void run() {
            		
            		// add enter key listener for command line
            		if (!commandLine.isDisposed()) {
            			commandLine.addKeyListener(new KeyAdapter() {
                			@Override
                			public void keyPressed(KeyEvent e) {
                				// if it's the enter key
                				if (e.keyCode == ENTER) {
                					String com = commandLine.getText().trim();
                					
                					// if there was a command in the text box AND the server is actually running
                					if (!com.equals("") && p.isAlive() == true) {
                						// run the command
                						scr.command(com);
                						
                						commandHistory.add(com);
                						
                						// reset position in command history if you press enter
                						currentIndex = commandHistory.size();
                						
                						// if the command was the stop command we need to disable and enable some buttons
                						if (com.toLowerCase().equals("stop")) {
                							runButton.setEnabled(true);
                							quitButton.setEnabled(false);
                							propsButton.setEnabled(true);
                						}
                							
                						commandLine.setText("");
                					}
                					// no else because that would be useless here lol
                				}
                			}
                		});
            			
            			commandLine.addKeyListener(new KeyAdapter() {
                			@Override
                			public void keyPressed(KeyEvent e) {
                				// if it's an arrow key
                				if (e.keyCode == UP || e.keyCode == DOWN) {
                					String com = commandLine.getText().trim();
                					
                					if (e.keyCode == UP && currentIndex == commandHistory.size()) {
                						currentCommand = com;
                					}
                					
                					// now based on which arrow key was pressed, either go up in history or down
                					switch(e.keyCode) {
                					case UP:
                						
                						if (commandHistory.size() > 0 && currentIndex > 0) {
                							currentIndex --;
                							commandLine.setText(commandHistory.get(currentIndex));
                							
                						}
                						break;
                					case DOWN:
                						
                						if (commandHistory.size() > 0 && currentIndex < commandHistory.size() - 1) {
                							currentIndex ++;
                							commandLine.setText(commandHistory.get(currentIndex));
                							
                						}
                						else if (currentIndex == commandHistory.size() - 1) {
                							
                							currentIndex ++;
                							commandLine.setText(currentCommand);
                							
                						}
                						break;
                					}
                					
                					
                				}
                				commandLine.setSelection(commandLine.getText().length());
                			}
                			
                		});
            		}
            		
            		// stop button handler
            		if (!quitButton.isDisposed()) {
            			quitButton.addMouseListener(new MouseAdapter() {
            				@Override
                			public void mouseUp(MouseEvent e) {
                				// first run the stop command
            					consolePrint("[Minepanel] Stop button pressed. Stopping server.");
            					scr.command("stop");
            					
            					runButton.setEnabled(true);
            		            quitButton.setEnabled(false);
            		            propsButton.setEnabled(true);
                			}
                		});
                	}
            		
            		// when the shell is gonna close
            		MPShell.addListener(SWT.Close, new Listener() {
            			@Override
            			public void handleEvent(Event e) {
            				if (p.isAlive()) {
            					// in case someone closes it before shutting off the server thread, this will nicely stop the server in the background.
            					// as the server is another thread, it will do this in the background even if the app is closed.
            					scr.command("stop");
            				}
            			}
            		});
            		
            		// clear button handler
            		if (!clrButton.isDisposed()) {
            			clrButton.addMouseListener(new MouseAdapter() {
            				@Override
            				public void mouseUp(MouseEvent e) {
            					consoleBox.setText("");
            				}
            			});
            		}
            		
            	}
            });
            
        } catch (IOException e) {
    		consolePrint("[Minepanel] Unable to start server...");
    		consolePrint(e.getMessage());
    		consolePrint("[Minepanel] Did you configure your java path correctly?");
            ErrorHandler.displayError("Unable to start the server", "The server process could not be started, do you have java in your path ?", e);
        }
		
        
	}
	
	/**
	 * This method will allow printing to the console from another thread/just makes it way easier to print to the console.
	 * @param line Line to print to the console box
	 */
	public void consolePrint(final String line) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!consoleBox.isDisposed()) {
					if (consoleBox.getText() == null || consoleBox.getText() == "") consoleBox.append(line);
					else consoleBox.append(System.lineSeparator() + line);
					
					// this is where we test to see if the stop command was issued in chat
					// it looks like this:
					//    [21:15:54] [Server thread/INFO]: [CactusMcFly: Stopping the server]
					if (line.matches("^.[0-9][0-9]:[0-9][0-9]:[0-9][0-9]. .Server thread/INFO.: .(§[a-z0-9A-Z])?[A-Za-z0-9_]{1,16}(§[a-z0-9A-Z])?: Stopping the server.$")) {
						runButton.setEnabled(true);
    		            quitButton.setEnabled(false);
    		            propsButton.setEnabled(true);
					}
					
					if (line.matches("^.[0-9][0-9]:[0-9][0-9]:[0-9][0-9]. .Server thread/INFO.: (§[a-z0-9A-Z])?[A-Za-z0-9_]{1,16}(§[a-z0-9A-Z])? joined the game$")) {
						String[] parts = line.split(" ");
						String uName = parts[3].replaceAll("(§[a-z0-9A-Z])", "");
						scr.command("tellraw " + uName + " {text:\"Welcome, " + uName + "! This server uses MinePanel by Will Eccles. Use !commands\",color:gold}");
						scr.command("tellraw " + uName + " {text:\"to see all of the commands added to this server!\",color:gold}");
					}
					
					// this is where it gets fun. we will now get into custom commands.
					// at the time of this writing, there are other, more important things to do first,
					// but this is more fun ;)
					// this starts by catching anything people say that starts with !
					if (line.matches("^.[0-9][0-9]:[0-9][0-9]:[0-9][0-9]. .Server thread/INFO.: <(§[a-z0-9A-Z])?[A-Za-z0-9_]{1,16}(§[a-z0-9A-Z])?> ![A-Za-z]+$")) {
						String[] parts = line.split(" ");
						String uName = parts[3].replace("<", "").replace(">", "").replaceAll("(§[a-z0-9A-Z])", "");
						String command = parts[4].replace("!", "");
						
						if (command.equals("commands")) {
							scr.command("tellraw " + uName + " {text:\"Custom commands available through MinePanel:\",color:green,italic:true}");
							scr.command("tellraw " + uName + " {text:\"    !commands - show this list\",color:yellow}");
							scr.command("tellraw " + uName + " {text:\"    !motd - show the server MOTD\",color:yellow}");
							
							// iterate over the custom commands, if there are any
							Iterator it = customCommands.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pair = (Map.Entry)it.next();
								scr.command("tellraw " + uName + " {text:\"    !" + pair.getKey() + "\",color:yellow}");
							}
							
						}
						if (command.equals("motd")) {
							Properties properties = new Properties();
							String MOTD;
							try {
								
								properties.load(new FileInputStream("server.properties"));
								
								MOTD = properties.getProperty("motd");
								
								if (!MOTD.equals(null)) {
									scr.command("tellraw @a {text:\"Server MOTD:\",color:green}");
									scr.command("tellraw @a {text:\"" + MOTD + "\",color:green,italic:true}");
								} else {
									scr.command("tellraw @a {text:\"No MOTD found. Check server.properties and make sure the motd line is there.\",color:red}");
								}
								
							} catch (Exception e) {
								ErrorHandler.displayError("Could not load the MOTD from the server.properties file!", e);
							}
							
							
						}
						if (!command.equals("commands") && !command.equals("motd")) {
							if (!customCommands.containsKey(command)) {
								scr.command("tellraw " + uName + " {text:\"Command '!" + command + "' does not exist.\",color:red}");
							}
							else {
								String[] commands = customCommands.get(command).split(commandSplitter);
								for (String comm : commands) {
									scr.command(comm.replace("$[user]", uName).replace("$[command]", command));
								}
							}
						}
						
					}
				}
			}
		});
	}
	
	class ServerConsoleReader implements Runnable {
		private BufferedReader reader;
		private OutputStream os;
		private PrintWriter w;
		public ServerConsoleReader(InputStream is, OutputStream os) {
			this.reader = new BufferedReader(new InputStreamReader(is));
			this.os = os;
			this.w = new PrintWriter(os);
		}
		
		public void run() {
			try {
				String line = reader.readLine();
				while (line != null) {
					consolePrint(line);
					line = reader.readLine();
				}
				reader.close();
			} catch(IOException e) {
				ErrorHandler.displayError("An error occurred while trying to read the output from the server process.", e);
			}
		}
		
		public void command(final String com) {
			try {
				// this writes the command to the server
				// it uses the \n to simulate the user inputting the text manually :)
				w.write(com);
				w.write("\n");
				w.flush();
				
			} catch (Exception e) {
				ErrorHandler.displayError("The command could not be executed for an unknown reason", e);
			}
		}
	}
}


