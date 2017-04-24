package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Mìstská èást reprezentující jeden pozemek ve mìstì.
 */
public class Lot extends City_part implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6035785538595754370L;
	/** Budova stojící na tomto pozemku. */
	public Building building;
	
	
	@Override
	public boolean equals(Object o){
		Lot lot = (Lot) o;
		if(Math.abs(area-lot.area) < 0.001 && Math.abs(lot.center.getX()-center.getX()) < 0.001 && Math.abs(lot.center.getY()-center.getY())<0.001)
			return true;
		return false;
	}
	
	/**
	 * Konstruktor
	 *
	 * @param streets ulice tvoøící hranice pozemku
	 * @param firstnode the firstnode
	 */
	public Lot (ArrayList<Street> streets,Node firstnode){
		this.firstnode = firstnode;
		this.streets = streets;
		compute_area();
	}
	/**
	 * Rozdìlí tento pozemek na dva podobnì velké pozemky.
	 * Vede ulici kolmo zhruba z prostøedku nejdelší ulice.
	 *
	 * @param block the block
	 * @return true, if successful
	 */
	public Street divide(Block block,boolean plus){
		
		Street longest = null;
		double best = Double.MIN_VALUE;
		for(Street s: streets){
			if(s.length > best){
				longest = s;
				best = s.length;
			}
		}

		
		
		double move = 0.5;

		double newx = (move*longest.node1.getPoint().getX() + (1-move)*longest.node2.getPoint().getX());
		double newy = (move*longest.node1.getPoint().getY() + (1-move)*longest.node2.getPoint().getY());
		Node oldnode = new Node( newx , newy, longest.major,true,null);
		oldnode.angle = longest.get_absolute_angle(longest.node1);
		Street newstreet = null;
		double[] angles = {90,270};
		for(double d: angles){
			Node newnode = Node.make_new_node((oldnode.angle+d)%360, Street_type.lot_border, oldnode, 1000,null);
			newnode.setBuilt(true);
			newstreet = new Street(oldnode, newnode, Street_type.lot_border);
			Street rt = find_cross(longest, newstreet, oldnode, newnode, block);
			if(rt!=null){
				return rt;
				
			}
		}
		return null;
	}
	public void undivide(Street newstreet,Block block,Street_type major){
		undivide_node(newstreet.node1,newstreet,block,major);
		undivide_node(newstreet.node2,newstreet,block,major);
	}
	private void undivide_node(Node node, Street newstreet,Block block,Street_type major){
		
		if(node.streets.size() == 3){
			Node n1 = null;
			Street s1 = null;
			Node n2 = null;
			Street s2 = null;
			for(Street s: node.streets){
				if(s==newstreet)
					continue;
				if(s1 == null)
					s1 = s;
				else
					s2 = s; 
			}
			n1 = s1.get_other_node(node);
			n2 = s2.get_other_node(node);
			Street oldstreet = new Street(n1, n2, s1.major);
			n1.streets.remove(s1);
			n1.streets.add(oldstreet);
			n2.streets.remove(s2);
			n2.streets.add(oldstreet);
			block.lot_borders.remove(node);
		}
		else{
			node.streets.remove(newstreet);
		}
		
		
	}
	/**
	 * Zkusí umístit budovu do tohoto pozemku. 
	 *
	 * @param building umisovaná budova
	 * @param settings aktuální parametry
	 * @return zda se podaøilo budovu umístit
	 */
	public boolean try_place_building(Building building,Settings settings){
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
	 * Pokusí se všemi monımi zpùsoby umístit budovu do tohoto pozemku na ulici s.
	 *
	 * @param s ulice, na kteoru s ebudova umisuje
	 * @param building umisovaná budova
	 * @param settings aktuální parametry
	 * @return zda se podaøilo budovu umístit
	 */
	private boolean	try_placing(Street s,Building building,Settings settings){

		boolean [] values = {true,false};
		for(boolean node1 : values){
			for(boolean rotation : values){
				for(boolean minus: values){
					if(building.try_place_on_street(this, s, node1, rotation,minus,settings)){
						return true;
					}
				}
			}
		}
		return false;
		
	}
	
	/**
	 * Najde místo, kde se ulice newstreet køíí s nìjakou hranicí tohoto pozemku. V místì køíení vytvoøí novı uzel
	 * a ulici v nìm ukonèí.
	 *
	 * @param longest the longest
	 * @param newstreet the newstreet
	 * @param oldnode the oldnode
	 * @param newnode the newnode
	 * @param block the block
	 * @return true, if successful
	 */
	private Street find_cross(Street longest, Street newstreet,Node oldnode,Node newnode,Block block){
		Street intersecting = null;
		Point intersection = null;
		double min = Double.MAX_VALUE;
		for (Node n: block.lot_borders) {
			for(Street street: n.streets){
				if(street != longest){
					Point intersect = Street.getIntersection(street, newstreet);
						
					if(intersect != null && Point.dist(oldnode.getPoint(), intersect) < min){
						intersecting = street;
						intersection = intersect;
						min = Point.dist(oldnode.getPoint(), intersect);
					}
				}
			}
		}
		Node close_node=null;
		min = Double.MAX_VALUE;
		for(Node n: block.lot_borders){
			if(this.check_if_inside(n)== Street_result.not_altered && n.compute_distance_from_street(newstreet)< 0.001 && Point.dist(n.getPoint(), oldnode.getPoint())<min && n!=oldnode){
				min = Point.dist(n.getPoint(), oldnode.getPoint());
				close_node = n;
			}
		}
		
		if(intersecting != null){
			Node newnode2 = null;
				if(Street.are_parallel(intersecting,newstreet)){
					double node1distance = intersecting.node1.compute_distance_from_street(longest);
					double node2distance = intersecting.node2.compute_distance_from_street(longest);
					if(node1distance<node2distance)
						close_node = intersecting.node1;
					else
						close_node = intersecting.node2;
				}
			
			if(close_node!=null &&  Point.dist(intersection,oldnode.getPoint())<Point.dist(close_node.getPoint(), oldnode.getPoint())){
				close_node = null;
			}
			newnode2 = new Node(intersection.getX(), intersection.getY(), intersecting.major,intersecting.built,null);
			for(Node n: block.lot_borders){
				if(Point.dist(newnode2.getPoint(),n.getPoint())< 0.001 &&(close_node==null || Point.dist(oldnode.getPoint(), n.getPoint())<Point.dist(oldnode.getPoint(), close_node.getPoint()))){
					close_node = n;
				}
			}
			
			if(close_node != null){
				newnode2 = close_node;
				Street newstreet0 = new Street(oldnode, newnode2, Street_type.lot_border,true);
				if(this.check_if_inside(newstreet0.get_center()) == Street_result.fail)
					return null;
				Street newstreet1 = new Street(longest.node1, oldnode, longest.major,true);
				Street newstreet2 = new Street(longest.node2, oldnode, longest.major,true);
				longest.node1.streets.add(newstreet1);
				longest.node1.streets.remove(longest);
				longest.node2.streets.add(newstreet2);
				longest.node2.streets.remove(longest);
				oldnode.streets.add(newstreet0);
				oldnode.streets.add(newstreet1);
				oldnode.streets.add(newstreet2);
				newnode2.streets.add(newstreet0);
				block.lot_borders.add(oldnode);
				
				return newstreet0;
			}
				
			if(this.streets.contains(intersecting)){		
				Street newstreet0 = new Street(oldnode, newnode2, Street_type.lot_border,true);
				if(block.check_if_inside(newstreet0.get_center()) == Street_result.fail)
					return null;
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
				
				return newstreet0;
			}
		}
		return null;
	}

	/**
	 * Umístí do tohoto pozemku nìkterou z budov, které pøísluší lutu. 
	 *
	 * @param lut the lut
	 * @param settings the settings
	 */
	public boolean choose_and_place_building(Lut lut,Settings settings) {
		//Collections.shuffle(lut.getBuildings());
		for(Building b: lut.getBuildings()){
			Building building = b.copy();
			if(try_place_building(building, settings)){
				this.building = building;
				return true;
			}
			
		}
		return false;
		
	}


}
