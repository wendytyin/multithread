package tests;

import elevator.Building;
import elevator.Rider;

public class Elevatortest {


	//Building with 4 floors, 1 elevator, elevator capacity of 50 (relatively unbounded)

	public void run1(){
		Building bldg=new Building(4, 1, 50);
		Rider r1=new Rider(1, 3, 2, bldg);
		Thread rr1=new Thread(r1);
		rr1.start();
		Rider r2=new Rider(2, 1, 3, bldg);
		Thread rr2=new Thread(r2);
		Rider r4=new Rider(4, 3, 4, bldg);
		Thread rr4=new Thread(r4);
		rr2.start();
		rr4.start();
	}

	//Building with 4 floors, 1 elevator, elevator capacity of 2

	public void run2(){
		Building bldg=new Building(4, 1, 2);
		Rider r1=new Rider(1, 4, 2, bldg);
		Thread rr1=new Thread(r1);
		rr1.start();
		Rider r2=new Rider(2, 3, 4, bldg);
		Thread rr2=new Thread(r2);
		Rider r3=new Rider(3, 3, 4, bldg);
		Thread rr3=new Thread(r3);
		Rider r4=new Rider(4, 3, 4, bldg);
		Thread rr4=new Thread(r4);
		rr2.start();
		rr3.start();
		rr4.start();
	}


	//Building with 4 floors, 2 elevators, elevator capacity of 2
	public void run3(){
		Building bldg=new Building(4, 2, 2);
		Rider r0=new Rider(5, 4, 2, bldg);
		Thread rr0=new Thread(r0);
		rr0.start();
		Rider r1=new Rider(1, 4, 2, bldg);
		Thread rr1=new Thread(r1);
		rr1.start();
		Rider r2=new Rider(2, 3, 4, bldg);
		Thread rr2=new Thread(r2);
		Rider r3=new Rider(3, 3, 4, bldg);
		Thread rr3=new Thread(r3);
		Rider r4=new Rider(4, 3, 4, bldg);
		Thread rr4=new Thread(r4);
		rr2.start();
		rr3.start();
		rr4.start();
	}
}
