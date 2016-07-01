package krabec.citysimulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
	
	/** The nodes. */
	List<Node> nodes;
	static Random rnd = new Random();
	
	/** The growthcenters. */
	//List<Street> streets;
	List<Point> growthcenters;
	
	/** List of nodes which are capable of further growth. */
	List<Node> to_grow_nodes;
	
	/** The focus_constant. */
	double focus_constant;
	double average_main_street_length;
	
	/**
	 * Instantiates a new street_ network.
	 *
	 * @param focus_constant the focus_constant
	 */
	public Street_Network(double focus_constant){
		nodes = new ArrayList<>();
		growthcenters = new ArrayList<>();
		to_grow_nodes = new ArrayList<>();
		this.focus_constant = focus_constant;
	}
	
	/**
	 * Grows new major streets. Chooses nodes to grow, grows street and then checks if
	 * new quarter came to be. If so, it is filled with minor streets.
	 */
	void grow_major_streets(){
		ArrayList<Node> chosen_nodes = choose_nodes_to_grow();
		
		for (Node node : chosen_nodes) {
			Street newstreet = grow_major_street(node);
			ArrayList<Street> new_quarter = check_for_new_quarters(newstreet,true);
			grow_minor_streets(new_quarter);
			new_quarter = check_for_new_quarters(newstreet,false);
			grow_minor_streets(new_quarter);
		}
	}
	
	/**
	 * Chooses from major nodes, which ones will be growing.
	 *
	 * @return List of chosen nodes.
	 */
	private ArrayList<Node> choose_nodes_to_grow(){  	 //TODO zohlednovat pomer poctu uzlu s valenci 2 a 4?
		
		ArrayList<Node> chosen = new ArrayList<>();
		assert to_grow_nodes.size()>0:"There are no nodes to grow";
	    double [] distribution = new double [to_grow_nodes.size()]; 
	    
	    double distSum = 0;
		for (int i = 0; i < to_grow_nodes.size(); i++) {
			distribution[i] = Math.pow(Math.E,-1*focus_constant*Point.get_smallest_distance(to_grow_nodes.get(i).point, growthcenters));
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
	 Street grow_major_street(Node node){
		Street s = null;
		boolean succes = false;
		int limit = 10;							//TODO nastav limit kolikrat se bude zkouset stavet cesta z uzlu nez se vyradi
		for (int i = 0; i < limit; i++) {
			//System.out.println(node.crossroad);
			int a = rnd.nextInt(node.crossroad.viable_crossroads.size());
			Crossroad new_crossroad = node.crossroad.viable_crossroads.get(a);
			
			double angle = (node.angle + node.crossroad.get_relative_angle(a)+360) % 360;
			System.out.println("angle" + angle);
			
			double max_length = 0.5;			//TODO nastav maximalni dleku ulice
			//double length = rnd.nextDouble() * max_length;
			double length = 0.5;
			double dx = Math.sin(angle * Math.PI / 180) * length;
			double dy = Math.cos(angle * Math.PI / 180) * length;
			
			Node newnode = new Node(node.point.x + dx, node.point.y + dy, true);
			
			newnode.angle = angle;
			
			s = new Street(node, newnode,true);
			
			newnode = check_for_close_node(s, newnode);
			s = new Street(node, newnode,true);
			
			if(true){
				succes = true;
				newnode.streets.add(s);
				
				if(!nodes.contains(newnode))
					nodes.add(newnode);
				
				newnode.angle = newnode.compute_angle();
				node.angle = node.compute_angle();
				
				node.crossroad = new_crossroad;
				node.streets.add(s);
				
				if(!to_grow_nodes.contains(newnode))
					to_grow_nodes.add(newnode);
				if(node.crossroad.viable_crossroads.size() == 0){
					to_grow_nodes.remove(node);
					
				}
				if(newnode.crossroad.viable_crossroads.size() == 0){
					System.out.println("removed a new node");
					to_grow_nodes.remove(newnode);
					
				}
				break;
			}
		}
		if(!succes){
			to_grow_nodes.remove(node);
		}
		return s;
	}
	
	private Node check_for_close_node(Street s, Node newnode) {
		for (Node n: nodes) {
			double close_node_constant = 0.1; //TODO najit spravnou hodnotu pro tuhle konstantu.
			if(s.node1 != n && s.node2!=n && Point.dist(n.point, newnode.point) < close_node_constant){
				System.out.println("Jsou blizko");
				System.out.println(n + " " + n.angle);
				System.out.println(n.crossroad);
				
				double new_angle1;
				double new_angle2;
				double sum = 0;
				int index = 0;
				double relative_angle = (360 + 360 - (n.angle - s.get_absolute_angle(newnode))) %360;
				
				if(n.crossroad.number_of_roads == 1){
					new_angle1 = (360 - relative_angle)%360;
					new_angle2 = (360 + relative_angle)%360;
					index = 0;
				}
				else{
				
					while(sum <= relative_angle){
						sum+=n.crossroad.angles.get(index);
						index++;
					}
					index--;
					new_angle1 = sum - relative_angle;
					new_angle2 = n.crossroad.angles.get(index) - new_angle1;
					
				}
				
				ArrayList<Double> new_angles = new ArrayList<>(n.crossroad.angles);
				new_angles.set(index, new_angle1);
				new_angles.add(index, new_angle2);
				Crossroad new_crossroad = new Crossroad(n.crossroad.number_of_roads+1, new_angles);
				
				for (Crossroad c: n.crossroad.viable_crossroads) {
					if(c.equals(new_crossroad)){
						System.out.println("Spojili se! ");
						n.crossroad = c;
						return n;
					}
				}
				
			}
		}
		return newnode;
		
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
	static ArrayList<Street> check_for_new_quarters(Street street,Boolean clockwise){
		Node n = street.node1;
		Street s = street;
		ArrayList<Street> rt = new ArrayList<>();
		HashSet<Street> Visited = new HashSet<>();
		
		while(s != null){
			Street s2 = s.get_least_angled(n, clockwise);
			rt.add(s);
			//System.out.println(s);
			if(s2 == null)
				return null;
			if (Visited.size()>1 && (street.node2 == s.node1 || street.node2 == s.node2)){
				System.out.println("Succes!");
				return rt;
			}
			
			for (Street visited : Visited) {
				if(Visited.size()>2 && (visited.node1 == s.node1 || visited.node1== s.node2 
						|| visited.node2 == s.node1 || visited.node2 == s.node2) ){
					return null;
				}
			}
			Visited.add(s);
			if(n == s2.node2)
				n = s2.node1;
			else
				n = s2.node2;
			s = s2;
			
		}
		return rt;
	}

	/**
	 * Fills the quarter with minor streets.
	 *
	 * @param quarter the quarter
	 */
	void grow_minor_streets(ArrayList<Street> quarter){
		
	}

}
