package krabec.citysimulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.security.sasl.SaslException;
// TODO: Auto-generated Javadoc

/**
 * This class manages the street network of the city. It is responsible for growth of new streets.
 * 
 */
public class Street_Network {
	
	public static final Crossroad end_of_road;
	static {
		end_of_road = new Crossroad(1, null);
		end_of_road.angles = new ArrayList<>();
		end_of_road.angles.add(360.0);
		//TODO nejak spocitat tady tu specialni krizovatku
	}
	
	
	List<Crossroad> all_crossroads;
	List<Quarter> quarters = new ArrayList<>();
	/** The nodes. */
	List<Node> nodes;
	static Random rnd = new Random();
	
	/** The growthcenters. */
	//List<Street> streets;
	List<Point> growthcenters;
	
	/** List of nodes which are capable of further growth. */
	List<Node> to_grow_nodes;
	
	Node current_new_node;
	Street current_new_street;
	
	
	/**
	 * Instantiates a new street_ network.
	 *
	 * @param focus_constant the focus_constant
	 */
	public Street_Network(double focus_constant){
		nodes = new ArrayList<>();
		growthcenters = new ArrayList<>();
		to_grow_nodes = new ArrayList<>();
		Settings.focus_constant = focus_constant;
	}
	
	/**
	 * Grows new major streets. Chooses nodes to grow, grows street and then checks if
	 * new quarter came to be. If so, it is filled with minor streets.
	 */
	void grow_major_streets(){
		ArrayList<Node> chosen_nodes = choose_nodes_to_grow(to_grow_nodes);
		
		for (Node node : chosen_nodes) {
			Street newstreet = grow_street(node,true,null,to_grow_nodes);
			System.out.println("Clockwise:");
			ArrayList<Street> new_quarter = check_for_new_quarters(newstreet,true);
			System.out.println("Counterclockwise:");
			ArrayList<Street> new_quarter2 = check_for_new_quarters(newstreet,false);
			if(new_quarter != null && new_quarter2 != null){
				if(new_quarter.size() < new_quarter2.size()){
					Quarter q = new Quarter(new_quarter);
					quarters.add(q);
					grow_minor_streets(q);
				}
				else{
					Quarter q = new Quarter(new_quarter2);
					quarters.add(q);
					grow_minor_streets(q);
				}
			}
		}
	}
	
	/**
	 * Chooses from major nodes, which ones will be growing.
	 *
	 * @return List of chosen nodes.
	 */
	private ArrayList<Node> choose_nodes_to_grow(List<Node> to_grow_nodes){  	 //TODO zohlednovat pomer poctu uzlu s valenci 2 a 4?
		
		ArrayList<Node> chosen = new ArrayList<>();
		assert to_grow_nodes.size()>0:"There are no nodes to grow";
	    double [] distribution = new double [to_grow_nodes.size()]; 
	    
	    double distSum = 0;
		for (int i = 0; i < to_grow_nodes.size(); i++) {
			distribution[i] = Math.pow(Math.E,-1*Settings.focus_constant*Point.get_smallest_distance(to_grow_nodes.get(i).point, growthcenters));
			distSum +=distribution[i];
		}
		for (int j = 0; j < 1; j++) {					 //TODO vypocitat pocet generovanych ulic
				double rand = Math.random();
				double ratio = 1.0f / distSum;
				double tempDist = 0;
				for (int i = 0; i < distribution.length; i++) {
					tempDist += distribution[i];
					if (rand / ratio <= tempDist) {
						chosen.add(to_grow_nodes.get(i));
						break;
					}
				}
		}
        return chosen;
	}

	/**
	 * Grow_street.
	 *
	 * @param node Node from which street is growing.
	 * @return the street
	 */
	 Street grow_street(Node oldnode,Boolean major, Quarter quarter, List<Node> to_grow_nodes){
		 if(oldnode.crossroad.viable_crossroads.size() == 0){
				to_grow_nodes.remove(oldnode);
				return null;
		}
		
		boolean succes = true;
		int limit = 20;							//TODO nastav limit kolikrat se bude zkouset stavet cesta z uzlu nez se vyradi
		cyklus: for (int i = 0; i < limit; i++) {
			succes = true;
			int a = rnd.nextInt(oldnode.crossroad.viable_crossroads.size());
			Crossroad new_crossroad = oldnode.crossroad.viable_crossroads.get(a);
			Crossroad old_crossroad = oldnode.crossroad;
			double angle = (oldnode.angle + oldnode.crossroad.get_relative_angle(a)+360) % 360;
			double length;
			if(major)
				length= rnd.nextDouble() * (Settings.major_max_length - Settings.major_min_length) + Settings.major_min_length +Settings.major_prolongation;
			else
				length = rnd.nextDouble() * (Settings.minor_max_length - Settings.minor_min_length) + Settings.minor_min_length +Settings.minor_prolongation; 
			
			current_new_node = make_new_node(angle, major, oldnode,length);
			current_new_node.angle = angle;
			current_new_street = new Street( oldnode, current_new_node,major);
			current_new_node.streets.add(current_new_street);
			current_new_node.angle = current_new_node.compute_angle();
			oldnode.streets.add(current_new_street);
			oldnode.crossroad = new_crossroad;
			oldnode.angle = oldnode.compute_angle();
			nodes.add(current_new_node);
			to_grow_nodes.add(current_new_node);

			Street_Result result = Street_Result.not_altered;
			
			if(succes){
				result = check_for_crosses(current_new_street, current_new_node, oldnode,major,to_grow_nodes);
				if(result == Street_Result.not_altered){
					if(major)
						length -= Settings.major_prolongation;	
					else
						length -= Settings.minor_prolongation;
					double dx = Math.sin(angle * Math.PI / 180) * length;
					double dy = Math.cos(angle * Math.PI / 180) * length;
					current_new_node.point = new Point(oldnode.point.x + dx, oldnode.point.y + dy);
				}
			}
			else if (result == Street_Result.fail){
				revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
				succes = false;
			}
			if(succes && !major && result != Street_Result.altered){
				result = check_if_inside(current_new_node,current_new_street, quarter);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			
			
			if(succes){
				result = check_for_close_node(current_new_street, current_new_node,oldnode,major,to_grow_nodes);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			if(succes){
				result = check_for_close_streets(current_new_node,current_new_street,major);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			
		if(succes){
			break cyklus;
			}
		}
		
		if(!succes){
			to_grow_nodes.remove(oldnode);
			return null;
		}
		return current_new_street;
	}
	
	 
	private Street_Result check_for_close_node(Street s, Node newnode, Node oldnode,Boolean major,List<Node> to_grow_nodes) {
		
		double constant;
		if(major)
			 constant = Settings.major_close_node_constant;
		else
			constant = Settings.minor_close_node_constant;
		for (Node n: nodes) {
			
			if(s.node1 != n && s.node2!=n && Point.dist(n.point, newnode.point) < constant){
				Street newstreet = new Street(oldnode, n, major);
				
				oldnode.streets.remove(s);
				oldnode.streets.add(newstreet);
				n.streets.add(newstreet);
				
				Crossroad oldnode_cross = Crossroad.find(all_crossroads,oldnode);
				Crossroad n_cross = Crossroad.find(all_crossroads,n);
				if(oldnode_cross != null && n_cross != null){
					oldnode.crossroad = oldnode_cross;
					n.crossroad = n_cross;
					
					nodes.remove(newnode);
					to_grow_nodes.remove(newnode);
					current_new_node = n;
					current_new_street = newstreet;
					
					//System.out.println("Spojili se! ");
					return Street_Result.altered;
				}
				else{
					oldnode.streets.add(s);
					oldnode.streets.remove(newstreet);
					n.streets.remove(newstreet);
					//System.out.println("Nespojili se! ");
					return Street_Result.fail;
				}
			}
		}
		return Street_Result.not_altered;
		
	}

	/**
	 * Checks for a face which contains street given as parameter. 
	 * There might be two faces we choose on using the second parameter.
	 * Returns null if there is no relevant face containing this street.
	 *
	 * @param street the street
	 * @param clockwise the clockwise
	 * @return List of streets creating the face.
	 */
	
	private Street_Result check_for_crosses(Street newstreet, Node newnode, Node oldnode,Boolean major,List<Node> to_grow_nodes){
		Street intersecting = null;
		Point intersection = null;
		double min = Point.dist(oldnode.point, newnode.point);
		for (Node n: nodes) {
			for(Street street: n.streets){
				if(!street.equals(newstreet)){
					Point intersect = null;
					intersect  = Street.getIntersection(street, newstreet);
					if(intersect != null && Point.dist(oldnode.point, intersect) < min
							&& Point.dist(intersect, street.node1.point) > 0.00001 && Point.dist(intersect, street.node2.point) > 0.00001){
						intersecting = street;
						intersection = intersect;
						min = Point.dist(oldnode.point, intersect);
					}
				}
			}
		}
		
		if(intersecting != null){
			Node trynode = new Node(intersection.x, intersection.y, major);
			Street newstreet1 = new Street(intersecting.node1, trynode, intersecting.major);
			Street newstreet2 = new Street(intersecting.node2,trynode,intersecting.major);
			Street newstreet3 = new Street(oldnode,trynode,major);
			ArrayList<Double> angles = new ArrayList<>();
			angles.add(180.0);
			double angle = Street.get_angle(newstreet1, newstreet3);
			if(angle>180)
				angle = angle -180;
			angles.add(angle);
			angles.add(180.0 - angle);
			//System.out.println("Angle  is " + Street.get_angle(newstreet1, newstreet3));
			Crossroad newcross = new Crossroad(3, angles);
			for (Crossroad c: all_crossroads) {
				if(c.equals(newcross)){
					System.out.println("uchytil se v " + c);
					trynode.streets.add(newstreet1);
					trynode.streets.add(newstreet2);
					trynode.streets.add(newstreet3);
					intersecting.node1.streets.add(newstreet1);
					intersecting.node2.streets.add(newstreet2);
					intersecting.node1.streets.remove(intersecting);
					intersecting.node2.streets.remove(intersecting);
					trynode.crossroad = c;
					oldnode.streets.remove(newstreet);
					oldnode.streets.add(newstreet3);
			
					nodes.add(trynode);
					to_grow_nodes.add(trynode);
					nodes.remove(newnode);
					to_grow_nodes.remove(newnode);
					trynode.angle = trynode.compute_angle();
					current_new_node = trynode;
					current_new_street = newstreet3;
					return Street_Result.altered;
				}
			}
			return Street_Result.fail;
		}
		return Street_Result.not_altered;
	}
	
	private Street_Result check_for_close_streets(Node newnode,Street newstreet, Boolean major){
		double constant;
		if(major)
			 constant = Settings.major_close_street_constant;
		else
			constant = Settings.minor_close_street_constant;
		
		
		for (Node n: nodes) {
			for(Street s: n.streets){
				if((s.node1 != newnode && s.node2 != newnode && newnode.distance(s) < constant) 
						|| (newstreet.node1 != n && newstreet.node2 != n && n.distance(newstreet) < constant)){
					System.out.println("Je blizko");
					return Street_Result.fail;
				}
			}
		
		}
		return Street_Result.not_altered;
	}
	
	private Street_Result check_if_inside(Node newnode,Street newstreet, Quarter quarter){
		double angle = 0;
		boolean inside = true;
		while(angle<360){
			Street help_street = new Street(newnode, make_new_node(angle, false, newnode, Settings.sufficiently_big), false);
			int intersections = 0;
			for (Street s: quarter.main_streets) {
				if( //newstreet.node1 != s.node1 && newstreet.node1 != s.node2 
					//	&& newstreet.node2 != s.node1 && newstreet.node2 != s.node2 &&
						Street.getIntersection(s, help_street)!=null){
					intersections++;
				}
			}
			inside = inside && (intersections%2 == 1);
			angle+=9.9;
		}	
		if(inside)
			return Street_Result.not_altered;
		return Street_Result.fail;
	}
	
	private ArrayList<Street> check_for_new_quarters(Street street,Boolean clockwise){
		if(street == null || street.node1.streets.size() ==1 || street.node2.streets.size() ==1)
			return null;
		
		ArrayList<Street> rt = new ArrayList<>();
		if(!rt.contains(street))
			rt.add(street);
		else
			System.out.println("contains");
		System.out.println("Zacinam v " + street);
		Node n = street.node1;
		Street s = street.get_least_angled(n, clockwise,true);
		
	
		if(s.node1 == n)
			n = s.node2;
		else
			n = s.node1;
		
		while(s != street){
			rt.add(s);
			
			//System.out.println(s);
			Street s2 = s.get_least_angled(n, clockwise,true);
			if(s2.node1 == n)
				n = s2.node2;
			else
				n = s2.node1;
			s = s2;
		}

		
		return rt;
	}

	/**
	 * Fills the quarter with minor streets.
	 *
	 * @param quarter the quarter
	 */
	private void grow_minor_streets(Quarter quarter){
		System.out.println("Rostu male");
		ArrayList<Node> to_grow_nodes = new ArrayList<>();
		for (Street s: quarter.main_streets) {
			if(!to_grow_nodes.contains(s.node1))
				to_grow_nodes.add(s.node1);
			if(!to_grow_nodes.contains(s.node2))
				to_grow_nodes.add(s.node2);
		}
		int a = 0;
		while(!to_grow_nodes.isEmpty()){
			ArrayList<Node> chosen_nodes = choose_nodes_to_grow(to_grow_nodes);
			for (Node n: chosen_nodes){
				grow_street(n,false, quarter, to_grow_nodes);
			}
			a++;
		}
		
		
	}
	
	private void revert_changes(Node oldnode,Crossroad old_crossroad,Boolean major,List<Node> to_grow_nodes){
		
		System.out.println("revertuju");
		current_new_node.streets.remove(current_new_street);
		nodes.remove(current_new_node);
		to_grow_nodes.remove(current_new_node);
		oldnode.streets.remove(current_new_street);
		oldnode.crossroad = old_crossroad;
		oldnode.angle = oldnode.compute_angle();
		
		if(current_new_node.streets.size() == 2 && Math.abs(180 - Street.get_angle(current_new_node.streets.get(0), current_new_node.streets.get(1)))<0.0001){
			Node node1 = current_new_node.streets.get(0).node1;
			if(node1 == current_new_node)
				node1 = current_new_node.streets.get(0).node2;
			Node node2 = current_new_node.streets.get(1).node1;
			if(node2 == current_new_node)
				node2 = current_new_node.streets.get(0).node2;
			node1.remove_street_to_node(current_new_node);
			node2.remove_street_to_node(current_new_node);
			Street street = new Street(node1, node2, major);
			node1.streets.add(street);
			node2.streets.add(street);
			
		}
		
	}

	private Node make_new_node(double angle, boolean major, Node oldnode, double length){
		double dx = Math.sin(angle * Math.PI / 180) * length;
		double dy = Math.cos(angle * Math.PI / 180) * length;
		return new Node(oldnode.point.x + dx, oldnode.point.y + dy, major);
	}
}
