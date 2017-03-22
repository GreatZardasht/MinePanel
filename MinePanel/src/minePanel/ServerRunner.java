package minePanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


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
	private Shell MPShell = Main._panel.shlMinepanel; // the shell, I add a handler later
	private Button propsButton;

	// This is being used instead of String[] because the ArrayList is mutable whereas the usual String[] array is not.
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int currentIndex = 0;
	private String currentCommand = "";

	private ServerConsoleReader scr;
	private String _motd; // since this will be used more than once but will only need to be loaded once, we can store it here

	private HashMap<String, Vector<String>> customCommands = new HashMap<String, Vector<String>>();

	/**
	 * Class resposible for running the server jar file.
	 * And yes, it has the worst constructor params in the world. Don't judge me.
	 * 
	 * @param serverJarName The name of the server jar.
	 * @param RAM The amount of RAM in MB to use.
	 * @param javaLoc Location of java.
	 * @param force64bit Whether or not to force 64-bit running of the server.
	 * @param useNogui Use nogui mode or not? (USE ONLY IN TESTING, END USER SHOULD NOT SEE GUI)
	 * @param outputBox Reference to text box to send the output of the server console to.
	 * @param entryBox Reference to the text box to use as the command line
	 * @param startButton Reference to the button used to start the server.
	 * @param stopButton Reference to the button used to start the server.
	 * @param clearButton Button used to clear the console.
	 * @param shell Reference to the SWT shell holding the app.
	 * @param propertiesButton Reference to the button used to open the properties menu.
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
	 * Run the server, managing buttons on the UI that have to do with running and stopping the server as needed.
	 */
	public void startServer() {

		// print some stuff and then clear the console before starting the server
		consoleBox.setText("");
		consolePrint("Loading MOTD from server.properties...");

		try {
			Properties properties = new Properties();

			properties.load(new FileInputStream("server.properties"));

			_motd = properties.getProperty("motd");

			if (_motd.equals(null)) {
				consolePrint("No MOTD set.");
			}

		} catch (Exception e) {
			consolePrint("Issue reading MOTD from server.properties, are you sure it's there?");
		}

		consolePrint("Starting Minecraft server...");

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
			Display.getDefault().asyncExec(() -> {
				try {
					if (new File("MPCommands.txt").exists()) {
						Scanner s = new Scanner(new BufferedReader(new FileReader("MPCommands.txt")));

						String commandName = "";
						Vector<String> commandValue = new Vector<String>();

						while(s.hasNext()) {
							String line = s.nextLine();

							if (line.matches("^![A-Za-z]+:$")) {
								// if there is already a command ready to be put in the list, go ahead and do that
								if (!commandName.equals("") && !commandValue.isEmpty()) {
									customCommands.put(commandName, new Vector<String>(commandValue));
								}
								// save the new command name and get it ready to add to the list
								commandName = line.replaceAll("[^A-Za-z]", "");
								commandValue.removeAllElements(); // empty the command value vector for reuse
								commandValue.trimToSize();
							}
							else if (line.matches("^/.+$")) {
								// add the command to the commands list
								commandValue.addElement(line.replaceAll("^/", "").trim());
							}
						}

						// clean up the last command, which won't get caught by the while loop
						if (!commandName.equals("") && !commandValue.isEmpty()) {
							customCommands.put(commandName, new Vector<String>(commandValue));
						}

						s.close();
					}
				} catch (Exception e) {
					ErrorHandler.displayError("Could not load the custom commands file", e);
				}

			});

			// listeners being added on another thread asyncronously
			Display.getDefault().asyncExec(() -> {
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
							consolePrint("Stop button pressed. Stopping server.");
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
			});

		} catch (IOException e) {
			consolePrint("Unable to start server...");
			consolePrint(e.getMessage());
			consolePrint("Did you configure your java path correctly?");
			ErrorHandler.displayError("Unable to start the server", "The server process could not be started, do you have java in your path?", e);
		}
	}

	/**
	 * This method will allow printing to the console from another thread/just makes it way easier to print to the console.
	 * @param line Line to print to the console box
	 */
	public void consolePrint(final String line) {
		Display.getDefault().asyncExec(() -> {
			if (!consoleBox.isDisposed()) {
				if (consoleBox.getText() == null || consoleBox.getText() == "") consoleBox.append("[Minepanel] " + line);
				else consoleBox.append(System.lineSeparator() + "[Minepanel] " + line);

				// this is where we test to see if the stop command was issued in chat
				// it looks like this:
				//    [21:15:54] [Server thread/INFO]: [CactusMcFly: Stopping the server]
				if (line.matches("^\\[[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\] \\[Server thread/INFO\\]: \\[(�[a-z0-9A-Z])?[A-Za-z0-9_]{1,16}(�[a-z0-9A-Z])?: Stopping the server\\]$")) {
					runButton.setEnabled(true);
					quitButton.setEnabled(false);
					propsButton.setEnabled(true);
				}

				// this is for when the user hasn't accped the EULA
				// it'll show them a quick little message explaining how to do that
				// even though I could automate that, it makes more sense for the end user to actually manually agree to the EULA
				else if (line.matches("^\\[[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\] \\[Server thread/INFO\\]: You need to agree to the EULA in order to run the server. Go to eula.txt for more info.")) {
					MessageBox msg = new MessageBox(MPShell, SWT.ICON_INFORMATION);
					msg.setText("You need to accept the EULA");
					msg.setMessage("The server will not run for the first time if you haven't accepted the EULA. The server has created a file called \"eula.txt\" in its folder. Go to that file and change the line that ends with \"=false\" to \"=true\" and then come back and run the server again.");
					msg.open();
					runButton.setEnabled(true);
					quitButton.setEnabled(false);
					propsButton.setEnabled(true);
				}

				else if (line.matches("^\\[[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\] \\[Server thread/INFO\\]: (�[a-z0-9A-Z])?[A-Za-z0-9_]{1,16}(�[a-z0-9A-Z])? joined the game$")) {
					String[] parts = line.split(" ");
					String uName = parts[3].replaceAll("(�[a-z0-9A-Z])", "");
					if (!_motd.equals(null))
						scr.command("tellraw " + uName + "[{\"text\":\"MOTD: \",\"bold\":true,\"color\":\"green\"},{\"text\":\"" + _motd + "\"}]");
					scr.command("tellraw " + uName + " {\"text\":\"Welcome, " + uName + "! This server uses MinePanel. Use !commands\",\"color\":\"gold\"}");
					scr.command("tellraw " + uName + " {\"text\":\"to see all the custom commands you can use here.\",\"color\":\"gold\"}");
				}

				// this is where it gets fun. we will now get into custom commands.
				// at the time of this writing, there are other, more important things to do first,
				// but this is more fun ;)
				// this starts by catching anything people say that starts with !
				else if (line.matches("^\\[[0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\] \\[Server thread/INFO\\]: <(�[a-z0-9A-Z])?[A-Za-z0-9_]{1,16}(�[a-z0-9A-Z])?> ![A-Za-z]+$")) {
					String[] parts = line.split(" ");
					String uName = parts[3].replace("<", "").replace(">", "").replaceAll("(�[a-z0-9A-Z])", "");
					String command = parts[4].replace("!", "");

					if (command.equals("commands")) {
						scr.command("tellraw " + uName + " {\"text\":\"Commands available through MinePanel:\",\"color\":\"green\",\"bold\":true}");
						scr.command("tellraw " + uName + " [{\"text\":\"    !commands:\",\"bold\":false,\"color\":\"gold\"},{\"text\":\" shows this list\",\"color\":\"white\",\"italic\":true}]");
						scr.command("tellraw " + uName + " [{\"text\":\"    !motd:\",\"bold\":false,\"color\":\"gold\"},{\"text\":\" shows the MOTD of the server\",\"color\":\"white\",\"italic\":true}]");

						// iterate over the custom commands, if there are any
						Iterator<Map.Entry<String, Vector<String>>> it = customCommands.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, Vector<String>> pair = it.next();
							scr.command("tellraw " + uName + " [{\"text\":\"    !" + pair.getKey() + "\",\"color\":\"gold\",\"bold\":true}]");
						}
					}
					else if (command.equals("motd")) {
						if (!_motd.equals(null)) {
							scr.command("tellraw @a [{\"text\":\"MOTD: \",\"bold\":true,\"color\":\"green\"},{\"text\":\"" + _motd + "\"}]");
						} else {
							scr.command("tellraw @a {\"text\":\"No MOTD set.\",\"color\":\"red\"}");
						}
					} else {
						if (!customCommands.containsKey(command)) {
							scr.command("tellraw " + uName + " {\"text\":\"Command '!" + command + "' does not exist.\",\"color\":\"red\"}");
						}
						else {
							for (String comm : customCommands.get(command)) {
								scr.command(comm.replace("$[user]", uName).replace("$[command]", command));
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
