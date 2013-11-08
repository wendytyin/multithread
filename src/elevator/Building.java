package elevator;

public class Building extends AbstractBuilding {

	private DoorEventBarrier[][][] doors;
	private Elevator[] elevators;
	
	public Building(int numFloors, int numElevators, int maxOccupancy) {
		super(numFloors, numElevators);
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

	@Override
	public AbstractElevator CallUp(int fromFloor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractElevator CallDown(int fromFloor) {
		//TODO ALGORITHM
		// TODO requests floor on behalf of rider at last second
		return null;
	}
	
	public DoorEventBarrier getDoor(AbstractElevator e, int floor, boolean in){
		int i=in?0:1;
		return doors[e.getID()][floor-1][i];
	}

}
