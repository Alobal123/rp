package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Tøída Crossroad reprezentuje køižovatku. Je daná seznamem úhlù mezi ulicemi a urèuje, z jaké køižovatky lze pøerùst v jakou.
 */
public class Crossroad implements Serializable,Comparable<Crossroad> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5891528450453833224L;
	/** Poèet ulic vycházejících z této køižovatky. */
	private int number_of_roads;
	/**
	 * Seznam všech køižovatek, které mohou vzniknout z této køižovatky pøidáním jedné ulice.
	 */
	public List<Crossroad> viable_crossroads = new ArrayList<>();
	/**
	 * Seznam úhlù mezi ulicemi, zadaný ve stupních a seøazených ve smìru hodinových ruèièek.
	 */
	public List<Double> angles = new ArrayList<>();
	/**
	 * Konstruktor.
	 *
	 * @param number_of_roads Poèet ulic vycházejících z této køižovatky.
	 * @param angles Seznam úhlù.
	 */
	public Crossroad(int number_of_roads, List<Double> angles){
		this.number_of_roads = number_of_roads;
		this.angles = angles;
	}
	public Crossroad(double ... angles){
		this.number_of_roads = angles.length;
		for (int i = 0; i < angles.length; i++) {
			this.angles.add(angles[i]);
		}
	}
	/**
	 * Najde všechny køižovatky, které mohou vzniknout z této køižovatky pøidáním jedné ulice.
	 * Dosadí výsledek do pøíslušného atributy.
	 *
	 * @param all_crossroads Všechny køižovatky.
	 * @return Seznam køižovatek
	 */
	public void get_viable_crossroads(List<Crossroad> all_crossroads){
		for(Crossroad c : all_crossroads){
			if (c.getNumber_of_roads() == this.getNumber_of_roads() + 1){
					int jump = 0;
					int matching_angles = 0;
					for (int i = 0; i < c.getNumber_of_roads() ; i++) {
						if(this.angles.get((i) % this.getNumber_of_roads()).equals(c.angles.get((i + jump) % c.getNumber_of_roads()))){
							matching_angles++;
						}
						else{
							if(jump==0)
								jump++;
							else
								matching_angles = -100;
						}
						if (matching_angles == this.getNumber_of_roads() - 1) {
							viable_crossroads.add(c);
							break;
						}
				}
					
			}
		}
	}
	
	/**
	 * Vrací úhel, který svírá nová ulice s první ulicí této køížovatky. Nová ulice je ulice a-té køižovatky ze seznamu køižovatek, které mouhou z této køižovatky vzniknout.
	 *
	 * @param a Index nové køižovatky
	 * @return Úhel
	 */
	public double get_relative_angle(int a){
		
		double sum = 0;
		Crossroad c = viable_crossroads.get(a);	
		for (int i = 0; i < this.getNumber_of_roads(); i++) {
			sum += c.angles.get((i) % c.getNumber_of_roads());
			if(!this.angles.get((i ) % this.getNumber_of_roads()).equals(c.angles.get((i) % c.getNumber_of_roads()))){
				break;
			}
		}
		return (360 + sum) % 360;
	}
	
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < angles.size(); i++) {
			sb.append(angles.get(i));
			if(i!= angles.size()-1)
				sb.append(" , ");
		}
		
		return sb.toString();
	}
	
	/** 
	 * Dvì køižovatky se rovnají, pokud rotací jedné lze získat druhou.
	 */
	@Override
	public boolean equals(Object o){
		if(o instanceof Crossroad){
			Crossroad c = (Crossroad) o;
			if (c.getNumber_of_roads() == this.getNumber_of_roads()) {
					int same_angles = 0;
					for (int j = 0; j < c.getNumber_of_roads(); j++) {
						if(Math.abs( c.angles.get((j) % c.getNumber_of_roads()) - this.angles.get(j)) < 0.000001){ //TODO nastavit spravnou konstantu
							same_angles++;
						}
					}
					if(same_angles == getNumber_of_roads())
						return true;
			}
		}
		return false;
	}
	
	/**
	 * Vrací seznam všech køižovatek, které vzniknou rotací této køižovatky.
	 *
	 * @return Všechny rotace
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
		
		for (int i = 0; i < getNumber_of_roads(); i++) {
			Collections.rotate(this.angles, 1);
			ArrayList<Double> new_angles = new ArrayList<>();
			new_angles = new ArrayList<>(angles);
			int number_od_roads = this.getNumber_of_roads();
			Crossroad new_crossroad = new Crossroad(number_od_roads, new_angles);
			rt.add(new_crossroad);
		}
		return rt;
	}
	
	/**
	 * Danému uzlu se pokusí pøiøadit jednu z køižovatek. Pokud žádná nesedí, varcí null.
	 *
	 * @param all_crossroads Všechny køižovatky.
	 * @param node Uzel.
	 * @return Køižovatka odpovídající uzlu.
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
	public int getNumber_of_roads() {
		return number_of_roads;
	}
	public static Crossroad Read_crossroad(String line){
		Crossroad crossroad =  new Crossroad();
		String[] split = line.split(",");
		for (int i = 0; i < split.length; i++) {
			try{
				double d = Double.parseDouble(split[i]);
				if(d>0)
					crossroad.angles.add(d);
			}
			catch(NumberFormatException e){
				return null;
			}	
		}
		crossroad.number_of_roads = crossroad.angles.size();
		return crossroad;
	}
	@Override
	public int compareTo(Crossroad o) {
		return Integer.compare(this.number_of_roads, o.number_of_roads);
	}

	
	
}
