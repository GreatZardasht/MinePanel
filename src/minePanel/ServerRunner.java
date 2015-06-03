package minePanel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
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
	 */
	public ServerRunner(String serverJarName, int RAM, String javaLoc, boolean force64bit, boolean useNogui, StyledText outputBox, Text entryBox, Button startButton, Button stopButton) {
		jarName = serverJarName;
		usedRAM = RAM;
		javaLocation = javaLoc;
		force64 = force64bit;
		commandLine = entryBox;
		consoleBox = outputBox;
		nogui = useNogui;
		quitButton = stopButton;
		runButton = startButton;
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
            Thread readerThread = new Thread(scr, "ServerConsoleReader");
            readerThread.start();
            
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
	 * @param display Display containing the console box
	 * @param cBox Console box
	 * @param line Line to print to the console box
	 */
	public static void consolePrint(final Display display, final StyledText cBox, final String line) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!cBox.isDisposed()) {
					if (cBox.getText() == null || cBox.getText() == "") cBox.append(line);
					else cBox.append(System.lineSeparator() + line);
				}
			}
		});
	}
	
	/**
	 * Input commands to the server.
	 * 
	 * @param input String to input to the server. If it's "stop," will disable stop button and enable start.
	 */
		/*public void input(String input) {
		try {
			OutputStream os = proc.getOutputStream();
			os.write(input.getBytes());
			if (input == "stop") {
				proc.waitFor();
				proc.destroy();
				runButton.setEnabled(true);
				quitButton.setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
	/**
	 * Stop the server. Will wait for server to finish shutting down then destroy process. Also, enables/disables start/stop buttons respectively.
	 */
	/*
	public void stopServer() {
		try {
			input ("stop");
			proc.waitFor();
			proc.destroy();
			runButton.setEnabled(true);
			quitButton.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	class ServerConsoleReader implements Runnable {
		private BufferedReader reader;
		private BufferedWriter writer;
		public ServerConsoleReader(InputStream is, OutputStream os) {
			this.reader = new BufferedReader(new InputStreamReader(is));
			this.writer = new BufferedWriter(new OutputStreamWriter(os));
		}
		
		public void run() {
			try {
				String line = reader.readLine();
				while (line != null) {
					consolePrint(Display.getDefault(), consoleBox, line);
					line = reader.readLine();
				}
				reader.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public void command(String com) {
			try {
				writer.write(com);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
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


