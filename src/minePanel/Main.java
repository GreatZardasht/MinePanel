package minePanel;

public class Main {
	//static Panel mainGUI = new Panel();
	static FileManager fm = new FileManager();
	/**
	 * This is my personal JRE location. Should NEVER be used in production.
	 */
	final static String MY_JAVA_LOC = "c:\\Program Files\\Java\\jre1.8.0_31\\bin\\java.exe";
	
	public static void main(String[] args) {
		// Enable the GUI (a bit necessary)
		//mainGUI.setEnabled(true);
		//mainGUI.setVisible(true);
		
		// Doing this in tests with a server jar named "server.jar" worked. No input to console yet, but that's next on the list.
		ServerRunner runner = new ServerRunner("server.jar", 3096, MY_JAVA_LOC, true, null);
		runner.startServer();
	}
}
