package minePanel;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;

import javax.swing.JTextArea;

import java.awt.Font;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class Panel extends JFrame {
	public JTextField consoleInputBox;
	public JTextArea console;
	public JButton startButton;
	public JButton stopButton;
	
	ServerRunner runner;
	
	public Panel() {
		setTitle("MinePanel");
		setSize(750, 484); // For whatever reason this wasn't added automagically
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem quitButton = new JMenuItem("Quit");
		quitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(quitButton);
		
		JButton startButton = new JButton("Run Server");
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				/// Run the server - this is where to set all the arguments when running it.
				runner = new ServerRunner("server.jar", 3096, Main.MY_JAVA_LOC, true, console, startButton, stopButton);
				runner.startServer();
			}
		});
		startButton.setBounds(10, 11, 89, 23);
		getContentPane().setLayout(null);
		getContentPane().add(startButton);
		
		JTextArea console = new JTextArea();
		console.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		console.setBounds(109, 0, 625, 370);
		console.setLineWrap(true);
		console.setFont(new Font("Monospaced", Font.PLAIN, 13));
		console.setEditable(false);
		getContentPane().add(console);
		
		JButton stopButton = new JButton("Stop Server");
		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				runner.stopServer();
			}
		});
		stopButton.setForeground(Color.RED);
		stopButton.setEnabled(false);
		stopButton.setBounds(10, 45, 89, 23);
		getContentPane().add(stopButton);
		
		consoleInputBox = new JTextField();
		consoleInputBox.setFont(new Font("Monospaced", consoleInputBox.getFont().getStyle(), 13));
		consoleInputBox.setBounds(109, 381, 625, 20);
		getContentPane().add(consoleInputBox);
		consoleInputBox.setColumns(10);
	}
}
