package minePanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;

import java.io.OutputStream;

// A lot of this class is thanks to the answer here: http://stackoverflow.com/questions/27684050/execute-jar-and-read-the-console-output-using-inputstream-java

// EDIT: Basically completely renovating this whole class.

/**
 * @author Will Eccles
 */
public class ServerRunner {
	private String jarName;
	private int usedRAM;
	private String javaLocation;
	private boolean force64;
	public StyledText consoleBox;
	private boolean nogui;
	private Button runButton;
	private Button quitButton;
	
	private ProcessBuilder pb;
	private Process p;
	
	/**
	 * Class resposible for running the server jar file.
	 * 
	 * @param jarName The name of the server jar.
	 * @param RAM The amount of RAM in MB to use.
	 * @param javaLoc Location of java.
	 * @param force64bit Whether or not to force 64-bit running of the server.
	 * @param useNogui Use nogui mode or not? (USE ONLY IN TESTING, END USE SHOULD NOT SEE GUI)
	 * @param outputBox Text box to send the output of the server console to.
	 * @param startButton Button used to start the server. Messy, but should work in theory.
	 * @param stopButton Button used to stop the server. Again, a bit sloppy.
	 */
	public ServerRunner(String serverJarName, int RAM, String javaLoc, boolean force64bit, boolean useNogui, StyledText outputBox, Button startButton, Button stopButton) {
		jarName = serverJarName;
		usedRAM = RAM;
		javaLocation = javaLoc;
		force64 = force64bit;
		consoleBox = outputBox;
		nogui = useNogui;
		quitButton = stopButton;
		runButton = startButton;
	}
	
	/**
	 * Run the server. Enables and disables start and stop buttons.
	 */
	public void startServer() {
		// Run the server based on the arguments supplied to the instance of the class
		try {
			
			runButton.setEnabled(false);
			quitButton.setEnabled(true);
			
			pb = new ProcessBuilder(javaLocation, "-jar", jarName, ("-Xmx"+usedRAM+"M"), ("Xms"+usedRAM+"M"), nogui?"nogui":"", force64?"d64":"");
			pb.redirectErrorStream(true);
			p = pb.start();
			
			consoleBox.setText("");
			
			InputStream in = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				if (consoleBox != null) {
					if (consoleBox.getText() == null || consoleBox.getText() == "") consoleBox.append(line);
					else consoleBox.append(System.lineSeparator() + line);
				}
				else {
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			System.out.println(e.toString());
			runButton.setEnabled(true);
			quitButton.setEnabled(false);
		}
		
	}
	
	/**
	 * Input commands to the server.
	 * 
	 * @param input String to input to the server. If it's "stop," will disable stop button and enable start.
	 */
	public void input(String input) {
		try {
			OutputStream os = p.getOutputStream();
			os.write(input.getBytes());
			if (input == "stop") {
				p.waitFor();
				p.destroy();
				runButton.setEnabled(true);
				quitButton.setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Stop the server. Will wait for server to finish shutting down then destroy process. Also, enables/disables start/stop buttons respectively.
	 */
	public void stopServer() {
		try {
			input ("stop");
			p.waitFor();
			p.destroy();
			runButton.setEnabled(true);
			quitButton.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}