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
	private int currFloor; 
	private int riders; //total number of riders in elevator
	private Dir dir;
	protected TreeSet<Floor> reqFloors; 
	protected DoorEventBarrier doorIn;
	protected DoorEventBarrier doorOut;
	protected Building myBuilding;
	
	
	public Elevator(int numFloors, int elevatorId, int maxOccupancyThreshold, AbstractBuilding bldg) {
		super(numFloors, elevatorId, maxOccupancyThreshold);
		myBuilding=(Building) bldg;
		riders=0;
		currFloor=1;
		dir=Dir.X;
		reqFloors=new TreeSet<Floor>();
		elevatorStr="E"+elevatorId;
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
	private Floor getNextFloor(){
//		System.out.println("#E: gNF called");
		if (reqFloors.isEmpty()){
			System.out.println("#E: empty");
			dir=Dir.X;
			return null;
		}
		Floor i=new Floor(currFloor,dir);
//		System.out.println("#E: currfloor:"+currFloor);
		if((dir==Dir.UP) || (dir==Dir.X)){
//			System.out.println("#E: currfloor:"+currFloor+reqFloors.size());
			dir=Dir.UP;
			i=i.increment();
			i=reqFloors.ceiling(i);
			while ((i!=null)&&(i.dir==Dir.DOWN)){
				System.out.println("#Ei2: "+i.floor+i.dir);
				i=i.increment();
				System.out.println("#Ei2: "+i.floor+i.dir);
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
				System.out.println("#Ee2: "+i.floor+i.dir);
				i=i.decrement();
				i=reqFloors.floor(i);
			}
			if (i==null){
				i=reqFloors.first();
			}
		}
		System.out.println("#E: nF"+i.floor+i.dir);
		return i;
	}
	
	//should really only be called within Elevator
	@Override
	protected synchronized void VisitFloor(Floor f) { 
		assert(f!=null);
		int floor=f.floor;
		assert(floor>0 && floor<=numFloors);
		if (floor<currFloor){ dir=Dir.DOWN;}
		else { dir=Dir.UP;}
		String d=(dir==Dir.UP)?"up":"down";
		while (currFloor!=floor){
			if (dir==Dir.UP) currFloor++;
			else currFloor--;

			String tmp=String.format("moves %s to F%d\n", d, currFloor);
			printEvent(tmp);
		}
		//currFloor == (desired) floor
		reqFloors.remove(f);
		
		if (!hasFloors()){
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
	public void RequestFloor(int floor) {
		assert(floor>0 && floor<=numFloors);
		reqFloors.add(new Floor(floor,Dir.X));
//		System.out.println("#E: floors visit: "+reqFloors.first());
	}

	public void RequestFloor(int floor,Dir d) {
		assert(floor>0 && floor<=numFloors);
		reqFloors.add(new Floor(floor,d));
		System.out.println("#E: floors visit: "+floor+d);
	}
	


	public boolean hasFloors(){
		return reqFloors.isEmpty();
	}

	/*
	 * For Building elevator selecting algorithm
	 */
	public int getCurrFloor(){
		return currFloor;
	}
	
	public Dir getDir(){
		return dir;
	}
	public boolean isFull(){
		return (riders>=maxOccupancyThreshold);
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
		doorIn=myBuilding.getDoor(this, currFloor, true);
		while (true){
//			System.out.println("#E: start");
			doorIn.arriveElev();
			System.out.println("#E:go"+toString());
			VisitFloor(getNextFloor());
			OpenDoors();
			ClosedDoors();
		}
		
	}

}
