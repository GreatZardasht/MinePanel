package minePanel;

import java.io.BufferedReader;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The class in charge of running the server, making sure commands work, and so on.
 * @author Will Eccles
 * @version idek anymore
 */
public class ServerRunner {
	
	// enter key (AKA character return)
	private final char ENTER = org.eclipse.swt.SWT.CR;
	// up arrow
	private final int UP = org.eclipse.swt.SWT.ARROW_UP;
	// down arrow
	private final int DOWN = org.eclipse.swt.SWT.ARROW_DOWN;
	
	
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
	private int currentIndex = commandHistory.size();
	private String currentCommand = "";
	
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
            ServerConsoleReader scr = new ServerConsoleReader(p.getInputStream(), p.getOutputStream());
            
            // serverconsolereader's thread
            Thread readerThread = new Thread(scr, "ServerConsoleReader");
            readerThread.start();
            
            // set button states
            runButton.setEnabled(false);
            quitButton.setEnabled(true);
            propsButton.setEnabled(false);
            
            
            
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
                					
                					// if there was a command in the text box
                					if (!com.equals("")) {
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
                					
                					if (!com.equals("")) {
                						// if the command entered isn't nothing, then store it
                						currentCommand = com;
                					}
                					else {
                						currentCommand = "";
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
                						if (currentIndex == commandHistory.size() - 1) {
                							if (!currentCommand.equals("")) {
                								commandLine.setText(currentCommand);
                							}
                							else {
                								commandLine.setText("");
                							}
                						}
                						break;
                					}
                				}
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
            e.printStackTrace();
        }
		
        
	}
	
	/**
	 * This method will allow printing to the console from another thread
	 * @param line Line to print to the console box
	 */
	public static void consolePrint(final String line) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!consoleBox.isDisposed()) {
					if (consoleBox.getText() == null || consoleBox.getText() == "") consoleBox.append(line);
					else consoleBox.append(System.lineSeparator() + line);
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
				e.printStackTrace();
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
				consolePrint("Command failed for some reason, see stack trace.");
				e.printStackTrace();
			}
		}
	}
}


