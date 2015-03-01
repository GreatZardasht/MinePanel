package minePanel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// A lot of this class is thanks to the answer here: http://stackoverflow.com/questions/15700879/how-to-run-a-java-executable-jar-in-another-java-program

public class ServerRunner {
	
	public ServerRunner(String jarName, String RAM, String javaLocation) {
		
		ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarName);
		
		try {
			Process p = pb.start();
			LogStreamReader lsr = new LogStreamReader(p.getInputStream());
			Thread thread = new Thread(lsr, "LogStreamReader");
			thread.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class LogStreamReader implements Runnable {
	
	private BufferedReader reader;
	
	public LogStreamReader(InputStream is) {
		this.reader = new BufferedReader(new InputStreamReader(is));
	}
	
	public void run() {
		try {
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
