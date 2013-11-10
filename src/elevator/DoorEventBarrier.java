package elevator;
import eventbarrier.AbstractEventBarrier;

/*
 * One per elevator and floor and direction
 */
public class DoorEventBarrier extends AbstractEventBarrier{
	
	private boolean DEBUG=false;
	
	private int riders;
	protected boolean doorOpen;

	//	protected final Object myElevator;
	protected final Elevator myElevator;
	protected final int myFloor;

	public DoorEventBarrier(Elevator e, int floor){
		riders=0;
		doorOpen=false;
		myElevator=e;
		myFloor=floor;
	}

	@Override
	public synchronized void arrive() {
		arrive(Dir.X);
	}

	public synchronized void arrive(Dir d){
		riders++;
		synchronized(myElevator){
			myElevator.RequestFloor(myFloor,d);
			myElevator.notifyAll(); //wake up idle elevator, if necessary
		}
		while(!doorOpen){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called by idle elevators
	 */
	public void arriveElev(){
		synchronized(myElevator){
			while (myElevator.hasNoFloors()){
				printDebug("#D: elev wait");
				try {
					myElevator.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public synchronized void raise() {
		assert(doorOpen==false);
		doorOpen=true;
		this.notifyAll();
		printDebug("R#"+riders);
		while (riders>0 && doorOpen){
			try {
				this.wait(); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		doorOpen=false;
	}

	@Override
	public synchronized void complete() {
		riders--;
		if (riders==0){ //all riders have attempted to enter/exit elevator, regardless of success
			doorOpen=false;
			this.notifyAll();
		}
		while(riders!=0){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public int waiters() {
		return riders;
	}

	private void printDebug(String s){
		if (DEBUG){
		System.out.println(s);
		}
	}
	

}
