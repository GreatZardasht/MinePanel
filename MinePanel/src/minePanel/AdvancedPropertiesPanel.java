package minePanel;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Link;
import org.eclipse.wb.swt.SWTResourceManager;

public class AdvancedPropertiesPanel {
	
	public Shell shl;
	private Text propertyName;
	private Text propertyValue;
	
	public void open() {
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
	
	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shl = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.APPLICATION_MODAL);
		shl.setSize(268, 231);
		shl.setText("Custom Property");
		
		Label lblThisIsWhere = new Label(shl, SWT.NONE);
		lblThisIsWhere.setBounds(10, 10, 242, 15);
		lblThisIsWhere.setText("Enter a custom property name and value.");
		
		propertyName = new Text(shl, SWT.BORDER);
		propertyName.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		propertyName.setBounds(10, 60, 242, 21);
		
		Label lblName = new Label(shl, SWT.NONE);
		lblName.setBounds(10, 39, 55, 15);
		lblName.setText("Name");
		
		propertyValue = new Text(shl, SWT.BORDER);
		propertyValue.setFont(SWTResourceManager.getFont("Lucida Console", 9, SWT.NORMAL));
		propertyValue.setBounds(10, 108, 242, 21);
		
		Label lblValueoftentrue = new Label(shl, SWT.NONE);
		lblValueoftentrue.setBounds(10, 87, 242, 15);
		lblValueoftentrue.setText("Value (often \"true\" or \"false\")");
		
		Link link = new Link(shl, SWT.NONE);
		link.setBounds(10, 135, 197, 15);
		link.setText("<a>What properties can you use?</a>");
		
		Button btnSave = new Button(shl, SWT.NONE);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String pName = propertyName.getText().trim().toLowerCase();
				String pVal = propertyValue.getText().trim().toLowerCase();
				if (!pName.equals("") && !pVal.equals("")) {
					Properties properties = new Properties();
					
					try {
						properties.load(new FileInputStream("server.properties"));
					
						properties.setProperty(pName, pVal);
					
						// store properties
						properties.store(new FileOutputStream("server.properties"), "Minecraft server properties");
					} catch (Exception e2) {
						ErrorHandler.displayError(e2);
					}
				}
				
				// close the window, regardless of whether or not a property was changed.
				shl.close();
			}
		});
		btnSave.setBounds(138, 167, 114, 25);
		btnSave.setText("Save and close");
		link.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// open http://minecraft.gamepedia.com/Server_properties#Minecraft_server_properties
				try {
					ServerPanel.openWebpage(new URL("http://minecraft.gamepedia.com/Server_properties#Minecraft_server_properties"));
				} catch (MalformedURLException e1) {
					//Should not display, as openWebpage doesn't throw anything
					ErrorHandler.displayError("This should not have happened!", "The wiki url is invalid!");
				}
			}
		});
	}
}
