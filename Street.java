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
 	public Street get_least_angled(Node n, Boolean clockwise){
		 double min = 500;
		 
		 Street rt = null;
		 int cc = 1;
		 if (!clockwise)
			 cc = -1;
		 for (Street s: n.streets) {
			 double angle = cc*get_angle(this,s);
			 if( s!= this && angle>0 && angle < min){
				 rt = s;
				 min = angle;
			 }

		 }
		 if(rt == null){
			 min = 500;
			
			 for (Street s: n.streets) {
				 double angle = cc* get_angle(this,s);
				 if(s != this && angle<0 && -1*angle < min){
					 rt = s;
					 min =  -1*angle;
				 } 
			 }
		 }
		 return rt;
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
			
	
	public double get_absolute_angle(Node node){
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
		return "Street from (" + node1.point.x + " , " + node1.point.y + ") to (" + node2.point.x + " , " + node2.point.y + ")";
		 
	 }
}
