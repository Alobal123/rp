package krabec.citysimulator;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Zkratka za Land Use Type. Reprezentuje využití nìjaké èásti mìsta. 
 * Podle využití se pak urèuje hodnota a také jaké pozemky a budovy se zde budopu vyskytovat.
 */
public class Lut implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1015487939669366246L;

	static final Building default_building;
	static{
		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));
		points.add(new Point(0, 0.05));
		points.add(new Point(0.05, 0.05));
		points.add(new Point(0.05, 0));
		default_building = new Building(points, "New building");
	}
	
	/** Jméno */
	String name;
	
	/** Poèet obyvatel na jeden uzel */
	double residents;
	
	double minimal_lot_area = 0.01;
	
	/** Udává jaký podíl má být ve mìstì zabrán tímto typem využití. */
	double wanted_percentage;
	
	/** Seznam ohodnocení. */
	List<Valuation> valuations = new ArrayList<>();
	
	/** Seznam budov. */
	List<Building> buildings = new ArrayList<>();
	
	Color color;
	
	/**
	 * Konstruktor
	 *
	 * @param name the name
	 * @param residents the residents
	 * @param percentage the percentage
	 * @param color the color
	 */
	public Lut (String name, double residents,double percentage,Color color){
		this.name = name;
		this.residents = residents;
		this.color = color;
		this.wanted_percentage = percentage;
		
		//buildings.add(default_building);
	}
	
	/**
	 * Pøidá {@link Valuation} do seznamu ohodnocení.
	 *
	 * @param val the val
	 */
	public void add_val(Valuation val){
		this.valuations.add(val);
		if(val.type == Valuation_Types.clustering)
			val.influencing_lut = this;
	}
	public void find_min_area(){
		double min_area = 0;
			for(Building b: buildings){
				if(b.area > min_area){
					min_area = b.area;
				}
			}
			if(min_area==0)
				min_area = 0.01;	
			this.minimal_lot_area = min_area*1.5;
	}
	
	/**
	 * Ohodnotí blok pomocí seznamu ohodnocení a vrátí jejich vážený souèet.
	 *
	 * @param block Blok 
	 * @param network Graf ulic
	 * @param nd Nejkratší vzdálenosti
	 * @return Hodnota bloku.
	 */
	public double evaluate(Block block,Street_Network network, Node_Distance nd){
		double sum = 0;
		for(Valuation val: valuations){
			sum += val.get_value(network, block, nd) * val.weight;
		}
		block.value = sum;
		return sum;
	}
	@Override
	public String toString(){
		return name;
	}
	
	
}
