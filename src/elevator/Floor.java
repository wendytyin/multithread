package elevator;

public class Floor implements Comparable<Floor>{
	public final int floor;
	public final Dir dir;
	
	public Floor(int floor, Dir dir){
		this.floor=floor;
		this.dir=dir;
	}

	public Floor increment(){
		return new Floor(this.floor+1,Dir.DOWN);
//		if (this.dir==Dir.UP){ return new Floor(this.floor+1,Dir.DOWN);	}
//		else if (this.dir==Dir.X) { return new Floor(this.floor,Dir.UP); }
//		return new Floor(this.floor,Dir.X);
	}

	public Floor decrement(){
		return new Floor(this.floor-1,Dir.UP);
//		if (this.dir==Dir.DOWN){ return new Floor(this.floor-1,Dir.UP);	}
//		else if (this.dir==Dir.X) { return new Floor(this.floor,Dir.DOWN); }
//		return new Floor(this.floor,Dir.X);
	}
	
	@Override
	public int compareTo(Floor o) {
		if (this.floor<o.floor){
			return -1;
		}
		else if (this.floor==o.floor){
			return (this.dir.compareTo(o.dir));
		}
		return 1;
	}

}
