package krabec.citysimulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * This class represents a point in euclidian plane.
 * It contains some functions as counting angles or distances in plane.
 */
public class Point {
	
	/** The x. */
	double x;
	
	/** The y. */
	double y;
	
	/**
	 * Instantiates a new point.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public Point (double x, double y){
		this.x=x;
		this.y=y;
	}
	
	/**
	 * Minus.
	 *
	 * @param p the p
	 * @return the point
	 */
	public Point minus (Point p){
		return new Point(this.x - p.x,this.y - p.y);
	}
	
	/**
	 * Angle between.
	 *
	 * @param point1 the point1
	 * @param point2 the point2
	 * @return the double
	 */
	public static double angleBetween(Point point1, Point point2)
	{
	    double sin = point1.x * point2.y - point2.x * point1.y;  
	    double cos = point1.x * point2.x + point1.y * point2.y;

	    return Math.atan2(sin, cos) * (180 / Math.PI);
	}
	
	
	/**
	 * Dist.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	static double dist(Point a, Point b){
		
		return (Math.sqrt(Math.pow((a.x-b.x),2) + Math.pow((a.y-b.y),2)));
	}
	
	/**
	 * Norm.
	 *
	 * @return the double
	 */
	double norm (){
		return Math.sqrt(x*x + y * y);
	}
	
	/**
	 * Gets the _smallest_distance.
	 *
	 * @param a the a
	 * @param list the list
	 * @return the _smallest_distance
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
	@Override
	public String toString(){
		double rx = round(x,3);
		double ry = round(y,3);
		return "(" + rx + " , " + ry + ")";
	}
	
	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	
	
}
