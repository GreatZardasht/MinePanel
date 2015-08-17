package minePanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ErrorHandler {
	
	public static void displayError(Throwable e) {
		displayError(e.getMessage());
		e.printStackTrace();
	}
	
	public static void displayError(String message, String title, Throwable e) {
		displayError(title, message);
		e.printStackTrace();
	}
	
	public static void displayError(String message, Throwable e) {
		displayError(message);
		e.printStackTrace();
	}
	
	public static void displayError(String error) {
		displayError("An error occurred!", error);
	}
	
	public static void displayError(String title, String error) {
		Shell shell = null;
		Display display = Display.getCurrent();
		if (display != null) {
	        Shell[] shells = display.getShells();
	        if (shells.length >= 1) shell = shells[0];
	        else shell = new Shell(display);
		} else {
			display = new Display();
			shell = new Shell(display);
		}
		
		MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR);
		msg.setText(title);
		msg.setMessage(error);
		msg.open();
		
		shell.open();
		while (!shell.isDisposed()) {
		    if (display.readAndDispatch()) {
		        display.sleep();
		    }
		}
		display.dispose();
	}
}
