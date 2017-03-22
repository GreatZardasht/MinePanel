package minePanel;

import java.io.File;

import org.eclipse.swt.widgets.Display;

/**
 * while this class is not strictly necessary due to the main() in ServerPanel, I am too lazy to fix it so it will remain.
 * @author will
 *
 */
public class Main {
	public static ServerPanel _panel;

	// perform main app setup upon startup
	public Main() {
		_panel = new ServerPanel();
		_panel.open();
	}

	public static void main(String[] args) {
		Display _display = Display.getDefault();
		Display.setAppName("Minepanel");
		new Main(); // anonymous class since this will only be run once.
		_display.dispose();
	}

	public static String getJavaDir() {
		final String separator = System.getProperty("file.separator");
		final String path = System.getProperty("java.home") + separator + "bin" + separator;

		if((System.getProperty("os.name").toLowerCase().contains("windows") || System.getProperty("os.name").toLowerCase().contains("win")) && new File(path + "javaw.exe").isFile())
			return path + "javaw.exe";

		return path + "java";
	}
}
