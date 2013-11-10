package elevator;
import eventbarrier.AbstractEventBarrier;

/*
 * One per elevator and floor and direction
 */
public class DoorEventBarrier extends AbstractEventBarrier{
	private int riders;
	protected boolean doorOpen;
	protected Dir dir;
	
//	protected final Object myElevator;
	protected final Elevator myElevator;
	protected final int myFloor;
	
	public DoorEventBarrier(Elevator e, int floor){
		riders=0;
		dir=Dir.X;
		doorOpen=false;
		myElevator=e;
		myFloor=floor;
	}
	
	@Override
	public synchronized void arrive() {
		riders++;
		System.out.println("#D: arrived");
		synchronized(myElevator){
			myElevator.RequestFloor(myFloor);
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
	public synchronized void arrive(Dir d){
		riders++;
		System.out.println("#D: arrivedD");
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
			while (myElevator.hasFloors()){
				System.out.println("#D: elev wait");
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
//		System.out.println("R#"+riders);
		while (riders>0){
			try {
				this.wait(); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		System.out.println("return raise inner");
		doorOpen=false;
	}

	@Override
	public synchronized void complete() {
		riders--;
		if (riders==0){ //all riders have attempted to enter/exit elevator, regardless of success
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
	public void setDir(Dir dir){
		this.dir=dir;
	}
	
	public Dir getDir(){
		return dir;
	}
	
	@Override
	public int waiters() {
		return riders;
	}

}
