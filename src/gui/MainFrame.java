package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.sun.glass.events.KeyEvent;

import controller.Controller;

public class MainFrame extends JFrame {

	private Toolbar toolbar;
	private FormPanel formPanel;
	private JFileChooser fileChooser;
	private Controller controller;
	private TablePanel tablePanel;
	private PrefsDialog prefsDialog;
	private Preferences prefs;	
	private JSplitPane splitPane;
	private JTabbedPane tabPane;
	private MessagePanel messagePanel;
	
	public MainFrame() {
		super("Hello World");
		setLayout(new BorderLayout());

		toolbar = new Toolbar();
		formPanel = new FormPanel();
		
		prefs = Preferences.userRoot().node("db");

		controller = new Controller();
		
		prefsDialog = new PrefsDialog(this);

		tabPane = new JTabbedPane();
		messagePanel = new MessagePanel(this);
		
		tablePanel = new TablePanel();
		tablePanel.setData(controller.getPeople());
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tabPane);
		splitPane.setOneTouchExpandable(true);
		
		tabPane.addTab("Person DB", tablePanel);
		tabPane.addTab("Messages", messagePanel);
		
		tabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {

				int tabIndex = tabPane.getSelectedIndex();
				
				if(tabIndex == 1) {
					messagePanel.refresh();
				}
			}
		});
		
		tablePanel.setPersonTableListener(new PersonTableListener() {
			public void rowDeleted(int row) {
				controller.removePerson(row);
			}
		});
		
		prefsDialog.setPrefsListener(new PrefsListener() {
			public void preferencesSet(String user, String password, int port) {

				prefs.put("user", user);
				prefs.put("password", password);
				prefs.putInt("port", port);
			}
			
		});
		
		String user = prefs.get("user", "");
		String password = prefs.get("password", "");
		Integer port = prefs.getInt("port", 3306);
		prefsDialog.setDefaults(user, password, port);

		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {

				if (f.isDirectory()) {
					return true;
				}

				String name = f.getName();
				String extension = Utils.getFileExtension(name);

				if (extension == null) {
					return false;
				}

				if (extension.equals("per")) {
					return true;
				}

				return false;
			}

			@Override
			public String getDescription() {
				return "Person database files (*.per)";
			}

		});

		setJMenuBar(createMenuBar());

		toolbar.setToolbarListener(new ToolbarListener() {

			@Override
			public void saveEventOccured() {
				// TODO Auto-generated method stub
				connect();
				
				try {
					controller.save();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(MainFrame.this, "Unable to save to DB", "DB connection problem", JOptionPane.ERROR_MESSAGE);
				}
			}

			@Override
			public void refreshEventOccured() {
				// TODO Auto-generated method stub
				refresh();
			}

		});

		formPanel.setFormListener(new FormListener() {
			@Override
			public void formEventOccured(FormEvent e) {
				/*
				 * String name = e.getName(); String occupation = e.getOccupation(); int ageCat
				 * = e.getAgeCategory(); String empCat = e.getEmploymentCategory();
				 * 
				 * textPanel.appendText(name + ": " + occupation + ": " + ageCat + ": " + empCat
				 * + "\n");
				 * 
				 * System.out.println(e.getGender());
				 */

				controller.addPerson(e);
				tablePanel.refresh();
			}
		});
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				controller.disconnect();
				dispose();
				System.gc();
			}
			
		});

		// add(formPanel, BorderLayout.WEST);
		// add(textPanel, BorderLayout.CENTER);
		add(splitPane, BorderLayout.CENTER);

		add(toolbar, BorderLayout.PAGE_START);
		
		refresh();

		setMinimumSize(new Dimension(500, 400));
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	protected void refresh() {
		// TODO Auto-generated method stub
		
		connect();
		
		try {
			controller.load();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(MainFrame.this, "Unable to load from DB", "DB connection problem", JOptionPane.ERROR_MESSAGE);
		}
		
		tablePanel.refresh();
		
	}

	public void connect() {
		try {
			controller.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(MainFrame.this, "Cannot connect to DB", "DB connection problem", JOptionPane.ERROR_MESSAGE);
		}
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem exportDataItem = new JMenuItem("Export Data...");
		JMenuItem importDataItem = new JMenuItem("Import Data...");
		JMenuItem exitItem = new JMenuItem("Exit");

		fileMenu.add(exportDataItem);
		fileMenu.add(importDataItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		JMenu windowMenu = new JMenu("Window");
		JMenu showMenu = new JMenu("Show");
		JMenuItem prefsItem = new JMenuItem("Preferences...");
		JCheckBoxMenuItem showFormItem = new JCheckBoxMenuItem("Person Form");
		showFormItem.setSelected(true);

		showMenu.add(showFormItem);
		windowMenu.add(showMenu);
		windowMenu.add(prefsItem);

		menuBar.add(fileMenu);
		menuBar.add(windowMenu);

		showFormItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
				
				if (menuItem.isSelected()) {
					splitPane.setDividerLocation((int) formPanel.getMinimumSize().getWidth());
				}
					
				formPanel.setVisible(menuItem.isSelected());
			}
		});
		
		prefsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				prefsDialog.setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int action = JOptionPane.showConfirmDialog(MainFrame.this, "Exit Application", "Confirm Exit",
						JOptionPane.OK_CANCEL_OPTION);

				if (action == JOptionPane.OK_OPTION) {
					WindowListener[] listeners = getWindowListeners();
					for (WindowListener listener : listeners) {
						listener.windowClosing(new WindowEvent(MainFrame.this, 0));
					}
				}
			}
		});

		importDataItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					// System.out.println(fileChooser.getSelectedFile());
					try {
						controller.loadFromFile(fileChooser.getSelectedFile());
						tablePanel.refresh();
					} catch (IOException e1) {
						// e1.printStackTrace();
						JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from the file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		exportDataItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					// System.out.println(fileChooser.getSelectedFile());
					try {
						controller.saveToFile(fileChooser.getSelectedFile());
					} catch (IOException e1) {
						// e1.printStackTrace();
						JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from the file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		fileMenu.setMnemonic(KeyEvent.VK_F);
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		importDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));

		return menuBar;
	}

}
