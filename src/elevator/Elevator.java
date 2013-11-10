package elevator;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

/* Notes to self:
 * Elevator cannot and must not close doors until all exiting riders have exited and as many entering as possible have entered
 * 
 * Extensions for badly behaved riders: 
 * 1. riders who call up or down, but do not wait (never try to invoke enter)
 * 2. riders who enter but do not request a floor (trap them, fuck them)
 * 3. riders who do not exit at their requested floor (at a different floor, or don't get off at all)
 */


public class Elevator extends AbstractElevator implements Runnable{

	private final boolean DEBUG=false;
	
	private final String elevatorStr;
	private int currFloor; 
	private int riders; //total number of riders in elevator
	private Dir currDir;
//	protected TreeSet<Floor> reqFloors; 
	protected ConcurrentSkipListSet<Floor> reqFloors;
	protected DoorEventBarrier doorIn;
	protected DoorEventBarrier doorOut;
	protected Building myBuilding;


	public Elevator(int numFloors, int elevatorId, int maxOccupancyThreshold, AbstractBuilding bldg) {
		super(numFloors, elevatorId, maxOccupancyThreshold);
		myBuilding=(Building) bldg;
		riders=0;
		currFloor=1;
		currDir=Dir.X;
//		reqFloors=new TreeSet<Floor>();
		reqFloors=new ConcurrentSkipListSet<Floor>();
		elevatorStr="E"+elevatorId;
	}


	@Override
	public void OpenDoors() {
		doorIn=myBuilding.getDoor(this, currFloor,true);
		doorOut=myBuilding.getDoor(this, currFloor,false);

		String tmp=String.format("on F%d opens\n", currFloor);
		printEvent(tmp);

		doorOut.raise();
		doorIn.raise(); //automatically close doors upon return
	}

	@Override
	public void ClosedDoors() {
		String tmp=String.format("on F%d closes\n", currFloor);
		printEvent(tmp);

	}

	/*
	 * Elevator attempts to minimize number of direction changes
	 */
	private Floor getNextFloor(){
		if (reqFloors.isEmpty()){
			printDebug("#E: empty");
			currDir=Dir.X;
			return null;
		}
		Floor i=new Floor(currFloor,currDir);
		if((currDir==Dir.UP) || (currDir==Dir.X)){
			currDir=Dir.UP;
			i=i.increment();
			i=reqFloors.ceiling(i);
			while ((i!=null)&&(i.dir==Dir.DOWN)){
				i=i.increment();
				i=reqFloors.ceiling(i);
			}
			if (i==null){
				i=reqFloors.last();
			}
		} 
		else {
			i=i.decrement();
			i=reqFloors.floor(i);
			while ((i!=null)&&(i.dir==Dir.UP)){
				i=i.decrement();
				i=reqFloors.floor(i);
			}
			if (i==null){
				i=reqFloors.first();
			}
		}
		printDebug("#E: nF"+i.floor+i.dir);
		return i;
	}

	//should really only be called within Elevator
	//TODO: SYNCHRONIZED?
	@Override
	protected void VisitFloor(Floor f) { 
		assert(f!=null);
		int floor=f.floor;
		assert(floor>0 && floor<=numFloors);
		if (floor<currFloor){ this.currDir=Dir.DOWN;}
		else { this.currDir=Dir.UP;}

		synchronized(this){
		String d=(currDir==Dir.UP)?"up":"down";
			while (currFloor!=floor){
				if (currDir==Dir.UP) currFloor++;
				else currFloor--;

				String tmp=String.format("moves %s to F%d\n", d, currFloor);
				printEvent(tmp);
			}
			
			//currFloor == (desired) floor
			printDebug("#E: removing "+f.floor+f.dir);
//			reqFloors.remove(f); //entering riders in this direction taken care of
			reqFloors.remove(new Floor(f.floor,Dir.UP)); //exiting riders taken care of
			reqFloors.remove(new Floor(f.floor,Dir.DOWN)); //exiting riders taken care of
			reqFloors.remove(new Floor(f.floor,Dir.X)); //exiting riders taken care of
			printDebug("#E: floors left "+reqFloors.size());
			}
		if (reqFloors.isEmpty()){
			currDir=Dir.X;
		}
		if (currFloor==1){
			currDir=Dir.UP;
		}
		if (currFloor==numFloors){
			currDir=Dir.DOWN;
		}
	}

	@Override
	public synchronized boolean Enter() {
		//		System.out.println("r:"+riders+"max:"+maxOccupancyThreshold);
		if (riders<maxOccupancyThreshold){
			riders++;
			return true;
		}
		return false;
	}

	@Override
	public synchronized void Exit() {
		riders--;
	}

	/**
	 * Accessed through DoorEventBarrier (possible synchronization issues)
	 */
	@Override
	public synchronized void RequestFloor(int floor) {
		assert(floor>0 && floor<=numFloors);
		if (!reqFloors.contains(new Floor(floor,Dir.DOWN)) && !reqFloors.contains(new Floor(floor,Dir.UP))){
			reqFloors.add(new Floor(floor,Dir.X));
			printDebug("#Es: floors visit: "+floor+Dir.X);
		}
	}

	public synchronized void RequestFloor(int floor,Dir d) {
		assert(floor>0 && floor<=numFloors);
		if (!reqFloors.contains(new Floor(floor,Dir.X))){
			reqFloors.add(new Floor(floor,d));
		}
		printDebug("#E: floors visit: "+floor+d);
	}

	public synchronized boolean hasNoFloors(){
		return reqFloors.isEmpty();
	}

	/*
	 * For Building elevator selecting algorithm
	 */
	public int getCurrFloor(){
		return currFloor;
	}

	public Dir getDir(){
		return currDir;
	}
	public boolean isFull(){
		return (riders>=maxOccupancyThreshold);
	}

	public String toString(){
		return elevatorStr;
	}

	protected void printEvent(String s){
		//TODO SEND TO FILE
		System.out.print(elevatorStr+" "+s);
	}
	
	private void printDebug(String s){
		if (DEBUG){
		System.out.println(s);
		}
	}
	
	public void quit(){
		myBuilding=null;
	}


	/*
	 * Thread stuff
	 */
	@Override
	public void run() {
		while (myBuilding!=null){
			doorIn=myBuilding.getDoor(this, currFloor, true);
			doorIn.arriveElev();
			printDebug("#E:go"+toString());
			VisitFloor(getNextFloor());
			OpenDoors();
			ClosedDoors();
		}

	}

}
