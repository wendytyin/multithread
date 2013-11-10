package elevator;

public class Building extends AbstractBuilding {

	private final boolean DEBUG=false;
	
	private DoorEventBarrier[][][] doors;
	private Elevator[] elevators;
	
	
	public Building(int numFloors, int numElevators, int maxOccupancy) {
		super(numFloors, numElevators);
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
			for (int j=0;j<numFloors;j++){
				doors[i][j][0]=new DoorEventBarrier(elevators[i],(j+1));
				doors[i][j][1]=new DoorEventBarrier(elevators[i],(j+1));
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
	private int calculateDistance(int rFloor, Dir rdir, Elevator e){
		int dist=0;
		if (e.isFull()){return (2*numFloors);}
		int eFloor=e.getCurrFloor();
		Dir edir=e.getDir();
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
	
	private AbstractElevator getNearestElev(int fromFloor,Dir d){
		AbstractElevator e=null;
		int min=2*numFloors;
		for (int i=0;i<numElevators;i++){
			Elevator tmp=elevators[i];
			int j=calculateDistance(fromFloor,d,tmp);
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
		return e;
	}
	
	
	@Override
	public AbstractElevator CallUp(int fromFloor) {
		AbstractElevator e=getNearestElev(fromFloor,Dir.UP);
		while (e==null){ //could not find any empty elevators
			synchronized(this){
				try {
					this.wait(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e=getNearestElev(fromFloor,Dir.UP);
			}
		}

		printDebug("#B: requested:"+e.toString());
		
		DoorEventBarrier eDoor=getDoor(e,fromFloor,true);
		
		eDoor.arrive(Dir.UP);
		
		return e;
	}

	@Override
	public AbstractElevator CallDown(int fromFloor) {
		AbstractElevator e=getNearestElev(fromFloor,Dir.DOWN);
		while (e==null){  //could not find any empty elevators
			synchronized(this){
				try {
					this.wait(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e=getNearestElev(fromFloor,Dir.DOWN);
			}
		}
		printDebug("#B:"+fromFloor+" called down:"+e.toString());
		DoorEventBarrier eDoor=getDoor(e,fromFloor,true);
		
		eDoor.arrive(Dir.DOWN);

		return e;
	}
	
	public DoorEventBarrier getDoor(AbstractElevator e, int floor, boolean in){
		int i=in?0:1;
		return doors[e.getID()][floor-1][i];
	}
	
	public void quit(){
		System.exit(0);
	}
	
	
	private void printDebug(String s){
		if (DEBUG){
		System.out.println(s);
		}
	}
	
}
