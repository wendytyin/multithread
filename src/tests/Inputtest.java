package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Inputtest {
	
	private void readFile(String filename) throws IOException {
		BufferedReader b=new BufferedReader(new FileReader(filename));
		String s=b.readLine();
		String[] strings=s.split(" ");
		Integer floor=Integer.parseInt(strings[0]);
		while (s!=null){
		}
	}
	public void run(String filename){
		
	}
	
}
