package krabec.citysimulator;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Zkratka za Land Use Type. Reprezentuje vyu�it� n�jak� ��sti m�sta. 
 * Podle vyu�it� se pak ur�uje hodnota a tak� jak� pozemky a budovy se zde budopu vyskytovat.
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
	
	/** Jm�no */
	private String name;
	
	/** Po�et obyvatel na jeden uzel */
	public double residents;
	
	double minimal_lot_area = 0.01;
	
	/** Ud�v� jak� pod�l m� b�t ve m�st� zabr�n t�mto typem vyu�it�. */
	public double wanted_percentage;
	
	/** Seznam ohodnocen�. */
	public List<Valuation> valuations = new ArrayList<>();
	
	/** Seznam budov. */
	private List<Building> buildings = new ArrayList<>();
	
	public Color color;
	
	Settings settings;
	
	/**
	 * Konstruktor
	 *
	 * @param name the name
	 * @param residents the residents
	 * @param percentage the percentage
	 * @param color the color
	 */
	public Lut (String name, double residents,double percentage,Color color,Settings settings){
		this.setName(name);
		this.residents = residents;
		this.color = color;
		this.wanted_percentage = percentage;
		this.settings = settings;
	}
	
	/**
	 * P�id� {@link Valuation} do seznamu ohodnocen�.
	 *
	 * @param val the val
	 */
	public void add_val(Valuation val){
		this.valuations.add(val);
		if(val.getType() == Valuation_Types.clustering)
			val.setInfluencing_lut(this);
	}
	
	public void find_min_area(){
		double min_area = 0;
		
		for(Building b: getBuildings()){
			if(b.getArea() > min_area){	
				min_area = b.getArea();
			}
		}
			
		if(min_area==0)
			min_area = 0.01;
		min_area = Math.pow((Math.sqrt(min_area)+ settings.street_width/2),2);
		this.minimal_lot_area = min_area*4;
	}
	
	/**
	 * Ohodnot� blok pomoc� seznamu ohodnocen� a vr�t� jejich v�en� sou�et.
	 *
	 * @param block Blok 
	 * @param network Graf ulic
	 * @param nd Nejkrat�� vzd�lenosti
	 * @return Hodnota bloku.
	 */
	public double evaluate(Block block,Street_Network network, Node_Distance nd){
		double sum = 0;
		for(Valuation val: valuations){
			sum += val.get_value(network, block, nd) * val.getWeight();
		}
		block.value = sum;
		return sum;
	}
	@Override
	public String toString(){
		return getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}
	
	
}
