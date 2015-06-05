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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// EDIT: Basically completely renovated this whole class.

/**
 * @author Will Eccles
 */
public class ServerRunner {
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
	
	/**
	 * Class resposible for running the server jar file.
	 * 
	 * @param jarName The name of the server jar.
	 * @param RAM The amount of RAM in MB to use.
	 * @param javaLoc Location of java.
	 * @param force64bit Whether or not to force 64-bit running of the server.
	 * @param useNogui Use nogui mode or not? (USE ONLY IN TESTING, END USE SHOULD NOT SEE GUI)
	 * @param outputBox Text box to send the output of the server console to.
	 * @param entryBox The text box to use as the command line
	 * @param startButton Button used to start the server. Messy, but should work in theory.
	 * @param stopButton Button used to stop the server. Again, a bit sloppy.
	 * @param clearButton Button used to clear the console.
	 * @param shell The SWT shell holding the app.
	 */
	public ServerRunner(String serverJarName, int RAM, String javaLoc, boolean force64bit, boolean useNogui, StyledText outputBox, Text entryBox, Button startButton, Button stopButton, Button clearButton, Shell shell) {
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
	}
	
	/**
	 * Run the server. Enables and disables start and stop buttons.
	 */
	public void startServer() {
		
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
            
            
            
            // listener for enter key press on console entry box
            Display.getDefault().asyncExec(new Runnable() {
            	@Override
            	public void run() {
            		if (!commandLine.isDisposed()) {
            			commandLine.addKeyListener(new KeyAdapter() {
                			@Override
                			public void keyPressed(KeyEvent e) {
                				// if it's the enter key
                				if (e.keyCode == org.eclipse.swt.SWT.CR) {
                					// if there was a command in the text box
                					if (!commandLine.getText().equals("")) {
                						// SHOULD run the command
                						scr.command(commandLine.getText());
                						commandLine.setText("");
                					}
                					// no else because that would be useless here lol
                				}
                			}
                		});
            		}
            	}
            });
            
            // listener for stop button
            Display.getDefault().asyncExec(new Runnable() {
            	public void run() {
            		if (!quitButton.isDisposed()) {
            			quitButton.addMouseListener(new MouseAdapter() {
            				@Override
                			public void mouseUp(MouseEvent e) {
                				// first run the stop command
            					consolePrint("Stopping server [Stop button]");
            					scr.command("stop");
            					
            					runButton.setEnabled(true);
            		            quitButton.setEnabled(false);
                			}
                		});
                	}
            	}
            });
            
            // this is our listener for when the shell closes
            Display.getDefault().asyncExec(new Runnable() {
            	public void run() {
            		MPShell.addListener(SWT.Close, new Listener() {
            			@Override
            			public void handleEvent(Event e) {
            				// this is to be done when the shell's close button is clicked.
            				// if the server is still running, it will send the stop command.
            				// this ensures that the user doesn't close it and end up not saving things.
            			}
            		});
            	}
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        
	}
	
	/**
	 * This method will allow printing to the console from another thread
	 * @param display Display containing the console box
	 * @param cBox Console box
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
	
	// dis be uzelezz
	class ServerConsoleWriter implements Runnable {
		private BufferedWriter writer;
		public ServerConsoleWriter(OutputStream s) {
			this.writer = new BufferedWriter(new OutputStreamWriter(s));
		}
		
		public void run() {
			while (true) {
				if (!commandLine.isDisposed()) {
					try {
						writer.write(commandLine.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}


