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
		
		Building bldg=new Building(4, 2, 1);
		Rider r1=new Rider(1, 4, 2, bldg);
		Thread rr1=new Thread(r1);
		Rider r2=new Rider(2, 3, 4, bldg);
		Thread rr2=new Thread(r2);
		Rider r4=new Rider(4, 3, 4, bldg);
		Thread rr4=new Thread(r4);
		rr1.start();
		rr2.start();
		rr4.start();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Rider r3=new Rider(3, 1, 4, bldg);
		Thread rr3=new Thread(r3);
		rr3.start();
	}
	
}
