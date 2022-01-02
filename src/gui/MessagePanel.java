package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import controller.MessageServer;
import model.Message;

public class MessagePanel extends JPanel implements ProgressDialogListener {

	private JTree serverTree;
	private ServerTreeCellRenderer treeCellRendered;
	private ServerTreeCellEditor treeCellEditor;
	
	private Set<Integer> selectedServers;
	private MessageServer messageServer;
	
	private ProgressDialog progressDialog;
	private SwingWorker<List<Message>, Integer> worker;
	
	private TextPanel textPanel;
	private JList messageList;
	private JSplitPane upperPane;
	private JSplitPane lowerPane;
	
	private DefaultListModel messageListModel; 
	
	public MessagePanel(JFrame parent) {
		
		messageListModel = new DefaultListModel();
		
		selectedServers = new TreeSet<Integer>();
		selectedServers.add(0);
		selectedServers.add(1);
		selectedServers.add(4);
		
		messageServer =  new MessageServer();
		
		serverTree = new JTree(createTree());
		treeCellRendered = new ServerTreeCellRenderer();
		treeCellEditor = new ServerTreeCellEditor();
		
		progressDialog = new ProgressDialog(parent, "Message Downloading...");
		progressDialog.setListener(this);
		
		serverTree.setCellRenderer(treeCellRendered);
		serverTree.setCellEditor(treeCellEditor);
		
		serverTree.setEditable(true);
		
		serverTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		messageServer.setSelectedServers(selectedServers);
		
		treeCellEditor.addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent e) {
				
				ServerInfo info = (ServerInfo) treeCellEditor.getCellEditorValue();
				System.out.println(info + ": " + info.getId() + ": " + info.isChecked());
				
				if(info.isChecked()) {
					selectedServers.add(info.getId());
				} else {
					selectedServers.remove(info.getId());
				}
				
				messageServer.setSelectedServers(selectedServers);
				
				retrieveMessages();
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		setLayout(new BorderLayout());
		
		textPanel = new TextPanel();
		messageList = new JList(messageListModel);
		messageList.setCellRenderer(new MessageListRenderer());
		
		messageList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				Message message = (Message) messageList.getSelectedValue();
				textPanel.setText(message.getContent());
			}
		});
		
		lowerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(messageList), textPanel);
		upperPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(serverTree), lowerPane);
		
		// lowerPane.setDividerLocation(100);
		textPanel.setMinimumSize(new Dimension(10, 100));
		messageList.setMinimumSize(new Dimension(10, 100));
		
		upperPane.setResizeWeight(0.5);
		lowerPane.setResizeWeight(0.5);
		
		add(upperPane, BorderLayout.CENTER);
		
	}
	
	private void retrieveMessages() {
		progressDialog.setMaximum(messageServer.getMessageCount());
		
		progressDialog.setVisible(true);
		
		
		worker = new SwingWorker<List<Message>, Integer>() {

			@Override
			protected List<Message> doInBackground() throws Exception {

				List<Message> retrieveMessages = new ArrayList<Message>();
				int count =0;
				
				for (Iterator<Message> itr = messageServer.iterator(); itr.hasNext();) {
					
					if(isCancelled()) break;
					
					Message message = itr.next();
					System.out.println(message.getTitle());
					
					retrieveMessages.add(message);
					count++;
					publish(count);
				}
				
				/*for(Message message: messageServer ) {
					System.out.println(message.getTitle());
				}*/
				
				return retrieveMessages;
			}

			@Override
			protected void process(List<Integer> counts) {
				// TODO Auto-generated method stub
				int retrieved = counts.get(counts.size() - 1);
				progressDialog.setValue(retrieved);
			}

			@Override
			protected void done() {
				// TODO Auto-generated method stub
				
				progressDialog.setVisible(false);
				
				if(isCancelled()) return;
				
				try {
					List<Message> retrieveMessages = get();
					System.out.println("Retrieved " + retrieveMessages.size() + " messages." );
					
					messageListModel.removeAllElements();
					for (Message message : retrieveMessages) {
						messageListModel.addElement(message);
					}
					
					messageList.setSelectedIndex(0);
					
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
			
		};
		
		worker.execute();
		
	}
	
	private DefaultMutableTreeNode createTree() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Servers");
		
		DefaultMutableTreeNode branch1 = new DefaultMutableTreeNode("USA");
		DefaultMutableTreeNode server1 = new DefaultMutableTreeNode(new ServerInfo("NY", 0, selectedServers.contains(0)));
		DefaultMutableTreeNode server2 = new DefaultMutableTreeNode(new ServerInfo("Bostom", 1, selectedServers.contains(1)));
		DefaultMutableTreeNode server3 = new DefaultMutableTreeNode(new ServerInfo("LA", 2, selectedServers.contains(2)));
		
		DefaultMutableTreeNode branch2 = new DefaultMutableTreeNode("UK");
		DefaultMutableTreeNode server4 = new DefaultMutableTreeNode(new ServerInfo("Paris", 3, selectedServers.contains(3)));
		DefaultMutableTreeNode server5 = new DefaultMutableTreeNode(new ServerInfo("London", 4, selectedServers.contains(4)));
		
		branch1.add(server1);
		branch1.add(server2);
		branch1.add(server3);
		
		branch2.add(server4);
		branch2.add(server5);
		
		top.add(branch1);
		top.add(branch2);
		
		return top;
	}

	@Override
	public void progressDialogCancelled() {

		if(worker != null) {
			worker.cancel(true);
		}
	}

	public void refresh() {
		retrieveMessages();
	}

}
