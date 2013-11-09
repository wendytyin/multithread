package elevator;

public class Building extends AbstractBuilding {

	private DoorEventBarrier[][][] doors;
	private Elevator[] elevators;
	private int idleElev;
	private boolean quit=false; //only true when we want to start deleting elevator threads
	
	public Building(int numFloors, int numElevators, int maxOccupancy) {
		super(numFloors, numElevators);
		idleElev=0;
		makeElevatorsAndDoors(numFloors,numElevators,maxOccupancy);
	}

	private void makeElevatorsAndDoors(int numFloors, int numElevators, int maxOccupancy) {
		elevators=new Elevator[numElevators];
		doors=new DoorEventBarrier[numElevators][numFloors][2];
		for(int i=0;i<numElevators;i++){
			elevators[i]=new Elevator(numFloors, i, maxOccupancy, this);
			for (int j=0;j<numFloors;j++){
				doors[i][j][0]=new DoorEventBarrier();
				doors[i][j][1]=new DoorEventBarrier();
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
	private int calculateDistance(int rFloor, int eFloor, boolean rdir, boolean edir){
		int dir=0;
		if ((rFloor==eFloor) && (rdir==edir)){return dir;}
		if (edir){
			if (!rdir){ dir=(numFloors-eFloor)+(numFloors-rFloor);}
			else {
				if (eFloor>rFloor){ dir=(numFloors-eFloor)+(numFloors+rFloor);}
				else { dir=rFloor-eFloor;}
			}
		}
		else {
			if (rdir){ dir=eFloor+rFloor;}
			else {
				if (eFloor>rFloor){ dir=eFloor-rFloor;}
				else { dir=(numFloors+eFloor)+(numFloors-rFloor);}
			}
			
		}
		return dir;
	}
	
	@Override
	public AbstractElevator CallUp(int fromFloor) {
		AbstractElevator e=null;
		int min=2*numFloors;
		for (int i=0;i<numElevators;i++){
			Elevator tmp=elevators[i];
			int j=calculateDistance(fromFloor,tmp.getCurrFloor(),true,tmp.getDir());
			if (j==0){ //special case, the elevator is already here
				e=tmp;
				break;
			}
			if (j<min){
				e=tmp;
				min=j;
			}
		}
		e.RequestFloor(fromFloor);
		notifyAll();
		return e;
	}

	@Override
	public AbstractElevator CallDown(int fromFloor) {
		AbstractElevator e=null;
		int min=2*numFloors;
		for (int i=0;i<numElevators;i++){
			Elevator tmp=elevators[i];
			int j=calculateDistance(fromFloor,tmp.getCurrFloor(),false,tmp.getDir());
			if (j<min){
				e=tmp;
				min=j;
			}
		}
		e.RequestFloor(fromFloor);
		notifyAll(); //too bad i cant wake up that elevator specifically
		return e;
	}
	
	public DoorEventBarrier getDoor(AbstractElevator e, int floor, boolean in){
		int i=in?0:1;
		return doors[e.getID()][floor-1][i];
	}
	
	/**
	 * Called by elevators when waiting for riders. 
	 */
	public synchronized void arrive(){
		idleElev++;
		if ((idleElev==numElevators) && quit){
			notifyAll();
		}
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns when all elevator threads have finished running
	 */
	public synchronized void quit(){
		quit=true;
		while (idleElev!=numElevators){
			try {
				wait(); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			System.exit(1); //TODO?
		}
	}
}
