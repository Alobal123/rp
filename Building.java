package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Reprezentuje obd�ln�kov� p�dorys budovy a jej� um�st�n� ve m�st�.
 */
public class Building implements Serializable{

	/** Body, kter�mi je budova tvo�ena. Ud�v� pouze tvar budovy. */
	final ArrayList<Point> points;
	
	/** Absolutn� um�st�n� budovy v rovin�.*/
	ArrayList<Point> placement;
	
	/** Hranice budovy.*/
	ArrayList<Street> borders;
	
	/** Jm�no budovy. */
	String name;
	
	/** Bod uprost�ed budovy. */
	Point center;
	
	/**Plocha budovy*/
	double area;
	
	/**
	 * Konstruktor
	 *
	 * @param points seznam bod�, kter� ud�vaj� obd�ln�k.
	 * @param name jm�no budovy
	 */
	public Building (ArrayList<Point> points,String name){
		this.name = name;
		this.points = points;
		place(new Point(0, 0));
		find_area();
	}
	
	/**
	 * Deafualtn� konstruktor - vytvo�� malou �tvercovou budovu.
	 */
	public Building() {
		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));
		points.add(new Point(0, 0.05));
		points.add(new Point(0.05, 0.05));
		points.add(new Point(0.05, 0));
		this.points = points;
		this.name = "New Building";
		place(new Point(0, 0));
		find_area();
	}

	/**
	 * Z bod� vytvo�� seznam ulic, kter� tvo�� hranice budovy a ulo�� je do atributu borders.
	 */
	private void create_borders(){
		borders = new ArrayList<>();
		Node first =  new Node(placement.get(0).x, placement.get(0).y, Street_type.lot_border);
		Node prev = first;
		for (int i = 1; i < points.size(); i++) {
			Node newnode =  new Node(placement.get(i).x, placement.get(i).y, Street_type.lot_border);
			borders.add(new Street(prev, newnode, Street_type.lot_border));
			prev = newnode;
		}
		borders.add(new Street(prev, first, Street_type.lot_border));
		double x = 0;
		double y = 0;
		for (int i=0; i<placement.size(); i++){
			x+= placement.get(i).x;
			y+= placement.get(i).y;
		}
		this.center = new Point(x/placement.size(), y/placement.size());
	}
	
	/**
	 * Um�st� budovu jej�m prvn� bodem do bodu point.
	 *
	 * @param point Bod, do kter�ho se m� budova um�stit.
	 */
	public void place (Point point){
		placement =  new ArrayList<>();
		for(Point p: points){
			placement.add(new Point(p.x+point.x, p.y + point.y));
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
		Point rotated = new Point(translated.x * Math.cos(angle) - translated.y * Math.sin(angle), translated.x * Math.sin(angle) + translated.y * Math.cos(angle));
		return rotated.plus(origin);
	}
	
	/**
	 * Vypo��t� plochu budovy.
	 */
	public void find_area(){
		
		this.area =  get_front_length()*get_side_length();
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
			//System.out.println("Je vetsi nez ulice");
			return false;
		}
		double ratio1 = (street.length/2 - small.length/2)/street.length;	
		double ratio2 = (street.length/2 + small.length/2)/street.length;
		if(node1){
			place(new Point(ratio1*street.node1.point.x + ratio2*street.node2.point.x, ratio1*street.node1.point.y + ratio2*street.node2.point.y));
		}
		else{
			place(new Point(ratio2*street.node1.point.x + ratio1*street.node2.point.x, ratio2*street.node1.point.y + ratio1*street.node2.point.y));
		}
		small = borders.get(0);
		if(rotation){
			rotate(180.0);
		}
		
		double angle = Math.min(Street.get_angle(street, small), (360+180-Street.get_angle(street, small))%360);
		rotate(angle);
		move_away_from_street(street, settings.street_width + 0.001, minus);
		boolean succes = control(cp,street,node1);
		if(succes){
			for(Street s: cp.streets){
				if(s !=  street){
					move_away_from_street(s, settings.street_width, true);
					succes = succes && control(cp,street,node1);
					move_away_from_street(s, 2*settings.street_width, false);
					succes = succes && control(cp,street,node1);
					move_away_from_street(s, settings.street_width, true);
				}
			}
		}
		
		if(succes)
			return true;
		else{
			place(new Point(0,0));
			return false;
		}
	}
	
	/**
	 * Zkontroluje, zda je budova spr�vn� um�st�na, tedy zda je uvnit� pozemku cp.
	 *
	 * @param cp pozemek, ve kter�m m� b�t budova um�st�na
	 * @return zda je budova spr�vn� um�st�na
	 */
	private boolean control(City_part cp,Street s,boolean node1){
		boolean succes = false;
		if(cp.check_if_inside(new Node(center.x,center.y,null)) == Street_Result.not_altered){
			succes = true;
		}
		if(succes){
			outer:for(Street s1: cp.streets){
				for(Street s2: borders){
					if(Street.getIntersection(s1, s2) != null){
						succes = false;
						break outer;
					}
				}
			}
		}
		
		return succes;
	}
	
	
	/**
	 * Posune budovu o n�jakou vzd�lenost na jednu nebo druhou stranu kolmo k ulici street.
	 *
	 * @param street Ulice, podle kter� posouv�me.
	 * @param dist Vzd�lenost, o kterou posouv�me
	 * @param minus Na kterou stranu posouv�me
	 */
	public void move_away_from_street(Street street,double dist, boolean minus){
		double minus_one = 1;
		if(minus)
			minus_one = -1;
		Point vector = new Point(street.node1.point.x - street.node2.point.x, street.node1.point.y - street.node2.point.y);
		vector = new Point(minus_one*dist*-1*vector.y/vector.norm(), dist*minus_one*vector.x/vector.norm());
		//System.out.println(vector);
		for(Point p: placement){
			p.x+= vector.x;
			p.y+= vector.y;
		}
		create_borders();
	}
	
	/**
	 * Zkop�ruje budovu.
	 *
	 * @return the building
	 */
	public Building copy(){
		return new Building(points,name);
	}
	
	/**
	 * Spo��t� a vr�t� d�lku p�edn� strany budovy.
	 *
	 * @return the front length
	 */
	public double get_front_length(){
		return borders.get(0).length;
	}
	
	/**
	 * Spo��t� a vr�t� d�lku bo�n� strany budovy.
	 *
	 * @return the side length
	 */
	public double get_side_length(){
		return borders.get(1).length;
	}
	
	@Override
	public String toString(){
		return name + " " + points.get(0) + " " + points.get(1);
		
	}

	
}
