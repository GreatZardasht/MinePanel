package minePanel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextPane;

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
	@SuppressWarnings("unused")
	private JTextPane jtp;
	
	/**
	 * @param jarName The name of the server jar.
	 * @param RAM The amount of RAM in MB to use.
	 * @param javaLoc Location of java.
	 * @param force64bit Whether or not to force 64-bit running of the server.
	 */
	public ServerRunner(String serverJarName, int RAM, String javaLoc, boolean force64bit, JTextPane pane) {
		jarName = serverJarName;
		usedRAM = RAM;
		javaLocation = javaLoc;
		force64 = force64bit;
		jtp = pane;
	}
	
	/**
	 * Run the server.
	 */
	public void startServer() {
		// Run the server based on the arguments supplied to the instance of the class
		try {
			ProcessBuilder pb = new ProcessBuilder(javaLocation, "-jar", jarName, ("-Xmx"+usedRAM+"M"), ("Xms"+usedRAM+"M"), "nogui", force64?"d64":"");
			pb.redirectErrorStream(true);
			Process p = pb.start();
		
			InputStream in = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			
			p.waitFor();
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}