package tests;

import java.util.Iterator;
import java.util.TreeSet;

import elevator.Dir;
import elevator.Floor;

public class Floortest {

	public TreeSet<Floor> ts=new TreeSet<Floor>();
	
	public static void main(String[]args){
		Floortest f=new Floortest();
		System.out.println(f.ts.size());
		f.ts.add(new Floor(1,Dir.UP));
		System.out.println(f.ts.size());
		f.ts.add(new Floor(1,Dir.X));
		System.out.println(f.ts.size());
		f.ts.add(new Floor(1,Dir.DOWN));
		System.out.println(f.ts.size());
		f.ts.add(new Floor(2,Dir.DOWN));
		System.out.println(f.ts.size());
		
		Iterator<Floor> i=f.ts.iterator();
		while (i.hasNext()){
			Floor fl=i.next();
			System.out.println(fl.floor+","+fl.dir);
			
		}
		System.out.println("\n");
		Floor fl=new Floor(1,Dir.X);
		fl=f.ts.ceiling(fl);

		System.out.println(fl.floor+","+fl.dir);
		fl=fl.increment();

		System.out.println(fl.floor+","+fl.dir);
		fl=f.ts.ceiling(fl);

		System.out.println(fl.floor+","+fl.dir);
		
	}
}
