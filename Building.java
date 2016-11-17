package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Reprezentuje obdélníkový pùdorys budovy a její umístìní ve mìstì.
 */
public class Building implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7358710944312026111L;

	/** Body, kterými je budova tvoøena. Udává pouze tvar budovy. */
	public final ArrayList<Point> points;
	
	/** Absolutní umístìní budovy v rovinì.*/
	ArrayList<Point> placement;
	
	/** Hranice budovy.*/
	ArrayList<Street> borders;
	
	/** Jméno budovy. */
	private String name;
	
	/** Bod uprostøed budovy. */
	Point center;
	
	double angle = 0;
	
	/**
	 * Konstruktor
	 *
	 * @param points seznam bodù, které udávají obdélník.
	 * @param name jméno budovy
	 */
	public Building (ArrayList<Point> points,String name){
		this.setName(name);
		this.points = points;
		place(new Point(0, 0));
	}
	
	/**
	 * Deafualtní konstruktor - vytvoøí malou ètvercovou budovu.
	 */
	public Building() {
		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));
		points.add(new Point(0, 0.05));
		points.add(new Point(0.05, 0.05));
		points.add(new Point(0.05, 0));
		this.points = points;
		this.setName("New Building");
		place(new Point(0, 0));
	}

	/**
	 * Z bodù vytvoøí seznam ulic, které tvoøí hranice budovy a uloží je do atributu borders.
	 */
	private void create_borders(){
		borders = new ArrayList<>();
		Node first =  new Node(placement.get(0).getX(), placement.get(0).getY(), Street_type.lot_border);
		Node prev = first;
		for (int i = 1; i < points.size(); i++) {
			Node newnode =  new Node(placement.get(i).getX(), placement.get(i).getY(), Street_type.lot_border);
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
	 * Umístí budovu jejím první bodem do bodu point.
	 *
	 * @param point Bod, do kterého se má budova umístit.
	 */
	public void place (Point point){
		placement =  new ArrayList<>();
		for(Point p: points){
			placement.add(new Point(p.getX()+point.getX(), p.getY() + point.getY()));
		}
		create_borders();
	}
	
	/**
	 * Otoèí celou budovu o úhel angle ve smìru hodinových ruèièek podle jejího prvního bodu.
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
	 *Provede rotaci bodu point podle bodu origin o úhel angle.
	 *
	 * @param point otáèený bod
	 * @param origin bod, podle kterého se otáèí
	 * @param angle úhel 
	 * @return the point otoèený bod
	 */
	private Point RotatePoint(Point point, Point origin, double angle)
	{
		angle = angle*Math.PI/180;
		Point translated = point.minus(origin);
		Point rotated = new Point(translated.getX() * Math.cos(angle) - translated.getY() * Math.sin(angle), translated.getX() * Math.sin(angle) + translated.getY() * Math.cos(angle));
		return rotated.plus(origin);
	}
	
	
	/**
	 * Zkusí budovu umístit rovnobìžnì s ulicí street a to doprostøed této ulice.  
	 *
	 * @param cp Pozemek, ve kterém se má budova nacházet.
	 * @param street Ulice, podle které se má budova umístit.
	 * @param node1 Zda zkoušíme umístit budovu blíže node1 nebo node2.
	 * @param rotation Zda budovu otoèíme o 180 stupòù.
	 * @param minus Zda budovu posouváme smìrem od ulice, nebo k ulici.
	 * @param settings Aktuální parametry simulace.
	 * @return zda se podaøilo budovu umístit
	 */
	public boolean try_place_on_street(City_part cp, Street street, boolean node1, boolean rotation,boolean minus,Settings settings){
		
		Street small = borders.get(0);
		if(street.length < small.length){
			return false;
		}
		double ratio1 = (street.length/2 - small.length/2)/street.length;	
		double ratio2 = (street.length/2 + small.length/2)/street.length;
		if(node1){
			place(new Point(ratio1*street.node1.point.getX() + ratio2*street.node2.point.getX(), ratio1*street.node1.point.getY() + ratio2*street.node2.point.getY()));
		}
		else{
			place(new Point(ratio2*street.node1.point.getX() + ratio1*street.node2.point.getX(), ratio2*street.node1.point.getY() + ratio1*street.node2.point.getY()));
		}
		small = borders.get(0);
		if(rotation){
			rotate(180.0);
			this.angle += 180;
		}
		
		double angle = Math.min(Street.get_angle(street, small), (360+180-Street.get_angle(street, small))%360);
		rotate(angle);
		this.angle += angle;	
		move_away_from_street(street, settings.street_width/2 + 0.001, minus);
		boolean succes = control(cp,street,node1);
		if(succes){
			for(Street s: cp.streets){
				if(s !=  street){
					move_away_from_street(s, settings.street_width/2, true);
					succes = succes && control(cp,street,node1);
					move_away_from_street(s, 2*settings.street_width/2, false);
					succes = succes && control(cp,street,node1);
					move_away_from_street(s, settings.street_width/2, true);
				}
			}
		}
		
		if(succes)
			return true;
		else{
			place(new Point(0,0));
			this.angle = 0;
			return false;
		}
	}
	
	/**
	 * Zkontroluje, zda je budova správnì umístìna, tedy zda je uvnitø pozemku cp.
	 *
	 * @param cp pozemek, ve kterém má být budova umístìna
	 * @return zda je budova správnì umístìna
	 */
	private boolean control(City_part cp,Street s,boolean node1){
		boolean succes = false;
		if(cp.check_if_inside(new Node(center.getX(),center.getY(),null)) == Street_Result.not_altered){
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
	 * Posune budovu o nìjakou vzdálenost na jednu nebo druhou stranu kolmo k ulici street.
	 *
	 * @param street Ulice, podle které posouváme.
	 * @param dist Vzdálenost, o kterou posouváme
	 * @param minus Na kterou stranu posouváme
	 */
	public void move_away_from_street(Street street,double dist, boolean minus){
		double minus_one = 1;
		if(minus)
			minus_one = -1;
		Point vector = new Point(street.node1.point.getX() - street.node2.point.getX(), street.node1.point.getY() - street.node2.point.getY());
		vector = new Point(minus_one*dist*-1*vector.getY()/vector.norm(), dist*minus_one*vector.getX()/vector.norm());
		//System.out.println(vector);
		for(Point p: placement){
			p.setX(p.getX() + vector.getX());
			p.setY(p.getY() + vector.getY());
		}
		create_borders();
	}
	
	/**
	 * Zkopíruje budovu.
	 *
	 * @return the building
	 */
	public Building copy(){
		return new Building(points,getName());
	}
	
	/**
	 * Spoèítá a vrátí délku pøední strany budovy.
	 *
	 * @return the front length
	 */
	public double get_front_length(){
		return borders.get(0).length;
	}
	
	/**
	 * Spoèítá a vrátí délku boèní strany budovy.
	 *
	 * @return the side length
	 */
	public double get_side_length(){
		return borders.get(1).length;
	}
	
	@Override
	public String toString(){
		return getName() + " " + points.get(0) + " " + points.get(1);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public double getArea(){
		return get_front_length()*get_side_length();
	}

	
}
