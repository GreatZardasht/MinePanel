package minePanel;

public class Main {
	public static SWTPanel panel = new SWTPanel();
	static FileManager fm = new FileManager();

	// This is my personal JRE location. Should NEVER be used in production.
	// ****NOTE**** If you are a beta tester, programmer, etc. and you want to run the program, it's on you ...
	// ... to fix this part. You'll need your own string.
	final static String MY_JAVA_LOC = "c:\\Program Files\\Java\\jre1.8.0_45\\bin\\java.exe";
	
	public static void main(String[] args) {
		/* The following is for the old Swing class. While it still is included with the project,
		 * I'm switching to SWT. This is for if things go bad with SWT.
		// Enable the GUI (a bit necessary)
		mainGUI.setEnabled(true);
		mainGUI.setVisible(true);
		*/
		
		panel.open();
	}
}
