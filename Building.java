package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Reprezentuje obd�ln�kov� p�dorys budovy a jej� um�st�n� ve m�st�.
 */
public class Building implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7358710944312026111L;

	/** Body, kter�mi je budova tvo�ena. Ud�v� pouze tvar budovy. */
	public final ArrayList<Point> points;
	
	/** Absolutn� um�st�n� budovy v rovin�.*/
	ArrayList<Point> placement;
	
	/** Hranice budovy.*/
	ArrayList<Street> borders;
	
	/** Jm�no budovy. */
	private String name;
	
	/** Bod uprost�ed budovy. */
	public Point center;
	
	public double angle = 0;
	
	/**
	 * Konstruktor
	 *
	 * @param points seznam bod�, kter� ud�vaj� obd�ln�k.
	 * @param name jm�no budovy
	 */
	public Building (ArrayList<Point> points,String name){
		this.setName(name);
		this.points = points;
		place(new Point(0, 0));
	}
	public Building (double front_length, double side_length, String name){
		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));
		points.add(new Point(front_length, 0));
		points.add(new Point(front_length, side_length));
		points.add(new Point(0, side_length));
		this.points = points;
		this.name = name;
		place(new Point(0, 0));
	}
	
	/**
	 * Deafualtn� konstruktor - vytvo�� malou �tvercovou budovu.
	 */
	public Building() {
		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));
		points.add(new Point(10.0, 0));
		points.add(new Point(10.0, 10.0));
		points.add(new Point(0, 10));
		this.points = points;
		this.setName("New Building");
		place(new Point(0, 0));
	}

	/**
	 * Z bod� vytvo�� seznam ulic, kter� tvo�� hranice budovy a ulo�� je do atributu borders.
	 */
	private void create_borders(){
		borders = new ArrayList<>();
		Node first =  new Node(placement.get(0).getX(), placement.get(0).getY(), Street_type.lot_border,null);
		Node prev = first;
		for (int i = 1; i < points.size(); i++) {
			Node newnode =  new Node(placement.get(i).getX(), placement.get(i).getY(), Street_type.lot_border,null);
			borders.add(new Street(prev, newnode, Street_type.lot_border));
			prev = newnode;
		}
		borders.add(new Street(prev, first, Street_type.lot_border));
		double x = 0;
		double y = 0;
		for (int i=0; i<placement.size(); i++){
			x+= placement.get(i).getX();
			y+= placement.get(i).getY();
		}
		this.center = new Point(x/placement.size(), y/placement.size());
	}
	
	/**
	 * Um�st� budovu jej�m prvn� bodem do bodu point.
	 *
	 * @param point Bod, do kter�ho se m� budova um�stit.
	 */
	public void place(Point point){
		placement =  new ArrayList<>();
		for(Point p: points){
			placement.add(new Point(p.getX()+point.getX(), p.getY() + point.getY()));
		}
		create_borders();
	}
	
	/**
	 * Oto�� celou budovu o �hel angle ve sm�ru hodinov�ch ru�i�ek podle jej�ho prvn�ho bodu.
	 *
	 * @param angle the angle
	 */
	public void rotate(double angle){
		for (int i = 1; i < placement.size(); i++) {
				placement.set(i, RotatePoint(placement.get(i), placement.get(0), angle));
		}
		create_borders();
	}
	
	/**
	 *Provede rotaci bodu point podle bodu origin o �hel angle.
	 *
	 * @param point ot��en� bod
	 * @param origin bod, podle kter�ho se ot���
	 * @param angle �hel 
	 * @return the point oto�en� bod
	 */
	private Point RotatePoint(Point point, Point origin, double angle)
	{
		angle = angle*Math.PI/180;
		Point translated = point.minus(origin);
		Point rotated = new Point(translated.getX() * Math.cos(angle) - translated.getY() * Math.sin(angle), translated.getX() * Math.sin(angle) + translated.getY() * Math.cos(angle));
		return rotated.plus(origin);
	}
	
	
	/**
	 * Zkus� budovu um�stit rovnob�n� s ulic� street a to doprost�ed t�to ulice.  
	 *
	 * @param cp Pozemek, ve kter�m se m� budova nach�zet.
	 * @param street Ulice, podle kter� se m� budova um�stit.
	 * @param node1 Zda zkou��me um�stit budovu bl�e node1 nebo node2.
	 * @param rotation Zda budovu oto��me o 180 stup��.
	 * @param minus Zda budovu posouv�me sm�rem od ulice, nebo k ulici.
	 * @param settings Aktu�ln� parametry simulace.
	 * @return zda se poda�ilo budovu um�stit
	 */
	public boolean try_place_on_street(City_part cp, Street street, boolean node1, boolean rotation,boolean minus,Settings settings){
		
		
		Street small = borders.get(0);
		if(street.length < small.length){
			return false;
		}
		double ratio1 = (street.length/2 - small.length/2)/street.length;	
		double ratio2 = (street.length/2 + small.length/2)/street.length;
		if(node1){
			place(new Point(ratio1*street.node1.getPoint().getX() + ratio2*street.node2.getPoint().getX(), ratio1*street.node1.getPoint().getY() + ratio2*street.node2.getPoint().getY()));
		}
		else{
			place(new Point(ratio2*street.node1.getPoint().getX() + ratio1*street.node2.getPoint().getX(), ratio2*street.node1.getPoint().getY() + ratio1*street.node2.getPoint().getY()));
		}
		small = borders.get(0);
		if(rotation){
			rotate(180.0);
			this.angle += 180;
		}
		
		double angle = Math.min(Street.get_angle(street, small), (360+180-Street.get_angle(street, small))%360);
		rotate(angle);
		this.angle += angle;	
		move_away_from_street(street, settings.street_width/2 + settings.street_offset+0.1, minus);
		
		//System.out.println(street.length + "   " + street.major + center);
		boolean succes = control(cp,street,node1,settings);
		/*if(succes){
			for(Street s: cp.streets){
				if(s !=  street && s.major != Street_type.lot_border){
					move_away_from_street(s, settings.street_width/2 + settings.street_offset, true);
					succes = succes && control(cp,street,node1,settings);
					move_away_from_street(s, 2*(settings.street_width/2 + settings.street_offset), false);
					succes = succes && control(cp,street,node1,settings);
					move_away_from_street(s, settings.street_width/2 + settings.street_offset, true);
				}
			}
		}*/
		
		if(succes){

			return true;
		}
		else{
			place(new Point(0,0));
			this.angle = 0;
			return false;
		}
	}
	
	/**
	 * Zkontroluje, zda je budova spr�vn� um�st�na, tedy zda je uvnit� pozemku cp.
	 *
	 * @param cp pozemek, ve kter�m m� b�t budova um�st�na
	 * @return zda je budova spr�vn� um�st�na
	 */
	private boolean control(City_part cp,Street s,boolean node1,Settings settings){
		double street_distance = settings.street_width/2+settings.street_offset;
		if(cp.check_if_inside(new Node(center.getX(),center.getY(),null,null)) == Street_result.fail){
			return false;
		}
		for(Street s1: cp.streets){
			for(Street s2: borders){
				
				if(Street.getIntersection(s1, s2) != null){
					//System.out.println("prusecik s normalni ulici " + s1 + " " +s2);
					return false;
				}
				if(s1.major != Street_type.lot_border && Street.getIntersection(s1.get_parallel_street(street_distance, true), s2) != null){
					//System.out.println("prusecik s true ulici " + s1+ " " +s2 + " coz je " + s1.get_parallel_street(street_distance, true));
					return false;
				}
				if(s1.major != Street_type.lot_border && Street.getIntersection(s1.get_parallel_street(street_distance, false), s2) != null){
					//System.out.println("prusecik s false ulici " + s1+ " " +s2);
					return false;
				}
			}
		}

		return true;
	}
	
	
	/**
	 * Posune budovu o n�jakou vzd�lenost na jednu nebo druhou stranu kolmo k ulici street.
	 *
	 * @param street Ulice, podle kter� posouv�me.
	 * @param dist Vzd�lenost, o kterou posouv�me
	 * @param minus Na kterou stranu posouv�me
	 */
	private void move_away_from_street(Street street,double dist, boolean minus){
		double minus_one = 1;
		if(minus)
			minus_one = -1;
		Point vector = new Point(street.node1.getPoint().getX() - street.node2.getPoint().getX(), street.node1.getPoint().getY() - street.node2.getPoint().getY());
		vector = new Point(minus_one*dist*-1*vector.getY()/vector.norm(), dist*minus_one*vector.getX()/vector.norm());
		for(Point p: placement){
			p.setX(p.getX() + vector.getX());
			p.setY(p.getY() + vector.getY());
		}
		create_borders();
	}
	
	/**
	 * Zkop�ruje budovu.
	 *
	 * @return the building
	 */
	public Building copy(){
		return new Building(points,getName());
	}
	
	/**
	 * Spo��t� a vr�t� d�lku p�edn� strany budovy.
	 *
	 * @return the front length
	 */
	public double get_front_length(){
		return points.get(1).getX();
	}
	
	/**
	 * Spo��t� a vr�t� d�lku bo�n� strany budovy.
	 *
	 * @return the side length
	 */
	public double get_side_length(){
		return points.get(2).getY();
	}
	
	@Override
	public String toString(){
		return getName() + " " + points.get(0) + " " + points.get(1) + " " + points.get(2);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public double get_area(){
		return get_front_length()*get_side_length();
	}

}
