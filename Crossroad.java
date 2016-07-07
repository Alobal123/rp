package krabec.citysimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krabec.citysimulator.Node.NodeComparator;

public class Crossroad {
	
	int number_of_roads;
	/**
	 * This list stores all crossroads which can be made from this crossroad by adding one road.
	 */
	List<Crossroad> viable_crossroads;
	
	
	/**
	 * This list stores angles in degrees between the roads in the crossroad clockwise.
	 */
	List<Double> angles;
	
	
	public Crossroad(int number_od_roads, List<Double> angles){
		this.number_of_roads = number_od_roads;
		this.angles = angles;
	}
	
	/**
	 * Makes a list of each crossroad which can be made from this crossroad by adding one road.
	 * Potom dosadí tento list do atributu viable_crossroads této třídy.
	 * @param all_crossroads 
	 */
	public void get_viable_crossroads(List<Crossroad> all_crossroads){
		ArrayList<Crossroad> viable_crossroads = new ArrayList<>();
		//System.out.println(this);
		for(Crossroad c : all_crossroads){
			
			if (c.number_of_roads == this.number_of_roads + 1){
					int jump = 0;
					int matching_angles = 0;
					for (int i = 0; i < c.number_of_roads ; i++) {
						if(this.angles.get((i) % this.number_of_roads).equals(c.angles.get((i + jump) % c.number_of_roads))){
							matching_angles++;
						}
						else{
							if(jump==0)
								jump++;
							else
								matching_angles = -100;
						}
						if (matching_angles == this.number_of_roads - 1) {
							//System.out.println(c);
							viable_crossroads.add(c);
							break;
						}
				}
					
			}
		}
		this.viable_crossroads = viable_crossroads;
	}
	public double get_relative_angle(int a){
		
		double sum = 0;
		Crossroad c = viable_crossroads.get(a);
		System.out.println("");
		System.out.println("rostu z " + this);
		System.out.println("rostu v " +  c);
		
		for (int i = 0; i < this.number_of_roads; i++) {
			
			sum += c.angles.get((i) % c.number_of_roads);
			if(!this.angles.get((i ) % this.number_of_roads).equals(c.angles.get((i) % c.number_of_roads))){
				break;
			}
		}
		//System.out.println("Sum " +sum);
		return (360 + sum) % 360;
	}
	
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("Crossroad: ");
		for(Double d : angles){
			sb.append(d + ", ");
		}
		return sb.toString();

	}
	@Override
	public boolean equals(Object o){
		if(o instanceof Crossroad){
			Crossroad c = (Crossroad) o;
			if (c.number_of_roads == this.number_of_roads) {
					int same_angles = 0;
					for (int j = 0; j < c.number_of_roads; j++) {
						if(Math.abs( c.angles.get((j) % c.number_of_roads) - this.angles.get(j)) < 0.00000000001){ //TODO nastavit spravnou konstantu
							same_angles++;
						}
					}
					if(same_angles == number_of_roads)
						return true;
					
				
			}
		}
		return false;
	}
	
	public ArrayList<Crossroad> get_all_rotations(){
		ArrayList<Crossroad> rt =  new ArrayList<>();
		boolean all_equal = true;
		for (int i = 0; i < angles.size()-1; i++) {
			if(!angles.get(i).equals(angles.get(i+1))){
				all_equal = false;
				break;
			}
		}
		if(all_equal){
			rt.add(this);
			return rt;
		}
		
		for (int i = 0; i < number_of_roads; i++) {
			Collections.rotate(this.angles, 1);
			ArrayList<Double> new_angles = new ArrayList<>();
			new_angles = new ArrayList<>(angles);
			int number_od_roads = this.number_of_roads;
			Crossroad new_crossroad = new Crossroad(number_od_roads, new_angles);
			rt.add(new_crossroad);
		}
		return rt;
	}
	
	public Crossroad copy(){
		Crossroad c = new Crossroad(this.number_of_roads,new ArrayList<Double>(this.angles));
		return c;
	}
	public static Crossroad find(List<Crossroad> all_crossroads, Node node){
		node.sort();
		ArrayList<Double> angles = new ArrayList<>();
		Crossroad newcrossroad = new Crossroad(node.streets.size(), angles);
		for (int i = 0; i < node.streets.size()-1; i++) {
			angles.add((node.streets.get(i+1).get_absolute_angle(node) - node.streets.get(i).get_absolute_angle(node)));
		}
		angles.add((360 - node.streets.get(node.streets.size()-1).get_absolute_angle(node) + node.streets.get(0).get_absolute_angle(node)));
		
		for (Crossroad c: all_crossroads) {
			if(c.equals(newcrossroad))
				return c;
		}
		
		return null;
	}
	
}
