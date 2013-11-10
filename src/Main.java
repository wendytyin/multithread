import elevator.*;
import eventbarrier.*;



public class Main {
	public static void main(String[] args){
//		EventBarrier e=new EventBarrier();
//		Thread p=new Thread(new Producer(e));
//		Thread c1=new Thread(new Consumer(e));
//		Thread c2=new Thread(new Consumer(e));
//		c1.start();
//		c2.start();
//		p.start();
		
		Building bldg=new Building(4, 1, 50);
		Rider r1=new Rider(1, 4, 2, bldg);
		Thread rr1=new Thread(r1);
		Rider r2=new Rider(2, 3, 2, bldg);
		Thread rr2=new Thread(r2);
		rr1.start();
		rr2.start();
	}
	
}
