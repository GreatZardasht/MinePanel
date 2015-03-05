package minePanel;

public class Main {
	static Panel mainGUI = new Panel();
	static FileManager fm = new FileManager();
	/**
	 * This is my personal JRE location. Should NEVER be used in production.
	 */
	final static String MY_JAVA_LOC = "c:\\Program Files\\Java\\jre1.8.0_40\\bin\\java.exe";
	
	public static void main(String[] args) {
		// Enable the GUI (a bit necessary)
		mainGUI.setEnabled(true);
		mainGUI.setVisible(true);

		/* Moved the serverrunner i had here to the frame, makes it easier to work with */
	}
}
