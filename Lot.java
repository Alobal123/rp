package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


/**
 * M�stsk� ��st reprezentuj�c� jeden pozemek ve m�st�.
 */
public class Lot extends City_part implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6035785538595754370L;
	/** Budova stoj�c� na tomto pozemku. */
	public Building building;
	
	/**
	 * Konstruktor
	 *
	 * @param streets ulice tvo��c� hranice pozemku
	 * @param firstnode the firstnode
	 */
	public Lot (ArrayList<Street> streets,Node firstnode){
		this.firstnode = firstnode;
		this.streets = streets;
		find_area();
	}
	
	/**
	 * Rozd�l� tento pozemek na dva podobn� velk� pozemky.
	 * Vede ulici kolmo zhruba z prost�edku nejdel�� ulice.
	 *
	 * @param block the block
	 * @return true, if successful
	 */
	public boolean divide(Block block){
		
		Street longest = null;
		double best = Double.MIN_VALUE;
		for(Street s: streets){
			if(s.length > best){
				longest = s;
				best = s.length;
			}
		}
		double move = Math.random();
		move = 0.5 - move/50;
		
		double newx = (move*longest.node1.getPoint().getX() + (1-move)*longest.node2.getPoint().getX());
		double newy = (move*longest.node1.getPoint().getY() + (1-move)*longest.node2.getPoint().getY());
		Node oldnode = new Node( newx , newy, longest.major,true);
		oldnode.angle = longest.get_absolute_angle(longest.node1);
		
		double[] angles = {90,270};
		boolean rt = false;
		for(double d: angles){
			Node newnode = Node.make_new_node((oldnode.angle+d)%360, Street_type.lot_border, oldnode, 1000);
			newnode.setBuilt(true);
			Street newstreet = new Street(oldnode, newnode, Street_type.lot_border);
			rt = rt|find_cross(longest, newstreet, oldnode, newnode, block);
		}
		return rt;
	}
	
	/**
	 * Zkus� um�stit budovu do tohoto pozemku. 
	 *
	 * @param building umis�ovan� budova
	 * @param settings aktu�ln� parametry
	 * @return zda se poda�ilo budovu um�stit
	 */
	public boolean place_building(Building building,Settings settings){
		boolean succes = false;
		for(Street s: streets){
			if(s.major != Street_type.lot_border){
				if(try_placing(s, building,settings)){
					succes = true;
					break;
				}
			}
		}
		if(!succes){
			for(Street s: streets){
				if(s.major == Street_type.lot_border){
					if(try_placing(s, building,settings)){
						succes = true;
						break;
					}
				}
			}
			
		}
		return succes;
		
	}
	
	/**
	 * Pokus� se v�emi mo�n�mi zp�soby um�stit budovu do tohoto pozemku na ulici s.
	 *
	 * @param s ulice, na kteoru s ebudova umis�uje
	 * @param building umis�ovan� budova
	 * @param settings aktu�ln� parametry
	 * @return zda se poda�ilo budovu um�stit
	 */
	private boolean	try_placing(Street s,Building building,Settings settings){
		boolean succes = false;
		succes = building.try_place_on_street(this, s, true, false,true,settings);
		if(!succes)
			succes = building.try_place_on_street(this, s, false, false,true,settings);
		if(!succes)
			succes = building.try_place_on_street(this, s, true, true,true,settings);			
		if(!succes)
			succes = building.try_place_on_street(this, s, false, true,true,settings);	
		if(!succes)
			succes = building.try_place_on_street(this, s, true, false,false,settings);
		if(!succes)
			succes = building.try_place_on_street(this, s, false, false,false,settings);
		if(!succes)
			succes = building.try_place_on_street(this, s, true, true,false,settings);			
		if(!succes)
			succes = building.try_place_on_street(this, s, false, true,false,settings);		
		return succes;
		
	}
	
	/**
	 * Najde m�sto, kde se ulice newstreet k��� s n�jakou hranic� tohoto pozemku. V m�st� k��en� vytvo�� nov� uzel
	 * a ulici v n�m ukon��.
	 *
	 * @param longest the longest
	 * @param newstreet the newstreet
	 * @param oldnode the oldnode
	 * @param newnode the newnode
	 * @param block the block
	 * @return true, if successful
	 */
	private boolean find_cross(Street longest, Street newstreet,Node oldnode,Node newnode,Block block){
		Street intersecting = null;
		Point intersection = null;
		double min = Point.dist(oldnode.getPoint(), newnode.getPoint());
		for (Node n: block.lot_borders) {
			for(Street street: n.streets){
				if(street != longest){
					Point intersect = Street.getIntersection(street, newstreet);
					if(intersect != null && Point.dist(oldnode.getPoint(), intersect) < min
							&& Point.dist(intersect, street.node1.getPoint()) > 0.00001 && Point.dist(intersect, street.node2.getPoint()) > 0.00001){
						intersecting = street;
						intersection = intersect;
						min = Point.dist(oldnode.getPoint(), intersect);
					}
				}
			}
		}
		if(intersecting != null){
			
			Node newnode2 = new Node(intersection.getX(), intersection.getY(), intersecting.major,intersecting.built);
			if(this.streets.contains(intersecting)){
				Street newstreet0 = new Street(oldnode, newnode2, Street_type.lot_border,true);
				Street newstreet1 = new Street(longest.node1, oldnode, longest.major,true);
				Street newstreet2 = new Street(longest.node2, oldnode, longest.major,true);
				Street newstreet3 = new Street(intersecting.node1, newnode2, intersecting.major,true);
				Street newstreet4 = new Street(intersecting.node2, newnode2, intersecting.major,true);
		
				longest.node1.streets.add(newstreet1);
				longest.node1.streets.remove(longest);
				longest.node2.streets.add(newstreet2);
				longest.node2.streets.remove(longest);
				oldnode.streets.add(newstreet0);
				oldnode.streets.add(newstreet1);
				oldnode.streets.add(newstreet2);
			
				intersecting.node1.streets.add(newstreet3);
				intersecting.node1.streets.remove(intersecting);
				intersecting.node2.streets.add(newstreet4);
				intersecting.node2.streets.remove(intersecting);
				newnode2.streets.add(newstreet0);
				newnode2.streets.add(newstreet3);
				newnode2.streets.add(newstreet4);
				
				block.lot_borders.add(oldnode);
				block.lot_borders.add(newnode2);
				return true;
			}
		}
		return false;
	}

	/**
	 * Um�st� do tohoto pozemku n�kteoru z budov, kter� p��slu�� lutu. 
	 *
	 * @param lut the lut
	 * @param settings the settings
	 */
	public void choose_and_place(Lut lut,Settings settings) {
		Collections.shuffle(lut.getBuildings());
		for(Building b: lut.getBuildings()){
			
			Building building = b.copy();
			if(place_building(building, settings)){
				this.building = building;
				break;
			}
			
		}
		
	}


	

}
