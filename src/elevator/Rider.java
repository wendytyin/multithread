package elevator;


public class Rider implements Runnable{
	
	private final String riderStr;
	private int currFloor;
	private int wantedFloor;
	protected int riderId;
	
	protected Building myBuilding;
	protected AbstractElevator myElevator;
	protected DoorEventBarrier elevDoor;
	
	
	public Rider (int id, int currFloor, int wantedFloor, AbstractBuilding bldg){
		this.riderId=id;
		this.currFloor=currFloor;
		this.wantedFloor=wantedFloor;
		myBuilding=(Building) bldg;
		riderStr="R"+id+" ";
	}
	
	/**
	 * Changes rider thread properties to 
	 * @param id
	 * @param currFloor
	 * @param wantedFloor
	 * @return true if thread successfully changed, false if thread reject the change
	 */
	public boolean resetRider(int id, int currFloor, int wantedFloor){
		if (currFloor==wantedFloor){
			this.riderId=id;
			this.currFloor=currFloor;
			this.wantedFloor=wantedFloor;
			return true;
		}
		//rider thread still needs to travel
		return false;
	}
	
	protected void getElevator(){
		String tmp="";
		if (wantedFloor>currFloor){
			myElevator=myBuilding.CallUp(currFloor);
			tmp="U";
		}
		else {
			myElevator=myBuilding.CallDown(currFloor);
			tmp="D";
		}
		elevDoor=myBuilding.getDoor(myElevator, currFloor, true);
		tmp=String.format("pushes %s%d\n",tmp,currFloor);
		printEvent(tmp);
	}
	
	protected boolean enterElevator(){
		elevDoor.arrive();
		boolean attemptSuc=myElevator.Enter();
		elevDoor.complete();
		if (!attemptSuc){
			getElevator();
		}
		else {
			String tmp=String.format("enters %s on F%d\n",myElevator.toString(),currFloor);
			printEvent(tmp);
		}
		return attemptSuc;
	}
	protected void rideElevator(){
		myElevator.RequestFloor(wantedFloor);
		elevDoor=myBuilding.getDoor(myElevator, wantedFloor, false);
		String tmp=String.format("pushes %sB%d\n",myElevator.toString(),wantedFloor);
		printEvent(tmp);
	}
	
	protected void exitElevator(){
		elevDoor.arrive();
		myElevator.Exit();
		String tmp=String.format("exits %s on F%d\n",myElevator.toString(),myElevator.getCurrFloor());
		printEvent(tmp);
	}

	//don't know if this will be useful
	public String toString(){
		return riderStr;
	}
	protected void printEvent(String s){
		//TODO SEND TO EVENTBARRIER
		System.out.println(riderStr+s);
	}


	@Override
	public void run() {
		while (riderId!=-1){
			//TODO: WAIT ON INPUT
			while (currFloor!=wantedFloor){
				getElevator();
				enterElevator();
				rideElevator();
				exitElevator();
			}
		}
	}
	
}
