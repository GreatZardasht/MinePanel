package minePanel;

public class Main {
	static SWTPanel panel = new SWTPanel();
	static FileManager fm = new FileManager();
	/**
	 * This is my personal JRE location. Should NEVER be used in production.
	 */
	final static String MY_JAVA_LOC = "c:\\Program Files\\Java\\jre1.8.0_40\\bin\\java.exe";
	
	public static void main(String[] args) {
		/* The following is for the old Swing class. While it still is included with the project,
		 * I'm switching to SWT.
		// Enable the GUI (a bit necessary)
		mainGUI.setEnabled(true);
		mainGUI.setVisible(true);
		*/
		
		panel.open();
	}
}
