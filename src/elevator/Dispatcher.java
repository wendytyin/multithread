package elevator;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 
 * @author Wendy
 *	Stores parsed information for rider threads 
 *
 */
public class Dispatcher{
	private AbstractBuilding bldg;
	private final int total;
	private boolean kill=false;
	private int waiters;
	private HashSet<Integer> active; //riderIDs being used
	private HashMap<Integer,LinkedList<Integer[]>> buffer;
	private final Integer[] killer={-1,0,0};

	public Dispatcher(int riders){
		total=riders;
		waiters=0; 
		active=new HashSet<Integer>();
		buffer=new HashMap<Integer,LinkedList<Integer[]>>();
		makeRiders();
	}


	private void makeRiders() {
		for (int i=0;i<total;i++){
			Rider r=new Rider(0, 0, 0, bldg);
			r.setDispatcher(this);
			new Thread(r).start();
		}
	}

	/*
	 * Called by thread when thread ready to take on a new identity
	 */
	public synchronized Integer[] getRider(int riderId) throws InterruptedException {
		active.remove(riderId);
		waiters++;
		while (buffer.isEmpty() && !kill){
			wait();
		}
		if (buffer.isEmpty() && kill){
			if (active.isEmpty()){ notifyAll(); } //wake up the dispatcher to return from quit
			return killer; //stop rider thread
		}
		Set<Integer> keys=buffer.keySet();
		keys.removeAll(active);
		while (keys.isEmpty()){
			wait();
			keys=buffer.keySet();
			keys.removeAll(active);
		}
		waiters--;
		if (buffer.containsKey(riderId)){
			LinkedList<Integer[]> value=buffer.get(riderId);
			Integer[] next=value.poll();
			if (value.isEmpty()){ buffer.remove(riderId);}
			active.add(riderId);
			return next;
		}
		//select something random from the nonactive values
		Integer[] keysArr=keys.toArray(new Integer[1]);
		riderId=keysArr[0];
		active.add(riderId);
		
		LinkedList<Integer[]> value=buffer.get(riderId);
		Integer[] next=value.poll();
		if (value.isEmpty()){ buffer.remove(riderId);}
		
		return next;
	}

	public synchronized void addRider(Integer[] riderId){
		assert(!kill);
		LinkedList<Integer[]>tmp;
		if (buffer.containsKey(riderId[0])){
			tmp=buffer.get(riderId[0]);
			tmp.add(riderId);
			buffer.put(riderId[0], tmp);
		}
		else {
			tmp=new LinkedList<Integer[]>();
			tmp.add(riderId);
		}
		buffer.put(riderId[0], tmp);

		if (waiters>0){
			notifyAll();
		}
	}
	
	/*
	 * returns when all buffered riders have finished and stopped running their threads
	 */
	public synchronized void quit() throws InterruptedException{
		kill=true;
		notifyAll();
		while (!active.isEmpty()){
			wait();
		}
	}

}
