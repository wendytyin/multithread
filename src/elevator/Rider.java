package elevator;


public class Rider implements Runnable{
	
	private final boolean DEBUG=false;

	private static Dispatcher dispatch=null;
	
	private final String riderStr;
	private int currFloor;
	private int wantedFloor;
	protected int riderId;

	protected Building myBuilding;
	protected Elevator myElevator;
	protected DoorEventBarrier elevDoor;


	public Rider (int id, int currFloor, int wantedFloor, AbstractBuilding bldg){
		this.riderId=id;
		this.currFloor=currFloor;
		this.wantedFloor=wantedFloor;
		myBuilding=(Building) bldg;
		riderStr="R"+id+" ";
	}
	
	public void setDispatcher(Dispatcher d){
		dispatch=d;
	}

	/**
	 * Changes rider thread properties to 
	 * @param id
	 * @param currFloor
	 * @param wantedFloor
	 * @return true if thread successfully changed, false if thread reject the change
	 */
	private boolean resetRider(int id, int currFloor, int wantedFloor){
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
		String tmp=(wantedFloor>currFloor)?"U":"D";
		tmp=String.format("pushes %s%d\n",tmp,currFloor);
		printEvent(tmp);
		if (wantedFloor>currFloor){
			myElevator=(Elevator) myBuilding.CallUp(currFloor);
			//			tmp="U";
		}
		else {
			myElevator=(Elevator) myBuilding.CallDown(currFloor);
			//			tmp="D";
		}
	}

	protected boolean enterElevator(){
		if (myElevator==null){return false;}
		boolean attemptSuc=false;
		Dir d=myElevator.getDir();
		printDebug("#R: dir"+d);
		if (d==Dir.X){
			attemptSuc=myElevator.Enter();
		}
		else if ((d==Dir.UP)&&(wantedFloor>currFloor)){
			attemptSuc=myElevator.Enter();
		}
		else if ((d==Dir.DOWN)&&(wantedFloor<currFloor)){
			attemptSuc=myElevator.Enter();
		}
		if (attemptSuc){
			String tmp=String.format("enters %s on F%d\n",myElevator.toString(),currFloor);
			printEvent(tmp);
		}
		elevDoor=myBuilding.getDoor(myElevator, currFloor, true);
		elevDoor.complete();
		return attemptSuc;
	}
	protected void rideElevator(){
		String tmp=String.format("pushes %sB%d\n",myElevator.toString(),wantedFloor);
		printEvent(tmp);
		elevDoor=myBuilding.getDoor(myElevator, wantedFloor, false);
		elevDoor.arrive();
	}

	protected void exitElevator(){
		currFloor=myElevator.getCurrFloor();
		myElevator.Exit();
		String tmp=String.format("exits %s on F%d\n",myElevator.toString(),myElevator.getCurrFloor());
		printEvent(tmp);
		elevDoor.complete();
	}

	//don't know if this will be useful
	public String toString(){
		return riderStr;
	}
	private void printDebug(String s){
		if (DEBUG){
		System.out.println(s);
		}
	}
	protected void printEvent(String s){
		//TODO SEND TO EVENTBARRIER
		System.out.print(riderStr+s);
	}


	@Override
	public void run() {
		while (riderId!=-1){
			if (dispatch!=null){ //for non-hard coding of riders
				try {
					Integer[] prop=dispatch.getRider(riderId);
					resetRider(prop[0],prop[1],prop[2]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while (currFloor!=wantedFloor){
				getElevator();
				boolean suc=enterElevator();
				printDebug("#R: suc"+suc);

				while (!suc){ //bit of a spin lock if failed enter

					printDebug("#R: "+myElevator+" full");
					getElevator();
					suc=enterElevator();
					printDebug("#R: suc"+suc);
				}
				rideElevator();
				exitElevator();
			}
		}
	}

}
