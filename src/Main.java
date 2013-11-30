import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import tests.*;

/*
 * TODO currently no input file support or stdin
 */
public class Main {


	public static void main(String[] args) throws FileNotFoundException{
		if (args.length==0){
			Elevatortest p=new Elevatortest();
			p.run1();//default: part one of elevator
		} 
		else if(args.length >1) {
			// Throw an error--too many args
		} else {// known just one arg
			if(args[0].equals("p1")){
				EventBarriertest eb=new EventBarriertest();
				eb.run();
				// call the EventBarrier
			}

			if(args[0].equals("p2part1")) {
				Elevatortest p=new Elevatortest();
				p.run1();
				// call the elevator part1
			} else if(args[0].equals("p2part2")) {
				Elevatortest p=new Elevatortest();
				p.run2();
				// call the elevator part2
			} else if(args[0].equals("p2part3")) {
				Elevatortest p=new Elevatortest();
				p.run3();
				// call the elevator part3
			} else if(args[0].equals("userinput")){
				Stdintest s=new Stdintest();
				BufferedReader b=new BufferedReader(new InputStreamReader(System.in));
				//			BufferedReader b=new BufferedReader(new FileReader(args[0]));
				String input;
				try {
					input = b.readLine();
					while (input!=null){
						try {
							s.make(input);
							input=b.readLine();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					b.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}
