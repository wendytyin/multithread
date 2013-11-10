package elevator;

import java.util.TreeSet;

/* Notes to self:
 * Elevator cannot and must not close doors until all exiting riders have exited and as many entering as possible have entered
 * 
 * Extensions for badly behaved riders: 
 * 1. riders who call up or down, but do not wait (never try to invoke enter)
 * 2. riders who enter but do not request a floor (trap them, fuck them)
 * 3. riders who do not exit at their requested floor (at a different floor, or don't get off at all)
 */


public class Elevator extends AbstractElevator implements Runnable{

	private final String elevatorStr;
	private int currFloor; //MODIFIED
	private int riders; //total number of riders in elevator
	private Dir dir;
	protected TreeSet<Integer> reqFloors; 
	protected DoorEventBarrier doorIn;
	protected DoorEventBarrier doorOut;
	protected Building myBuilding;
	
	
	public Elevator(int numFloors, int elevatorId, int maxOccupancyThreshold, AbstractBuilding bldg) {
		super(numFloors, elevatorId, maxOccupancyThreshold);
		myBuilding=(Building) bldg;
		riders=0;
		currFloor=1;
		dir=Dir.X;
		reqFloors=new TreeSet<Integer>();
		elevatorStr="E"+elevatorId;
		System.out.println("#E: done loading");
	}


	/*
	 * May be useful in building nearest elevator algorithm?
	 */
	public int getCurrFloor(){
		return currFloor;
	}
	
	@Override
	public void OpenDoors() {
		doorIn=myBuilding.getDoor(this, currFloor,true);
		doorOut=myBuilding.getDoor(this, currFloor,false);

		String tmp=String.format("on F%d opens\n", currFloor);
		printEvent(tmp);
		
		doorOut.raise();
		doorIn.setDir(dir);
		doorIn.raise(); //automatically close doors upon return
//		System.out.println("return raise");
	}

	@Override
	public void ClosedDoors() {
		String tmp=String.format("on F%d closes\n", currFloor);
		printEvent(tmp);
		
	}
	
	/*
	 * Elevator attempts to minimize number of direction changes
	 */
	private int getNextFloor(){
		if (reqFloors.isEmpty()){
			return -1;
		}
		int i=-1;
		System.out.println("#E: currfloor:"+currFloor);
		if((dir==Dir.UP) || (dir==Dir.X)){
			System.out.println("#E: currfloor:"+currFloor+reqFloors.size());
			i=reqFloors.ceiling(currFloor);
			dir=Dir.UP;
			if (i==0){
				dir=Dir.DOWN;
				return getNextFloor();
			}
		} 
		else {
			System.out.println("#E: "+reqFloors.first());
			System.out.println("#E: size:"+reqFloors.size());
			i=reqFloors.floor(currFloor);
			if (i==0){
				System.out.println("#E: switch dir");
				dir=Dir.UP;
				return getNextFloor();
			}
		}
		return i;
	}
	
	//should really only be called within Elevator
	@Override
	public synchronized void VisitFloor(int floor) { 
		assert(floor>0 && floor<=numFloors);
		String d=(dir==Dir.UP)?"up":"down";
		while (currFloor!=floor){
			if (dir==Dir.UP) currFloor++;
			else currFloor--;

			String tmp=String.format("moves %s to F%d\n", d, currFloor);
			printEvent(tmp);
		}
		//currFloor == (desired) floor
		reqFloors.remove(floor);
		
		if (getNextFloor()==-1){
			dir=Dir.X;
		}
		if (currFloor==1){
			dir=Dir.UP;
		}
		if (currFloor==numFloors){
			dir=Dir.DOWN;
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
	 * Must be accessed through DoorEventBarrier (synchronization issues)
	 */
	@Override
	public synchronized void RequestFloor(int floor) {
		assert(floor>0 && floor<=numFloors);
		reqFloors.add(floor);
		System.out.println("#E: floors visit: "+reqFloors.first());
	}

	/**
	 * @return true for up, false for down
	 */
	public Dir getDir(){
		return dir;
	}
	
	public String toString(){
		return elevatorStr;
	}
	
	protected void printEvent(String s){
		//TODO SEND TO EVENTBARRIER
		System.out.println(elevatorStr+" "+s);
	}

	
	/*
	 * Thread stuff
	 */
	@Override
	public void run() {
		while (true){
			System.out.println("#E: start");
			while (getNextFloor()==-1){
				doorIn=myBuilding.getDoor(this, currFloor, true);
//				DoorEventBarrier d=myBuilding.getDoor(this, currFloor, true);
				System.out.println("wait"+toString());
//				myBuilding.arrive();
				dir=Dir.X;
				doorIn.arriveElev();
			}
//			myBuilding.complete();
			System.out.println("#E:go"+toString());
			VisitFloor(getNextFloor());
			OpenDoors();
			ClosedDoors();
		}
		
	}

}
