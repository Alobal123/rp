package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krabec.citysimulator.Node.NodeComparator;

// TODO: Auto-generated Javadoc
/**
 * T��da Crossroad reprezentuje k�i�ovatku. Je dan� seznamem �hl� mezi ulicemi a ur�uje, z jak� k�i�ovatky lze p�er�st v jakou.
 */
public class Crossroad implements Serializable {
	
	/** Po�et ulic vych�zej�c�ch z t�to k�i�ovatky. */
	int number_of_roads;
	/**
	 * Seznam v�ech k�i�ovatek, kter� mohou vzniknout z t�to k�i�ovatky p�id�n�m jedn� ulice.
	 */
	List<Crossroad> viable_crossroads = new ArrayList<>();
	
	
	/**
	 * Seznam �hl� mezi ulicemi, zadan� ve stupn�ch a se�azen�ch ve sm�ru hodinov�ch ru�i�ek.
	 */
	List<Double> angles;
	
	
	/**
	 * Konstruktor.
	 *
	 * @param number_of_roads Po�et ulic vych�zej�c�ch z t�to k�i�ovatky.
	 * @param angles Seznam �hl�.
	 */
	public Crossroad(int number_of_roads, List<Double> angles){
		this.number_of_roads = number_of_roads;
		this.angles = angles;
	}
	
	/**
	 * Najde v�echny k�i�ovatky, kter� mohou vzniknout z t�to k�i�ovatky p�id�n�m jedn� ulice.
	 * Dosad� v�sledek do p��slu�n�ho atributy.
	 *
	 * @param all_crossroads V�echny k�i�ovatky.
	 * @return Seznam k�i�ovatek
	 */
	public void get_viable_crossroads(List<Crossroad> all_crossroads){
		
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
							viable_crossroads.add(c);
							break;
						}
				}
					
			}
		}
		
	}
	
	/**
	 * Vrac� �hel, kter� sv�r� nov� ulice s prvn� ulic� t�to k��ovatky. Nov� ulice je ulice a-t� k�i�ovatky ze seznamu k�i�ovatek, kter� mouhou z t�to k�i�ovatky vzniknout.
	 *
	 * @param a Index nov� k�i�ovatky
	 * @return �hel
	 */
	public double get_relative_angle(int a){
		
		double sum = 0;
		Crossroad c = viable_crossroads.get(a);	
		for (int i = 0; i < this.number_of_roads; i++) {
			sum += c.angles.get((i) % c.number_of_roads);
			if(!this.angles.get((i ) % this.number_of_roads).equals(c.angles.get((i) % c.number_of_roads))){
				break;
			}
		}
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
	
	/** 
	 * Dv� k�i�ovatky se rovnaj�, pokud rotac� jedn� lze z�skat druhou.
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof Crossroad){
			Crossroad c = (Crossroad) o;
			if (c.number_of_roads == this.number_of_roads) {
					int same_angles = 0;
					for (int j = 0; j < c.number_of_roads; j++) {
						if(Math.abs( c.angles.get((j) % c.number_of_roads) - this.angles.get(j)) < 0.000001){ //TODO nastavit spravnou konstantu
							same_angles++;
						}
					}
					if(same_angles == number_of_roads)
						return true;
					
				
			}
		}
		return false;
	}
	
	/**
	 * Vrac� seznam v�ech k�i�ovatek, kter� vzniknou rotac� t�to k�i�ovatky.
	 *
	 * @return V�echny rotace
	 */
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
	
	/**
	 * Dan�mu uzlu se pokus� p�i�adit jednu z k�i�ovatek. Pokud ��dn� nesed�, varc� null.
	 *
	 * @param all_crossroads V�echny k�i�ovatky.
	 * @param node Uzel.
	 * @return K�i�ovatka odpov�daj�c� uzlu.
	 */
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
