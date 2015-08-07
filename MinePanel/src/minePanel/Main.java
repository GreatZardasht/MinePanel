package minePanel;

public class Main {
	public static SWTPanel panel = new SWTPanel();
	static FileManager fm = new FileManager();

	// This is my personal JRE location. Should NEVER be used in production.
	// ****NOTE**** If you are a beta tester, programmer, etc. and you want to run the program, it's on you ...
	//     to fix this part. You'll need your own string. Use the JRE, not the JDK. Although the JDK might work (IDK),
	//     the average user will use the JRE, and any issues that arise with the JDK may be extraneous.
	final static String MY_JAVA_LOC = "c:\\Program Files\\Java\\jre1.8.0_51\\bin\\java.exe";
	
	public static void main(String[] args) {
		panel.open();
	}
}
