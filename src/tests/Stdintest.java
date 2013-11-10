package tests;

import elevator.*;

public class Stdintest {
	private AbstractBuilding b;
	private Dispatcher d;
	
	private boolean built=false;
	
	public Stdintest(){
	}
	
	public void make(String s) throws InterruptedException{
		if (s.equals("quit")){
			System.out.println("quit");
			d.quit();
			b.quit();
			System.exit(0);
		}
		String[] strings=s.split(" ");
		if (!built){
			Integer flrs=Integer.parseInt(strings[0]);
			Integer elev=Integer.parseInt(strings[1]);
			Integer riders=Integer.parseInt(strings[2]);
			Integer occ=Integer.parseInt(strings[3]);
			
			b=new Building(flrs, elev, occ);
			d=new Dispatcher(riders);
			
			built=true;
		} else {
			Integer[] rid=new Integer[3];
			rid[0]=Integer.parseInt(strings[0]);
			rid[1]=Integer.parseInt(strings[1]);
			rid[2]=Integer.parseInt(strings[2]);
			d.addRider(rid);
		}
	}

}
