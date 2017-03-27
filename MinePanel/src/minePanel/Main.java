package minePanel;

import java.io.File;

import org.eclipse.swt.widgets.Display;

/**
 * while this class is not strictly necessary due to the main() in ServerPanel, I am too lazy to fix it so it will remain.
 * @author will
 *
 */
public class Main {
	protected static ServerPanel _panel;
	protected static Display disp;
	
	// perform main app setup upon startup
	public Main() {
		disp = new Display();
		_panel = new ServerPanel();
		_panel.open();
	}

	public static void main(String[] args) {
		Display.setAppName("Minepanel");
		new Main(); // anonymous class since this will only be run once.
	}

	public static String getJavaDir() {
		final String separator = System.getProperty("file.separator");
		final String path = System.getProperty("java.home") + separator + "bin" + separator;

		if((System.getProperty("os.name").toLowerCase().contains("windows") || System.getProperty("os.name").toLowerCase().contains("win")) && new File(path + "javaw.exe").isFile())
			return path + "javaw.exe";

		return path + "java";
	}
}
