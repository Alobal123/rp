package krabec.citysimulator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Tøída reprezentující uzel v grafu ulic, typicky tedy køižovatku.
 */
public class Node implements Serializable, Comparable<Object>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5532262799594332955L;

	/**
	 * Tøída sloužící ke tøídìní ulic, podle úhlu, jaký svírají s osou y ve smìru hodinových ruèièek.
	 */
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
	
	/**Bod v rovinì, urèije polohu uzlu.*/
	private Point point;
	
	/** Urèuje, zda je tento uzel hlavní. */
	Street_type major;
	
	/** Urèuje, zda je tento uzel postavený. */
	private boolean built;
	
	/** Poèet obyvatel v bydlících v tomto uzlu. */
	double residents = 1;
	
	/** Vzdálenost - slouží k výpoètu nejkratších cest ve tøídì {@link Node_Distance} */
	double distance;
	
	/** Typ køižovatky, která odpovídá upsoøádání ulic v tomto uzlu.*/
	public Crossroad crossroad;
	
	/** Úhel o jaký je køižovatka v tomto uzlu natoèená vzhledem k ose y ve smìru hodinových ruèièek. */
	public double angle;
	
	/**Seznam ulic vedoucích z tohoto uzlu.*/
	public ArrayList<Street> streets = new ArrayList<>();
	
	/**Seznam blokù, na jejichž hranici se tento uzel vyskytuje. */
	ArrayList<Block> blocks = new ArrayList<>();
	
	/** Seznam všech tripù zaèínajích v tomto uzlu. */
	ArrayList<Trip> trips = new ArrayList<>();
	
	/**
	 * Konstruktor
	 *
	 * @param x the x
	 * @param y the y
	 * @param major the major
	 */
	public Node(double x,double y, Street_type major,Crossroad crossroad){
		this.point = (new Point(x,y));
		this.major = major;
		this.crossroad = crossroad;
 	}
	
	/**
	 * Konstruktor
	 *
	 * @param x the x
	 * @param y the y
	 * @param major the major
	 * @param built the built
	 */
	public Node(double x,double y, Street_type major,boolean built,Crossroad crossroad){
		this.point = (new Point(x,y));
		this.major = major;
		this.setBuilt(built);
		this.crossroad = crossroad;
 	}
	
	/**
	 * Odebere dopravu ze všech tripù zaèínajících v tomto uzlu a poté je smaže.
	 *
	 * @param nd the nd
	 */
	void remove_trips(Node_Distance nd){
		for(Trip t: trips){
			t.remove_traffic(nd);
		}
		trips = new ArrayList<>();
	}
	

	/**
	 * Vypoèítá úhel o jaký je køižovatka v tomto uzlu natoèená vzhledem k ose y ve smìru hodinových ruèièek.
	 *
	 * @return Úhel uzlu
	 */
	double compute_angle(){
		if(streets.size() != crossroad.getNumber_of_roads()){
			return 0;
			
		}
		if(streets.size() == 1){
			return (streets.get(0).get_absolute_angle(this)) % 360;
		}
		else{
			this.sort_streets_in_this_node();
			for (int i = 0; i < this.crossroad.getNumber_of_roads(); i++) {
				int same_angles = 0;
				for (int j = 0; j < this.crossroad.getNumber_of_roads(); j++) {
						
					double angle_between_streets = (360 + Street.get_oriented_angle(streets.get((i+j)%crossroad.getNumber_of_roads()),
																	streets.get((i+j+1)%crossroad.getNumber_of_roads()),this)) %360;
					if((Math.abs(angle_between_streets - crossroad.angles.get(j)) < 0.00001)){
						same_angles++;
					}	
				}
				if(same_angles == crossroad.getNumber_of_roads()){
					return (streets.get(i).get_absolute_angle(this));
				}
			}
			
		}
		return 0;
	}
	
	/**
	 * Setøídí ulice tohoto uzlu.
	 */
	void sort_streets_in_this_node(){
		NodeComparator nc = new NodeComparator(this);
		Collections.sort(streets,nc);
	}
	
	/**
	 * Urèí vzdálenost od ulice v parametru.
	 *
	 * @param street Ulice
	 * @return Vzdálenost od ulice.
	 */
	double compute_distance_from_street(Street street){	
		Point v = street.node1.getPoint();
		Point w = street.node2.getPoint();
		Point p = this.getPoint();
		Point direction = v.minus(w);
		double length_squared = Math.abs(Point.dot(direction,direction));
		double t = Math.max(0, Math.min(1, Point.dot(p.minus(v),w.minus(v))/length_squared));
		Point projection = v.plus(new Point(w.minus(v).getX()*t,w.minus(v).getY()*t));
		return Point.dist(projection, p);
	}
	
	
	@Override
	public String toString(){
		return "Node at: (" + (int)(getPoint().getX()*1000)/1000.0 + "," + (int)(getPoint().getY()*1000)/1000.0 + ")";
	}
	
	/**
	 * Odstraní ulici do vedoucí z tohoto uzlu do uzlu v parametru.
	 *
	 * @param node Uzel
	 */
	void remove_street_to_node(Node node){
		Street to_remove = null;
		for (Street s: streets) {
			if(s.node1 ==node || s.node2 ==node)
				to_remove = s;
		}
		streets.remove(to_remove);
	}
	/**
	 * Vytvoøí nový uzel pod daným úhlem a danou délkou ulice z daného uzlu.
	 *
	 * @param angle Úhel nové ulice
	 * @param major Zda stavíme hlavní ulici
	 * @param oldnode Starý uzel - ze kterého vedeme novou ulici
	 * @param length Vzdálenost novì vytvoøeného uzlu od starého
	 * @return Novì vytvoøený uzel
	 */
	static Node make_new_node(double angle, Street_type major, Node oldnode, double length,Crossroad crossroad){
		double dx = Math.sin(angle * Math.PI / 180) * length;
		double dy = Math.cos(angle * Math.PI / 180) * length;
		return new Node(oldnode.getPoint().getX() + dx, oldnode.getPoint().getY() + dy, major,crossroad);
	}
	
	
	/**
	 * Zkopíruje uzel ale jen s tìmi ulicemi, které náleží do bloku.
	 * @param block
	 * @return
	 */
	Node copy_node_with_streets_from_block (Block block){
		Node newnode = new Node(this.getPoint().getX(), this.getPoint().getY(), this.major,this.crossroad);
		newnode.setBuilt(this.isBuilt());
		for(Street s: this.streets){
			if(block.check_if_inside(s.node1) == Street_result.not_altered &&
					block.check_if_inside(s.node2) == Street_result.not_altered &&
					block.check_if_inside(s.get_center()) == Street_result.not_altered){
				newnode.streets.add(s);
			}	
		}
		return newnode;
	}
	

	
	/*@Override
	public boolean equals(Object o){
		Node n;
		if(o instanceof Node)
			n = (Node) o;
		else
			return false;
		if(Point.dist(this.getPoint(), n.getPoint())<0.0001){
			if(o != this){
				System.out.println(Point.dist(this.getPoint(), n.getPoint()));
				throw new IndexOutOfBoundsException();
			}
			return true;
		}
		return false;
	}*/

	@Override	
	public int compareTo(Object o) {
		Node n = (Node) o;
		if(getPoint().getX()>n.getPoint().getX())
			return 1;
		else if(getPoint().getX()==n.getPoint().getX())
			return 0;
		else 
			return -1;
		
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public boolean isBuilt() {
		return built;
	}

	public void setBuilt(boolean built) {
		this.built = built;
	}
}
