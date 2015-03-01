package minePanel;

public class Main {
	//static Panel mainGUI = new Panel();
	static FileManager fm = new FileManager();
	
	public static void main(String[] args) {
		// Enable the GUI (a bit necessary)
		//mainGUI.setEnabled(true);
		//mainGUI.setVisible(true);
		
		
		FileManager.setProp("motd", "penis");
	}
}
