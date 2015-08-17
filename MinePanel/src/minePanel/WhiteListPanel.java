package minePanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;

public class WhiteListPanel {

	public Shell shl;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		shl = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.APPLICATION_MODAL);
		shl.setSize(512, 355);
		shl.setText("White List");
		
		try {
			//File whiteList = new File("whitelist.json")
			//if ()
		} catch (Exception e) {
			MessageBox msg = new MessageBox(shl, SWT.ICON_ERROR);
			msg.setText("An error occurred!");
			msg.setMessage("White list file was not found. Start the server to create a new one.");
			msg.open();
			shl.close();
			return;
		}
		
		Display display = Display.getDefault();
		createContents();
		shl.open();
		shl.layout();
		while (!shl.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents() {
		Label lblOnlyThePlayers = new Label(shl, SWT.NONE);
		lblOnlyThePlayers.setBounds(10, 10, 473, 30);
		lblOnlyThePlayers.setText("Only the players in this list can join the game.\r\nAdd each allowed username on its own line, and they will be able to join!");
	}
}
