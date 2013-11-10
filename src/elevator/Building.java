package elevator;

public class Building extends AbstractBuilding {

	private DoorEventBarrier[][][] doors;
	private Elevator[] elevators;
//	private int idleElev;
//	private boolean quit=false; //only true when we want to quit the program
	
	public Building(int numFloors, int numElevators, int maxOccupancy) {
		super(numFloors, numElevators);
//		idleElev=0;
		makeElevatorsAndDoors(numFloors,numElevators,maxOccupancy);
		runElevators();
	}

	private void runElevators() {
		for (int i=0;i<numElevators;i++){
			new Thread(elevators[i]).start();
		}
	}

	private void makeElevatorsAndDoors(int numFloors, int numElevators, int maxOccupancy) {
		elevators=new Elevator[numElevators];
		doors=new DoorEventBarrier[numElevators][numFloors][2];
		for(int i=0;i<numElevators;i++){
			elevators[i]=new Elevator(numFloors, i, maxOccupancy, this);
//			Object elevLock=new Object();
			for (int j=0;j<numFloors;j++){
				doors[i][j][0]=new DoorEventBarrier(elevators[i],(j+1));
				doors[i][j][1]=new DoorEventBarrier(elevators[i],(j+1));
//				doors[i][j][0]=new DoorEventBarrier(elevLock);
//				doors[i][j][1]=new DoorEventBarrier(elevLock);
//				doors[i][j][0]=new DoorEventBarrier();
//				doors[i][j][1]=new DoorEventBarrier();
			}
		}
	}

	/*
	 * Current algorithm: elevator minimizes number of turns it makes.
	 * We calculate a worst-case scenario where the elevator spends the same amount of time on each floor, 
	 * and visits every floor on the way to the rider.
	 * 
	 * May edit this algorithm later to be smarter (take into account different times on each floor)
	 */
	private int calculateDistance(int rFloor, int eFloor, Dir rdir, Dir edir){
		int dist=0;
		if ((rFloor==eFloor) && (rdir==edir)){return dist;}
		if (edir==Dir.UP){
			if (rdir==Dir.DOWN){ dist=(numFloors-eFloor)+(numFloors-rFloor);}
			else {
				if (eFloor>rFloor){ dist=(numFloors-eFloor)+(numFloors+rFloor);}
				else { dist=rFloor-eFloor;}
			}
		}
		else if (edir==Dir.DOWN){
			if (rdir==Dir.UP){ dist=eFloor+rFloor;}
			else {
				if (eFloor>rFloor){ dist=eFloor-rFloor;}
				else { dist=(numFloors+eFloor)+(numFloors-rFloor);}
			}
		}
		else {
			if (eFloor>rFloor){dist=eFloor-rFloor;}
			else {dist=rFloor-eFloor;}
		}
		return dist;
	}
	
	@Override
	public AbstractElevator CallUp(int fromFloor) {
		AbstractElevator e=null;
		int min=2*numFloors;
		for (int i=0;i<numElevators;i++){
			Elevator tmp=elevators[i];
			int j=calculateDistance(fromFloor,tmp.getCurrFloor(),Dir.UP,tmp.getDir());
			if (j==0){ //special case, the elevator is already here
				e=tmp;
				min=j;
				break;
			}
			if (j<min){
				e=tmp;
				min=j;
			}
		}

		System.out.println(fromFloor+"called up elev:"+e.toString());
//		e.RequestFloor(fromFloor); //TODO: PROBLEM CONTEXT SWITCH AFTER THIS, LIVELOCK
		DoorEventBarrier eDoor=getDoor(e,fromFloor,true);
		
//TODO: PROBLEMATIC
//		wakeUp();
		eDoor.arrive();
		//
		
		return e;
	}

	@Override
	public AbstractElevator CallDown(int fromFloor) {
		AbstractElevator e=null;
		int min=2*numFloors;
		for (int i=0;i<numElevators;i++){
			Elevator tmp=elevators[i];
			int j=calculateDistance(fromFloor,tmp.getCurrFloor(),Dir.DOWN,tmp.getDir());
			if (j<min){
				e=tmp;
				min=j;
			}
		}
		System.out.println("#B:"+fromFloor+" called down:"+e.toString()+","+min);
//		e.RequestFloor(fromFloor); //TODO: PROBLEMATIC
		DoorEventBarrier eDoor=getDoor(e,fromFloor,true);
		
		eDoor.arrive();

		return e;
	}
	
	public DoorEventBarrier getDoor(AbstractElevator e, int floor, boolean in){
		int i=in?0:1;
		return doors[e.getID()][floor-1][i];
	}
//	
//	/**
//	 * Called by elevators when no longer idle
//	 */
//	public synchronized void complete(){
//		idleElev--;
//	}
//	
//	protected synchronized void wakeUp(){
//		System.out.println("wakeup!");
//		notifyAll();
//	}
//	
//	/**
//	 * Returns when all elevator threads have finished running and gone idle
//	 */
//	public synchronized void quit(){
//		quit=true;
//		notifyAll();
//		while (idleElev!=numElevators){
//			try {
//				wait(); 
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//		}
//		System.out.flush();
//		System.exit(1); //TODO?
//	}
}
