package krabec.citysimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// TODO: Auto-generated Javadoc
/**
 * The Class Node.
 */
public class Node {
	
	
	class NodeComparator implements Comparator<Street>{
		
		Node node;
		public NodeComparator(Node node) {
			this.node = node;
		}
		
		@Override
		public int compare(Street s1, Street s2) {
			if(s1.get_absolute_angle(node) < s2.get_absolute_angle(node)){
				return -1;
			}
			else
				return 1;
		}
		
	}
	
	/** The point. */
	Point point;
	
	/** The major. */
	boolean major;
	
	/** The finished. */
	boolean finished;
	
	Crossroad crossroad;
	double angle;
	
	/** The streets. */
	ArrayList<Street> streets = new ArrayList<>();
	
	
	/**
	 * Instantiates a new node.
	 *
	 * @param x the x
	 * @param y the y
	 * @param major the major
	 */
	public Node(double x,double y, boolean major){
		this.point = new Point(x,y);
		this.major = major;
		this.crossroad = Street_Network.end_of_road;
 	}
	
	

	public double compute_angle(){
		if(streets.size() == 1){
			return (streets.get(0).get_absolute_angle(this)) % 360;
		}
		else{
			NodeComparator nc = new NodeComparator(this);
			Collections.sort(streets,nc);
			for (int i = 0; i < this.crossroad.number_of_roads; i++) {
				int same_angles = 0;
				for (int j = 0; j < this.crossroad.number_of_roads; j++) {
						
					double angle_between_streets = (360 + Street.get_oriented_angle(streets.get((i+j)%crossroad.number_of_roads),
																	streets.get((i+j+1)%crossroad.number_of_roads),this)) %360;
					//System.out.println("absolute angle" + angle_between_streets);
					if((Math.abs(angle_between_streets - crossroad.angles.get(j)) < 0.0001)){
						same_angles++;
					}	
					
				}
				if(same_angles == crossroad.number_of_roads){
					return (streets.get(i).get_absolute_angle(this));
				}
			}
			
		}
		
		System.out.println("Something is wrong");
		return -5;
	}
	
	@Override
	public String toString(){
		return "Node at: (" + (int)(point.x*1000)/1000.0 + "," + (int)(point.y*1000)/1000.0 + ")";
	}
	
}
