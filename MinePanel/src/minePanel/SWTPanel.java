package minePanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
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

public class SWTPanel {

	ServerRunner runner;
	
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
			SWTPanel window = new SWTPanel();
			window.open();
		} catch (Exception e) {
			//Same as ServerPanel, there shouldn't be any exception thrown here.
			ErrorHandler.displayError("Wow.", "Could not even show the main window, you might be having some big trouble", e);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlMinepanel.open();
		shlMinepanel.layout();
		while (!shlMinepanel.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
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
		
		
		consoleBox = new StyledText(shlMinepanel, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		consoleBox.setWrapIndent(40);
		consoleBox.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		consoleBox.setDoubleClickEnabled(false);
		consoleBox.setEditable(false);
		consoleBox.setBounds(156, 10, 608, 346);
		consoleBox.setAlwaysShowScrollBars(true);
		
		entryBox = new Text(shlMinepanel, SWT.BORDER);
		entryBox.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		entryBox.setBounds(156, 362, 608, 21);
		
		startButton = new Button(shlMinepanel, SWT.NONE);
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				runner = new ServerRunner("server.jar", 3096, Main.MY_JAVA_LOC, true, true, consoleBox, entryBox, startButton, stopButton, clearButton, shlMinepanel, propertiesButton);
				runner.startServer();
			}
		});
		startButton.setBounds(10, 10, 140, 25);
		startButton.setText("Start Server");
		
		stopButton = new Button(shlMinepanel, SWT.NONE);
		stopButton.setBounds(10, 41, 140, 25);
		stopButton.setText("Stop Server");
		stopButton.setEnabled(false);
		
		clearButton = new Button(shlMinepanel, SWT.NONE);
		clearButton.setBounds(10, 72, 140, 21);
		clearButton.setText("Clear Console");
		
		propertiesButton = new Button(shlMinepanel, SWT.NONE);
		propertiesButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// open the properties panel
				PropertiesPanel ppanel = new PropertiesPanel();
				ppanel.open();
			}
		});
		propertiesButton.setBounds(10, 136, 140, 25);
		propertiesButton.setText("Server Properties");

	}
}
