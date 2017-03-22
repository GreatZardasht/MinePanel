package minePanel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Class intended to update the files in the server directory, namely server.properties among other things
 * 
 * @author Will Eccles
 */
public class FileManager {

	final static String PROPS = "server.properties"; // Properties file constant
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
			try { // Try to read and write the file
				FileWriter fw = new FileWriter(PROPS);
				BufferedWriter bw = new BufferedWriter(fw);
				FileReader fr = new FileReader(PROPS);
				BufferedReader br = new BufferedReader(fr);
			}
			catch (Exception e) {
				ErrorHandler.displayError("Error while writing to the server.properties file", e);
			}
		}
		else {

		}
	}
}
