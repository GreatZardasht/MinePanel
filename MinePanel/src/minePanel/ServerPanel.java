package minePanel;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Link;

public class ServerPanel {

	public static ServerRunner runner;

	public Shell shlMinepanel;
	public Text entryBox;
	public StyledText consoleBox;
	public Button startButton;
	public Button stopButton;
	public Button clearButton;
	public Button propertiesButton;

	/**
	 * Launch the application.
	 * @param args Same old args you always use lel
	 */
	public static void main(String[] args) {
		try {
			ServerPanel window = new ServerPanel();
			window.open();
		} catch (Exception e) {
			//e.printStackTrace();
			//I don't think any exception might be thrown here but why not.
			ErrorHandler.displayError("Wow.", "Could not even show the main window, you might be having some big trouble", e);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		shlMinepanel.open();
		shlMinepanel.layout();
		while (!shlMinepanel.isDisposed()) {
			if (!Display.getDefault().readAndDispatch()) {
				Display.getDefault().sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		// disables the maximize button and resizing
		shlMinepanel = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlMinepanel.setSize(790, 427);
		shlMinepanel.setText("MinePanel");


		consoleBox = new StyledText(shlMinepanel, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		consoleBox.setWrapIndent(40);
		consoleBox.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		consoleBox.setDoubleClickEnabled(false);
		consoleBox.setEditable(false);
		consoleBox.setBounds(167, 10, 597, 346);
		consoleBox.setAlwaysShowScrollBars(true);
		// when text is added to it, always scroll to the bottom
		consoleBox.addListener(SWT.Modify, new Listener(){
			public void handleEvent(Event e){
				consoleBox.setTopIndex(consoleBox.getLineCount() - 1);
			}
		});

		entryBox = new Text(shlMinepanel, SWT.BORDER);
		entryBox.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		entryBox.setBounds(167, 362, 597, 21);

		startButton = new Button(shlMinepanel, SWT.NONE);
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// first read the java settings file, assuming there is one. Otherwise, assume max of 1024MB memory and no 64-bit
				int mem = 1024;
				boolean d64 = false;
				
				try {
					Properties props = new Properties();
					File propertiesFile = new File("javasettings.properties");
					if (propertiesFile.exists()) {
						props.load(new FileInputStream(propertiesFile));
						if (props.getProperty("memory") != null) {
							try {
								mem = Integer.parseInt(props.getProperty("memory"));
							} catch (NumberFormatException e2) {
								mem = 1024;
							}
						}
						
						if (props.getProperty("bitness") != null) {
							if (props.getProperty("bitness").equals("64")) {
								d64 = true;
							} else {
								d64 = false;
							}
						}
					}
				} catch (Exception e1) {
					ErrorHandler.displayError("Error while reading or writing the Java settings file. Make sure you have permission to read and write in the server directory.", e1);
					return;
				}
				
				runner = new ServerRunner("server.jar", mem, Main.getJavaDir(), d64, true, consoleBox, entryBox, startButton, stopButton, clearButton, shlMinepanel, propertiesButton);
				runner.startServer();
			}
		});
		startButton.setBounds(10, 10, 151, 25);
		startButton.setText("Start Server");

		stopButton = new Button(shlMinepanel, SWT.NONE);
		stopButton.setBounds(10, 41, 151, 25);
		stopButton.setText("Stop Server");
		stopButton.setEnabled(false);

		clearButton = new Button(shlMinepanel, SWT.NONE);
		clearButton.setBounds(10, 72, 151, 21);
		clearButton.setText("Clear Console");

		propertiesButton = new Button(shlMinepanel, SWT.NONE);
		propertiesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// open the properties panel
				new PropertiesPanel().open();
			}
		});
		propertiesButton.setBounds(10, 136, 151, 25);
		propertiesButton.setText("Server Properties...");

		Link commandsLink = new Link(shlMinepanel, SWT.NONE);
		commandsLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					openWebpage(new URL("https://github.com/WillEccles/MinePanel/wiki/Custom-Commands"));
				} catch (Exception e2) {
					//This should not happen, openWebpage does not throw anything
					ErrorHandler.displayError("Could not open the wiki page.", e2);
				}
			}
		});
		commandsLink.setBounds(20, 198, 130, 15);
		commandsLink.setText("<a>Custom commands?</a>");

		// this button is here to tell the user how to add themselves to the white list
		Button btnWL = new Button(shlMinepanel, SWT.NONE);
		btnWL.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				MessageBox msg = new MessageBox(shlMinepanel, SWT.ICON_INFORMATION);
				msg.setText("How to edit the whitelist:");
				msg.setMessage("To add yourself to the white list, use the \"whitelist add [name]\" command. Use \"whitelist remove [name]\" to remove someone. If you can't join after adding yourself, try \"whitelist reload\".");
				msg.open();
			}
		});
		btnWL.setBounds(10, 167, 151, 25);
		btnWL.setText("White List");

	}

	// the next two methods have to do with opening URLs.
	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				ErrorHandler.displayError("Opening the following uri :" + uri + " failed.", e);
			}
		}
	}
	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			ErrorHandler.displayError("This should not have happened!", "The url passed had an invalid format ! (" + url + ")");
		}
	}
}
