package krabec.citysimulator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 *Bod v rovin�.
 */
public class Point implements Serializable{
	
	/** x-ov� sou�adnice bodu */
	double x;
	
	/** z-ov� sou�adnice bodu */
	double y;
	
	/**
	 * Konstruktor
	 *
	 * @param x the x
	 * @param y the y
	 */
	public Point (double x, double y){
		this.x=x;
		this.y=y;
	}
	
	/**
	 * Ode�te od tohot bodu jin� bod (po slo�k�ch).
	 *
	 * @param p Bod p
	 * @return Rozd�l
	 */
	public Point minus (Point p){
		return new Point(this.x - p.x,this.y - p.y);
	}
	public Point plus (Point p){
		return new Point(this.x + p.x,this.y + p.y);
	}
	
	public static double dot(Point p1,Point p2){
		return p1.x*p2.x + p1.y*p2.y;
	}
	
	/**
	 * Vypo��t� �hel mezi dv�ma body. Body v tomto kontextu ch�peme jako vektory.
	 *
	 * @param point1 Bod1
	 * @param point2 Bod2
	 * @return �hel
	 */
	public static double angleBetween(Point point1, Point point2)
	{
	    double sin = point1.x * point2.y - point2.x * point1.y;  
	    double cos = point1.x * point2.x + point1.y * point2.y;

	    return Math.atan2(sin, cos) * (180 / Math.PI);
	}
	
	public double norm(){
		return Math.sqrt(x*x+y*y);
	}
	
	/**
	 * Vypo��t� vzd�lenost dvou bod�.
	 *
	 * @param a Bod a
	 * @param b Bod b
	 * @return Vzd�lenost
	 */
	static double dist(Point a, Point b){
		
		return (Math.sqrt(Math.pow((a.x-b.x),2) + Math.pow((a.y-b.y),2)));
	}
	
	
	/**
	 * Najde nejbli��� bod k dan�mu bodu ze seznamu bod� a vr�t� jeho vzd�lenost.
	 *
	 * @param a Bod a
	 * @param list Seznam bod�.
	 * @return Vzd�lenost
	 */
	static double get_smallest_distance(Point a, List<Point> list){
		double minval= Double.MAX_VALUE;
		for (int j = 0; j < list.size(); j++) {
			if(!a.equals(list.get(j)) && Point.dist(a, list.get(j))<minval){
				minval = Point.dist(a, list.get(j));
			}
		}
		return minval;
	}
	
	static Point get_closest(Point a, List<Point> list){
		double minval= Double.MAX_VALUE;
		Point best = null;
		for (int j = 0; j < list.size(); j++) {
			if(!a.equals(list.get(j)) && Point.dist(a, list.get(j))<minval){
				minval = Point.dist(a, list.get(j));
				best = list.get(j);
			}
		}
		return best;
	}
	
	@Override
	public String toString(){
		double rx = round(x,3);
		double ry = round(y,3);
		return "(" + rx + " , " + ry + ")";
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	
	
}
