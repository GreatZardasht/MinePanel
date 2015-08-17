package minePanel;

import java.io.File;

/**
 * while this class is not strictly necessary due to the main() in ServerPanel, I am too lazy to fix it so it will remain.
 * @author will
 *
 */
public class Main {
	public static ServerPanel panel = new ServerPanel();
	static FileManager fm = new FileManager();

	// This is my personal JRE location. Should NEVER be used in production.
	// ****NOTE**** If you are a beta tester, programmer, etc. and you want to run the program, it's on you ...
	//     to fix this part. You'll need your own string. Use the JRE, not the JDK. Although the JDK might work (IDK),
	//     the average user will use the JRE, and any issues that arise with the JDK may be extraneous.
	static String MY_JAVA_LOC = "c:\\Program Files\\Java\\jre1.8.0_51\\bin\\java.exe";
	
	public static void main(String[] args) {
		if (!(new File(MY_JAVA_LOC)).exists()) MY_JAVA_LOC = getJavaDir();
		
		// at some point, I will have to think of a way to let the user change deditated wam for the server.
		// if they have 32-bit java they can't use more than 1GB (i think)
		
		panel.open();
	}
	
    public static String getJavaDir() {
        final String separator = System.getProperty("file.separator");
        final String path = System.getProperty("java.home") + separator + "bin" + separator;

        if((System.getProperty("os.name").toLowerCase().contains("windows") || System.getProperty("os.name").toLowerCase().contains("win")) && new File(path + "javaw.exe").isFile())
            return path + "javaw.exe";

        return path + "java";
    }
}
