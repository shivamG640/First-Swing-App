package gui;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class Toolbar extends JToolBar implements ActionListener{

	private JButton saveBtn;	
	private JButton refreshBtn;
	private ToolbarListener textListener;
	
	public Toolbar() {
		setBorder(BorderFactory.createEtchedBorder());
		// setFloatable(false);
		
		saveBtn = new JButton();
		saveBtn.setIcon(createIcon("/images/Save16.gif"));
		saveBtn.setToolTipText("Save");
		
		refreshBtn = new JButton();
		refreshBtn.setIcon(createIcon("/images/Refresh16.gif"));
		refreshBtn.setToolTipText("Refresh");
		
		saveBtn.addActionListener(this);
		refreshBtn.addActionListener(this);
		
		//setLayout(new FlowLayout(FlowLayout.LEFT));
		
		add(saveBtn);
		// addSeparator();
		add(refreshBtn);
	}
	
	private ImageIcon createIcon(String path) {
		URL url = getClass().getResource(path);
		
		if (url == null) {
			System.err.println("Unable to load image" + path);
		}
		
		ImageIcon icon = new ImageIcon(url);
		
		return icon;
	}

	public void setToolbarListener(ToolbarListener textListener) {
		this.textListener = textListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton)e.getSource();
		
		if(clicked == saveBtn) {
			if (textListener != null) {
				textListener.saveEventOccured();
			}
		} else if(clicked == refreshBtn) {
			if (textListener != null) {
				textListener.refreshEventOccured();
			}
		}
		
	}
}
