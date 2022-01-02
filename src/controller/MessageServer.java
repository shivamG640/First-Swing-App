package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.Message;

public class MessageServer implements Iterable<Message> {

	private Map<Integer, List<Message>> messages;
	private List<Message> selected;
	
	public MessageServer() {
		selected = new ArrayList<Message>();
		messages = new TreeMap<Integer, List<Message>>();
		
		List<Message> list= new ArrayList<Message>();
		list.add(new Message("title1 from 0th server", "Message1 from 0th server"));
		list.add(new Message("title2 from 0th server", "Message2 from 0th server"));
		
		messages.put(0, list);
		
		list= new ArrayList<Message>();
		list.add(new Message("title1 from 1st server", "Message1 from 1st server"));
		list.add(new Message("title2 from 1st server", "Message2 from 1th server"));
		
		messages.put(1, list);
		
		list= new ArrayList<Message>();
		list.add(new Message("title1 from 2nd server", "Message1 from 2nd server"));
		messages.put(2, list);
		
		list= new ArrayList<Message>();
		list.add(new Message("title1 from 3rd server", "Message1 from 3rd server"));
		list.add(new Message("title2 from 3rd server", "Message2 from 3rd server"));
		list.add(new Message("title3 from 3rd server", "Message3 from 3rd server"));
		
		messages.put(3, list);
		
		list= new ArrayList<Message>();
		list.add(new Message("title1 from 4th server", "Message1 from 4th server"));
		list.add(new Message("title2 from 4th server", "Message2 from 4th server"));
		
		messages.put(4, list);	
		
	}
	
	public void setSelectedServers(Set<Integer> servers) {
		
		selected.clear();
		for(Integer id:servers) {
			if(messages.containsKey(id)) {
				selected.addAll(messages.get(id));
			}
		}
		
	}
	
	public int getMessageCount() {
		return selected.size();
	}

	@Override
	public Iterator<Message> iterator() {
		// TODO Auto-generated method stub
		return new MessageIterator(selected);
	}
}

class MessageIterator implements Iterator{
	
	private Iterator<Message> iterator;
	
	public MessageIterator(List<Message> messages) {
		iterator = messages.iterator();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return iterator.hasNext();
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			
		}
		return iterator.next();
	}
	
}