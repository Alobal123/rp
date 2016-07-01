package krabec.citysimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// TODO: Auto-generated Javadoc
/**
 * The Class Tests.
 */
public class Tests {

	
	static ArrayList<Crossroad> all_crossroads = new ArrayList<>();

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
				
		
		create_crossroad(2, 180.0, 180.0);
		create_crossroad(2, 60.0, 300.0);
		create_crossroad(2, 120.0, 240.0);
		
		create_crossroad(3, 60.0,180.0,120.0);
		create_crossroad(3, 60.0,120.0,180.0);
		create_crossroad(3, 60.0,60.0,240.0);
		create_crossroad(3, 180.0,120.0,60.0);
		create_crossroad(3, 60.0,120.0,180.0);
		
		
		City city = new City();
		Street_Network network = new Street_Network(0.5);
		all_crossroads.add(Street_Network.end_of_road);
		for (Crossroad c: all_crossroads) {
			c.get_viable_crossroads(all_crossroads);
			
		}
		
		
		Node node1 = (new Node(0, 0, true));
		network.nodes.add(node1);
		node1.crossroad = Street_Network.end_of_road;
		node1.angle = 270;
		
		Node node2 = new Node(-0.5, 0, true);
		network.nodes.add(node2);
		node2.crossroad = Street_Network.end_of_road;
		node2.angle = 90;
		
		
		Street s1 = create_street(node1, node2);
		
		for (Node n: network.nodes) {
				network.to_grow_nodes.add(n);
		}
		
		network.growthcenters.add(new Point(1,1));
		//network.grow_major_street(node1);
		
		
	
		
		try(Scanner s = new Scanner (System.in)){
			String c  =s.nextLine();
			while(true){
				if(!c.equals("end")){
					network.grow_major_streets();
					Simple_paint.paint(network);
					c=s.nextLine();
				}
				else
					break;
				
			}
		}
	}

	/**
	 * Create_street.
	 *
	 * @param n1 the n1
	 * @param n2 the n2	
	 * @return the street
	 */
	public static Street create_street(Node n1, Node n2){
		Street s = new Street(n1,n2,true);
		n1.streets.add(s);
		n2.streets.add(s);
		return s;
		
	}
	private static void create_crossroad(int n, double... angles){
		Crossroad c = new Crossroad(n, new ArrayList<>());
		for (int i = 0; i < angles.length; i++) {
			c.angles.add(angles[i]);
		}
		all_crossroads.addAll(c.get_all_rotations());
	}
	
}
