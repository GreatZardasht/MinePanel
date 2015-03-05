package minePanel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JTextArea;

import java.io.OutputStream;

// A lot of this class is thanks to the answer here: http://stackoverflow.com/questions/27684050/execute-jar-and-read-the-console-output-using-inputstream-java

// EDIT: Basically completely renovating this whole class.

/**
 * @author Will Eccles
 */
/**
 * @author will
 *
 */
public class ServerRunner {
	private String jarName;
	private int usedRAM;
	private String javaLocation;
	private boolean force64;
	private JTextArea jtp;
	private JButton stpb;
	private JButton strtb;
	
	private ProcessBuilder pb;
	private Process p;
	
	/**
	 * @param jarName The name of the server jar.
	 * @param RAM The amount of RAM in MB to use.
	 * @param javaLoc Location of java.
	 * @param force64bit Whether or not to force 64-bit running of the server.
	 */
	public ServerRunner(String serverJarName, int RAM, String javaLoc, boolean force64bit, JTextArea pane, JButton startButton, JButton stopButton) {
		jarName = serverJarName;
		usedRAM = RAM;
		javaLocation = javaLoc;
		force64 = force64bit;
		jtp = pane;
		stpb = stopButton;
		strtb = startButton;
	}
	
	/**
	 * Run the server. Enables and disables start and stop buttons.
	 */
	public void startServer() {
		// Run the server based on the arguments supplied to the instance of the class
		try {
			strtb.setEnabled(false);
			stpb.setEnabled(true);
			
			pb = new ProcessBuilder(javaLocation, "-jar", jarName, ("-Xmx"+usedRAM+"M"), ("Xms"+usedRAM+"M"), "nogui", force64?"d64":"");
			pb.redirectErrorStream(true);
			p = pb.start();
			
			InputStream in = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				if (jtp != null) {
					jtp.append(line);
				}
				else {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			strtb.setEnabled(true);
			stpb.setEnabled(false);
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
				stpb.setEnabled(false);
				strtb.setEnabled(true);
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
			stpb.setEnabled(false);
			strtb.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}