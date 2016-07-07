package krabec.citysimulator;

// TODO: Auto-generated Javadoc
/**
 * This class represents a single Street segment in our city. 
 * It connects two nodes.
 */
public class Street 
/**
	
*/
{
	/** The node1. */
	Node node1;
	
	/** The node2. */
	Node node2;
	
	/** If this street is already built or just planned. */
	boolean built; 
	
	/** If this street is major or minor. */
	boolean major;
	
	/** Width of the streets in meters. */
	double width;
	
	/**  Current value of traffic on this street. */
	double traffic;
	
	/**
	 * Instantiates a new street.
	 *
	 * @param node1 the node1
	 * @param node2 the node2
	 * @param major the major
	 */
	public Street(Node node1, Node node2, boolean major){
		this.node1 = node1;
		this.node2 = node2;
		this.major = major;
		
	}
	
	 /**
 	 * Given a node, which must be one of the nodes of this street,
 	 * it finds a street which interesects with this street at smallest angle at this node.
 	 * 
 	 * 
 	 * @param n Node from which we search for a street.
 	 * @param clockwise the clockwise
 	 * @return the _least_angled
 	 */
 	public Street get_least_angled(Node n, Boolean clockwise, Boolean major){
		 n.sort();
		 int index = n.streets.indexOf(this);
		 int count = n.streets.size();
		 
		 if(!major){
			 if(clockwise)
				 return (n.streets.get((index+count-1) % count));
			 else
				 return (n.streets.get((index+count+1) % count));
		 }
		 else{
			 
			 int summand = 1;
			 if(!clockwise)
				 summand = -1;
			 int counter = summand;
			 while(!(n.streets.get((index+count + counter) % count)).major){
				 counter += summand;
			 }
			 return n.streets.get((index+count + counter) % count);
		 }
		 
		 
	 }
	
	double get_length(){
		return Point.dist(node1.point, node2.point);
	}
 	
 	
	 /**
 	 * Returns an angle between two streets in degrees.
 	 *
 	 * @param s1 the s1
 	 * @param s2 the s2
 	 * 
 	 * @return the _angle
 	 */
 	static double get_angle (Street s1, Street s2){
		 	if(s1.node1 == s2.node1 || s1.node2 == s2.node2){
		 		Point v1 = s1.node1.point.minus(s1.node2.point);
		 		Point v2 = s2.node1.point.minus(s2.node2.point);
		 		return (360 + Point.angleBetween(v1, v2))%360;
		 	}
		 	else{
		 		Point v1 = s1.node1.point.minus(s1.node2.point);
		 		Point v2 = s2.node2.point.minus(s2.node1.point);
		 		return (360 + Point.angleBetween(v1, v2))%360;
		 	}
	 }
 	static double get_oriented_angle(Street s1, Street s2,Node node){
 		double as1 = s1.get_absolute_angle(node);
 		double as2 = s2.get_absolute_angle(node);
 		return (360 + (as2 -as1))%360;
 	}
	static Point getIntersection(Street s1,Street s2){
		double i_x,i_y;
		double p0_x =  s1.node2.point.x;
		double p0_y =  s1.node2.point.y;
		double p1_x =  s1.node1.point.x;
		double p1_y =  s1.node1.point.y;
		double p2_x =  s2.node2.point.x;
		double p2_y =  s2.node2.point.y;
		double p3_x =  s2.node1.point.x;
		double p3_y =  s2.node1.point.y;
		double s1_x, s1_y, s2_x, s2_y;
		s1_x = p1_x - p0_x;
		s1_y = p1_y - p0_y;
		s2_x = p3_x - p2_x;
		s2_y = p3_y - p2_y;
		double s,t;
		s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
		t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
		   if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
		    {
		        i_x = p0_x + (t * s1_x);
		        i_y = p0_y + (t * s1_y);
		        return new Point(i_x, i_y);
		    }
		return null;
	}
	
	double get_absolute_angle(Node node){
		double angle = 0;
		if(node == node1){
			angle = Point.angleBetween(this.node2.point.minus(this.node1.point), new Point(0,1));
		}
		else if (node == node2){
			angle = Point.angleBetween(this.node1.point.minus(this.node2.point), new Point(0,1));
		}
		
		return ( 360 + angle) % 360;
	}

	 
	 /* (non-Javadoc)
 	 * @see java.lang.Object#toString()
 	 */
 	@Override
	public String toString(){
		return "Street from (" + node1.point  +" to "  + node2.point;
		 
	 }
}
