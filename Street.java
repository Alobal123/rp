package krabec.citysimulator;

import java.io.Serializable;

/**
 * This class represents a single Street segment in our city. 
 * It connects two nodes.
 */
public class Street implements Serializable

{
	private static final long serialVersionUID = 8640165666271756086L;

	/** Prvn� uzel ze kter�ho ulice vych�z�. */
	public Node node1;
	
	/** Druh� uzel ze kter�ho ulice vych�z�. */
	public Node node2;
	
	/** Ud�v�, zda je ulice postavena, nebo jen napl�nov�na. */
	public boolean built; 
	
	/** Zda je tato ulice hlavn�, nebo vedlej��.*/
	Street_type major;
	
	/** Hodnota dopravy na t�to ulici. */
	double traffic;
	
	/**  D�lka ulice. */
	public double length;
	
	/**
	 * Konstruktor.
	 *
	 * @param node1 the node1
	 * @param node2 the node2
	 * @param major the major
	 */
	public Street(Node node1, Node node2, Street_type major){
		this.node1 = node1;
		this.node2 = node2;
		this.major = major;
		this.length = compute_length();
	}
	
	/**
	 * Konstruktor.
	 *
	 * @param node1 the node 1
	 * @param node2 the node 2
	 * @param major the major
	 * @param built the built
	 */
	public Street(Node node1, Node node2, Street_type major,boolean built){
		this.node1 = node1;
		this.node2 = node2;
		this.major = major;
		this.length = compute_length();
		this.built = built;
	}
	

 
	/**
	 * Vrac� druh� uzel ulice.
	 *
	 * @param node Prvn� uzel 
	 * @return Druh� uzel
	 */
	public Node get_other_node(Node node){
			
		if(node1 == node)
			return node2;
		if(node2 == node)
			return node1;
		System.out.println("Vracim null");
		return null;
	}
	public Node get_center(){
		return new Node((node1.getPoint().getX() + node2.getPoint().getX())/2,
				(node1.getPoint().getY() + node2.getPoint().getY())/2, null,null);
	}
	
	/**
	 * Vypo��t� d�lku ulice.
	 *
	 * @return D�lka ulice
	 */
	public double compute_length(){
		return Point.dist(node1.getPoint(), node2.getPoint());
	}
 	
 	
	 /**
 	 * Vrac� �hel mezi dv�ma ulicemi ve stupn�ch
 	 *
 	 * @param s1 Ulice s1
 	 * @param s2 Ulice s2
 	 * 
 	 * @return �hel
 	 */
 	static double get_angle (Street s1, Street s2){
		 	if(s1.node1 == s2.node1 || s1.node2 == s2.node2){
		 		Point v1 = s1.node1.getPoint().minus(s1.node2.getPoint());
		 		Point v2 = s2.node1.getPoint().minus(s2.node2.getPoint());
		 		return (360 + Point.angleBetween(v1, v2))%360;
		 	}
		 	else{
		 		Point v1 = s1.node1.getPoint().minus(s1.node2.getPoint());
		 		Point v2 = s2.node2.getPoint().minus(s2.node1.getPoint());
		 		return (360 + Point.angleBetween(v1, v2))%360;
		 	}
	 }
 	
	 /**
	  * Vrac� orientovan� �hel mezi dv�ma ulicemi. Je to �hel ve sm�ru hodinov�ch ru�i�ek.
	  *
 	 * @param s1 Ulice s1
 	 * @param s2 Ulice s2
 	 * @param node Uzel spole�n� s1 a s2
 	 * @return �hel
	  */
	 static double get_oriented_angle(Street s1, Street s2,Node node){
 		double as1 = s1.get_absolute_angle(node);
 		double as2 = s2.get_absolute_angle(node);
 		return (360 + (as2 -as1))%360;
 	}
	
	/**
	 * Vypo��t� pr�se��k dvou ulic. Pokud se neprot�naj� vrac� null.
	 *
 	 * @param s1 Ulice s1
 	 * @param s2 Ulice s2
	 * @return Bod pr�se��ku
	 */
	static Point getIntersection(Street s1,Street s2){
		double i_x,i_y;
		double p0_x =  s1.node2.getPoint().getX();
		double p0_y =  s1.node2.getPoint().getY();
		double p1_x =  s1.node1.getPoint().getX();
		double p1_y =  s1.node1.getPoint().getY();
		double p2_x =  s2.node2.getPoint().getX();
		double p2_y =  s2.node2.getPoint().getY();
		double p3_x =  s2.node1.getPoint().getX();
		double p3_y =  s2.node1.getPoint().getY();
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
	
	/**
	 * Vr�t� �hel ulice vzhledem k uzlu.
	 *
	 * @param node Uzel
	 * @return �hel
	 */
	public double get_absolute_angle(Node node){
		double angle = 0;
		if(node == node1){
			angle = Point.angleBetween(this.node2.getPoint().minus(this.node1.getPoint()), new Point(0,1));
		}
		else if (node == node2){
			angle = Point.angleBetween(this.node1.getPoint().minus(this.node2.getPoint()), new Point(0,1));
		}
		
		return ( 360 + angle) % 360;
	}

	public Street get_parallel_street(double distance, boolean minus){
		Point vector = node1.getPoint().minus(node2.getPoint());
		Point perpendicular;
		if(minus)
			perpendicular = new Point(-1*vector.getY(), vector.getX());
		else
			perpendicular = new Point(vector.getY(), -1*vector.getX());
		
		perpendicular = new Point(perpendicular.getX()/perpendicular.norm(), perpendicular.getY()/perpendicular.norm());
		
		Node new_node1 = new Node(node1.getPoint().getX() + distance*perpendicular.getX(), node1.getPoint().getY() + distance*perpendicular.getY(), null,null);
		Node new_node2 = new Node(node2.getPoint().getX() + distance*perpendicular.getX(), node2.getPoint().getY() + distance*perpendicular.getY(), null,null);
		return new Street(new_node1, new_node2, null);
	}
	 
 	@Override
	public String toString(){
		return "Street from (" + node1.getPoint()  +" to "  + node2.getPoint();
		 
	 }

	public static boolean are_parallel(Street intersecting, Street longest) {
		Point vector1 = intersecting.node1.getPoint().minus(intersecting.node2.getPoint());
		Point vector2 = longest.node1.getPoint().minus(longest.node2.getPoint());
		vector1 = new Point(vector1.getX()/vector1.norm(), vector1.getY()/vector1.norm());
		vector2 = new Point(vector2.getX()/vector2.norm(), vector2.getY()/vector2.norm());
		if(Math.abs(vector1.getX()-vector2.getX())<0.01 && Math.abs(vector1.getY()-vector2.getY())<0.01)
			return true;
		if(Math.abs(vector1.getX()+vector2.getX())<0.01 && Math.abs(vector1.getY()+vector2.getY())<0.01)
			return true;
		return false;
	}
}
