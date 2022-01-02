
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gui.MainFrame;

public class App {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				new MainFrame();
				
			}
			
		}); 
		
		
	}

}
