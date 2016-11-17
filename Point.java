package krabec.citysimulator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 *Bod v rovinì.
 */
public class Point implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8151971964805701305L;

	/** x-ová souøadnice bodu */
	private double x;
	
	/** z-ová souøadnice bodu */
	private double y;
	
	/**
	 * Konstruktor
	 *
	 * @param x the x
	 * @param y the y
	 */
	public Point (double x, double y){
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * Odeète od tohot bodu jiný bod (po složkách).
	 *
	 * @param p Bod p
	 * @return Rozdíl
	 */
	public Point minus (Point p){
		return new Point(this.getX() - p.getX(),this.getY() - p.getY());
	}
	public Point plus (Point p){
		return new Point(this.getX() + p.getX(),this.getY() + p.getY());
	}
	
	public static double dot(Point p1,Point p2){
		return p1.getX()*p2.getX() + p1.getY()*p2.getY();
	}
	
	/**
	 * Vypoèítá úhel mezi dvìma body. Body v tomto kontextu chápeme jako vektory.
	 *
	 * @param point1 Bod1
	 * @param point2 Bod2
	 * @return Úhel
	 */
	public static double angleBetween(Point point1, Point point2)
	{
	    double sin = point1.getX() * point2.getY() - point2.getX() * point1.getY();  
	    double cos = point1.getX() * point2.getX() + point1.getY() * point2.getY();

	    return Math.atan2(sin, cos) * (180 / Math.PI);
	}
	
	public double norm(){
		return Math.sqrt(getX()*getX()+getY()*getY());
	}
	
	/**
	 * Vypoèítá vzdálenost dvou bodù.
	 *
	 * @param a Bod a
	 * @param b Bod b
	 * @return Vzdálenost
	 */
	static double dist(Point a, Point b){
		
		return (Math.sqrt(Math.pow((a.getX()-b.getX()),2) + Math.pow((a.getY()-b.getY()),2)));
	}
	
	
	/**
	 * Najde nejbližší bod k danému bodu ze seznamu bodù a vrátí jeho vzdálenost.
	 *
	 * @param a Bod a
	 * @param list Seznam bodù.
	 * @return Vzdálenost
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
		double rx = round(getX(),3);
		double ry = round(getY(),3);
		return "(" + rx + " , " + ry + ")";
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	
	
}
