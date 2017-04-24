package krabec.citysimulator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Random_Wrapped{

	Random rnd;
	long counter = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3972001090009645908L;
	
	public Random_Wrapped(){
		rnd = new Random();
	}
	
	public Random_Wrapped(long seed) {
		rnd = new Random(seed); 

	}
	

	public int nextInt(int bound,String msg){
		counter++;
		int i = rnd.nextInt(bound);
		return i;
	}
	
	public double nextDouble(String msg){
		
		counter++;
		double i = rnd.nextDouble();
		return i;
	}

}
