package elevator;

import java.util.Collections;
import java.util.SortedSet;
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
	private int riders; //total number of riders in elevator
	private boolean upDir;
	private TreeSet<Integer> reqFloors; 
	protected DoorEventBarrier doorIn;
	protected DoorEventBarrier doorOut;
	protected Building myBuilding;
	
	
	public Elevator(int numFloors, int elevatorId, int maxOccupancyThreshold, AbstractBuilding bldg) {
		super(numFloors, elevatorId, maxOccupancyThreshold);
		myBuilding=(Building) bldg;
		currFloor=1; riders=0;
		upDir=true; 
		reqFloors=new TreeSet<Integer>();
		elevatorStr="E"+elevatorId+" ";
	}

	@Override
	public void OpenDoors() {
		doorIn=myBuilding.getDoor(this, currFloor,true);
		doorOut=myBuilding.getDoor(this, currFloor,false);

		String tmp=String.format("on F%d opens\n", currFloor);
		printEvent(tmp);
		
		doorOut.raise();
		doorIn.setDir(upDir);
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
	private int getNextFloor(){
		if (reqFloors.isEmpty()){
			return -1;
		}
		int i=-1;
		if(upDir){
			i=reqFloors.ceiling(currFloor);
			if (i==0){
				upDir=false;
				return getNextFloor();
			}
		} 
		else {
			i=reqFloors.floor(currFloor);
			if (i==0){
				upDir=true;
				return getNextFloor();
			}
		}
		return i;
	}
	
	//should really only be called within Elevator
	@Override
	public void VisitFloor(int floor) { 
		String dir=(upDir)?"up":"down";
		
		while (currFloor!=floor){
			if (upDir) currFloor++;
			else currFloor--;

			String tmp=String.format("moves %s to F%d\n", dir, currFloor);
			printEvent(tmp);
		}
		//currFloor == (desired) floor
		reqFloors.remove(floor);
	}

	@Override
	public synchronized boolean Enter() {
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

	@Override
	public synchronized void RequestFloor(int floor) {
		reqFloors.add(floor);
	}
	//is this necessary?
	public boolean full() {
		return (riders==maxOccupancyThreshold);
	}
	/**
	 * @return true for up, false for down
	 */
	public boolean getDir(){
		return upDir;
	}
	
	public String toString(){
		return elevatorStr;
	}
	
	protected void printEvent(String s){
		//TODO SEND TO EVENTBARRIER
		System.out.println(elevatorStr+s);
	}

	
	/*
	 * Thread stuff
	 */
	@Override
	public void run() {
		while (true){
			while (reqFloors.isEmpty()){
				myBuilding.arrive();
			}
			VisitFloor(getNextFloor());
			OpenDoors();
			ClosedDoors();
		}
		
	}

}
