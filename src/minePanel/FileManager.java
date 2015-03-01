package minePanel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class FileManager {
	
	final static String PROPS = "server.txt"; // Properties file constant
	final static String SERVER = "server.jar"; // Server file constant
	
	
	/** Check if "server.jar" is in folder. */
	public static boolean checkJar() {
		boolean check = new File(SERVER).exists();
		if (check == true) return true;
		else return false;
	}
	
	public static boolean checkProps() {
		boolean check = new File(PROPS).exists();
		if (check == true) return true;
		else return false;
	}
	
	public static void setProp(String propertyName, String propertyValue) {
		if (checkProps() == true) {
			try { // Try to read and write the file, and catch error e if not possible
				FileWriter fw = new FileWriter(PROPS);
				BufferedWriter bw = new BufferedWriter(fw);
				FileReader fr = new FileReader(PROPS);
				BufferedReader br = new BufferedReader(fr);
				
				String line = null;
				String allLines = "";
				
				while ((line = br.readLine()) != null) {
					allLines += line + System.lineSeparator();
				}
				
				allLines.replaceAll(propertyName+"[ ]*=[^" + System.lineSeparator() + "]*", propertyName+"="+propertyValue);
				
				FileOutputStream file = new FileOutputStream(PROPS);
				file.write(allLines.getBytes());
				
				file.close();
				bw.close();
				br.close();
			}
			catch (Exception e) {
				System.err.println(e);
			}
		}
		else {
			
		}
	}
}
