package tests;

import eventbarrier.*;

public class EventBarriertest {

	public void run(){
		EventBarrier e=new EventBarrier();
		Thread p=new Thread(new Producer(e));
		Thread c1=new Thread(new Consumer(e));
		Thread c2=new Thread(new Consumer(e));
		c1.start();
		c2.start();
		p.start();
	}
}
