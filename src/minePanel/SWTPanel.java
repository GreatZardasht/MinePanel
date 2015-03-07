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

public class SWTPanel {

	ServerRunner runner;
	
	Shell shlMinepanel;
	private Text entryBox;
	public StyledText consoleBox;
	public Button startButton;
	public Button stopButton;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SWTPanel window = new SWTPanel();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
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
		shlMinepanel = new Shell();
		shlMinepanel.setSize(790, 427);
		shlMinepanel.setText("MinePanel");
		
		consoleBox = new StyledText(shlMinepanel, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		consoleBox.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		consoleBox.setDoubleClickEnabled(false);
		consoleBox.setEditable(false);
		consoleBox.setBounds(156, 10, 608, 346);
		
		entryBox = new Text(shlMinepanel, SWT.BORDER);
		entryBox.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		entryBox.setBounds(156, 362, 608, 21);
		
		startButton = new Button(shlMinepanel, SWT.NONE);
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runner = new ServerRunner("server.jar", 3096, Main.MY_JAVA_LOC, true, consoleBox, startButton, stopButton);
			}
		});
		startButton.setBounds(10, 10, 140, 25);
		startButton.setText("Start Server");
		
		stopButton = new Button(shlMinepanel, SWT.NONE);
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		stopButton.setBounds(10, 41, 140, 25);
		stopButton.setText("Stop Server");

	}
}
