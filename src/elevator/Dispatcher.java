package elevator;

import java.util.HashMap;
import java.util.HashSet;

import eventbarrier.AbstractEventBarrier;

public class Dispatcher{
	private int waiters;
	private HashSet<Integer> active;
	private HashMap<Integer, Integer[]> buffer;
	
	public Dispatcher(){
		waiters=0;
		active=new HashSet<Integer>();
		buffer=new HashMap<Integer, Integer[]>();
	}

	public synchronized void arrive() throws InterruptedException {
		waiters++;
		while (buffer.isEmpty()){
			wait();
		}
	}
	public synchronized void addRider(int riderId, Integer[] floors){
		buffer.put(riderId, floors);
		notify();
	}
	
	public synchronized Integer[] getAttrib(){
		Integer[] tmp=new Integer[3];
		return tmp;
		
	}

	public synchronized void complete(int riderId) {
		active.remove(riderId);
		if (buffer.containsKey(riderId)){
			if (waiters>0){
				notify();
			}
		}
	}
	

}
