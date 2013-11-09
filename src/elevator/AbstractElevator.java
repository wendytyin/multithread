package elevator;

public abstract class AbstractElevator {

	protected int currFloor; //MODIFIED
	protected int numFloors; 
	protected final int elevatorId; //MODIFIED - should never change for thread lifetime
	protected final int maxOccupancyThreshold;

	/**
	 * Other variables/data structures as needed goes here 
	 */

	public AbstractElevator(int numFloors, int elevatorId, int maxOccupancyThreshold) {
		this.numFloors = numFloors;
		this.elevatorId = elevatorId;
		this.maxOccupancyThreshold = maxOccupancyThreshold;
	}
	

	/**
	 * Elevator control interface: invoked by Elevator thread.
 	 */

	/* Signal incoming and outgoing riders */
	public abstract void OpenDoors(); 	

	/**
	 * When capacity is reached or the outgoing riders are exited and
	 * incoming riders are in. 
 	 */
	public abstract void ClosedDoors();

	/* Go to a requested floor */
	public abstract void VisitFloor(int floor);


	/**
	 * Elevator rider interface (part 1): invoked by rider threads. 
  	 */

	/* Enter the elevator */
	public abstract boolean Enter();
	
	/* Exit the elevator */
	public abstract void Exit();

	/* Request a destination floor once you enter */
	/* Does not wait - is called by building too */
 	public abstract void RequestFloor(int floor);	
	
	/* Other methods as needed goes here */
 	

	/*
	 * May be useful in building nearest elevator algorithm?
	 */
	public int getCurrFloor(){
		return currFloor;
	}
	//is this necessary?
	public int getID(){
		return elevatorId;
	}
}
