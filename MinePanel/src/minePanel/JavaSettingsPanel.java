package minePanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class JavaSettingsPanel {
	/**
	 * @wbp.parser.entryPoint
	 */
	public Shell shl;
	Properties props = new Properties();
	String bitness;
	String mem;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		shl = new Shell(Main.disp, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.APPLICATION_MODAL);
		shl.setSize(471, 207);
		shl.setText("Java Settings");
		
		try {
			File propertiesFile = new File("javasettings.properties");
			if (propertiesFile.exists()) {
				props.load(new FileInputStream(propertiesFile));
				bitness = props.getProperty("bitness");
				mem = props.getProperty("memory");
				if (bitness == null || !bitness.matches("^(32|64)$")) {
					bitness = "32";
					props.setProperty("bitness", bitness);
				}
				if (mem == null || !mem.matches("^[0-9]+$")) {
					mem = "1024";
					props.setProperty("mem", mem);
				}
				
				// make sure that the settings match
				if (Integer.parseInt(mem) > 1024 && bitness.equals("32")) {
					mem = "1024";
					props.setProperty("memory", mem);
				}
			} else {
				propertiesFile.createNewFile(); // create the file if it doesn't exist and set some basic properties
				props.load(new FileInputStream(propertiesFile));
				props.setProperty("bitness", "32");
				props.setProperty("memory", "1024");
				mem = "1024";
				bitness = "32";
			}
		} catch (Exception e) {
			ErrorHandler.displayError("Error while reading or writing the Java settings file. Make sure you have permission to read and write in the server directory.", e);
			return;
		}
		
		Button btn64bit = new Button(shl, SWT.CHECK);
		btn64bit.setToolTipText("If you have a 64-bit Java installation, check this box. It will allow you to use more RAM. If your Java is not 64-bit, leave this un-checked.");
		btn64bit.setBounds(10, 10, 182, 18);
		btn64bit.setText("64-bit JRE");
		
		Spinner memorySpinner = new Spinner(shl, SWT.BORDER);
		memorySpinner.setIncrement(1024);
		memorySpinner.setMaximum(1024);
		memorySpinner.setMinimum(512);
		memorySpinner.setBounds(10, 56, 110, 22);
		
		btn64bit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				if (btn64bit.getSelection()) {
					memorySpinner.setMaximum(32768); // limited to 32GB, since the most users will use is likely in the neighborhood of 8GB or so
				} else {
					memorySpinner.setMaximum(1024);
					if (memorySpinner.getSelection() > 1024)
						memorySpinner.setSelection(1024);
				}
			}
		});
		
		Label lblAllocatedRamin = new Label(shl, SWT.NONE);
		lblAllocatedRamin.setToolTipText("Choose how much RAM you want to allocate to the server in MB (Megabytes). For reference, 1024 is 1GB. If you have 32-bit Java, ");
		lblAllocatedRamin.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.BOLD));
		lblAllocatedRamin.setBounds(10, 34, 215, 27);
		lblAllocatedRamin.setText("Allocated RAM (in MB):");
		
		Button btnSave = new Button(shl, SWT.NONE);
		btnSave.setBounds(334, 147, 127, 28);
		btnSave.setText("Save and Close");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				props.setProperty("bitness", btn64bit.getSelection()?"64":"32");
				props.setProperty("memory", Integer.toString(memorySpinner.getSelection()));
				
				try {
					props.store(new FileOutputStream("javasettings.properties"), "Java Runtime Settings for Minepanel");
				} catch (FileNotFoundException e1) {
					ErrorHandler.displayError("Could not find javasettings.properties.", e1);
				} catch (IOException e1) {
					ErrorHandler.displayError("Could not open javasettings.properties for writing.", e1);
				}
				
				shl.close();
			}
		});
		
		Label lblNewLabel = new Label(shl, SWT.NONE);
		lblNewLabel.setToolTipText("");
		lblNewLabel.setBounds(10, 84, 451, 90);
		lblNewLabel.setText("Choose how much RAM to allocate for the server to use. Tips:\n- 1GB is 1024MB, so 2GB = 2048MB, etc.\n- If you have 32-bit Java, any more than 1024MB is not possible.\n- If you have 32-bit and try to check the 64-bit box, the server will not run.");
		
		// reflect the settings in the UI
		if (bitness.equals("32")) {
			btn64bit.setSelection(false);
		} else {
			btn64bit.setSelection(true);
			memorySpinner.setMaximum(32768);
		}
		memorySpinner.setSelection(Integer.parseInt(mem));
		
		shl.open();
		shl.layout();
		while (!shl.isDisposed()) {
			if (!Main.disp.readAndDispatch()) {
				Main.disp.sleep();
			}
		}
	}
}
