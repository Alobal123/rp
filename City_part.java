package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Mìstská èást - je nìjaká èást mìsta ohranièena ulicemi, tedy stìna grafu tvoøeného ulicemi a køižovatkami.
 * Jejími potomky jsou tøídy Quarter a Block. 
 * {@link Quarter} je ohranièena pouze hlavními ulicemi, zatímco {@link Block} i tìmi vedlejšími.
 */
public abstract class City_part implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7374818903326872052L;
	/** Seznam ulic ohranièujích tuto mìstskou èást. */
	List<Street> streets = new ArrayList<Street>();
	public List<City_part> contained_city_parts = new ArrayList<>();
	
	/**První uzel, tj. uzel ze kterého vychází první ulice v atributu streets. */
	Node firstnode;
	
	/** Land_use_type této èásti mìsta. */
	public Lut lut;
	
	/** Lokální hodnota této èásti mìsta. */
	double value;
	
	/** Výmìra, neboli plocha, kterou tato èást mìsta zabírá. */
	double area;
	
	/** Urèuje, zda je tato èást mìsta postavena, nebo zatím ne. */
	boolean built;
	
	/** Tìžištì, tedy bod zhruba ve støedu této mìstské èásti. */
	Point center;
	
	/**
	 * Vrací množinu všech uzlù, které jsou na hranici této mìstské èásti.
	 *
	 * @return Množina uzlù
	 */
	public LinkedHashSet<Node> get_nodes_once(){
		LinkedHashSet<Node> nodes =  new LinkedHashSet<>();
		for (Street street: streets) {
			nodes.add(street.node1);
			nodes.add(street.node2);
		}
		return nodes;		
	}

	/**
	 * Vrací seznam uzlù na hranici této mìstské èásti, tak jak jdou za sebou.
	 *
	 * @return Seznam uzlù
	 */
	public ArrayList<Node> get_nodes(){
		ArrayList<Node> points = new ArrayList<>();
		Street prev = null;
		for (Street s: streets) {
			
			if(points.size() == 0){
				points.add(s.get_other_node(firstnode));
				points.add(firstnode);
			}
			else {
				if(prev == s){
					int count = 0;
					for(Street street: s.node1.streets){
						if(street.major==Street_type.major || this instanceof Block || this instanceof Lot)
							count++;
					}
					if(count == 1)
						points.add(s.node2);
					else
						points.add(s.node1);
				}
				else if(s.node1 == prev.node1 || s.node1 == prev.node2){
					points.add(s.node2);
				}
				else if (s.node2 == prev.node1 || s.node2 == prev.node2){
					points.add(s.node1);

				}

			}
			prev = s;
		}	
		return points;
		
		
	}
	
	
	/**
	 * Vrací seznam bodù, které odpovídají uzlù, na hranici této mìstské èásti.
	 *
	 * @return Seznam bodù
	 */
	public ArrayList<Point>  get_points(){
		ArrayList<Point> points = new ArrayList<>();
		Street prev = null;

		for (Street s: streets) {
			
			if(points.size() == 0){
				points.add(s.get_other_node(firstnode).getPoint());
				points.add(firstnode.getPoint());
				
			}
			else {
				if(prev == s){
					int count = 0;
					for(Street street: s.node1.streets){
						if(street.major==Street_type.major || this instanceof Block || this instanceof Lot)
							count++;
					}
					if(count == 1)
						points.add(s.node2.getPoint());
					else
						points.add(s.node1.getPoint());
				}
				else if(s.node1 == prev.node1 || s.node1 == prev.node2){
					points.add(s.node2.getPoint());
				}
				else if (s.node2 == prev.node1 || s.node2 == prev.node2){
					points.add(s.node1.getPoint());

				}
			}
			prev = s;
		}	
		return points;
	}
	/**
	 * Vrací seznam uzlù ležících uvnitø této ètvrti.
	 * @return
	 */
	public ArrayList<Node> filter_nodes_outside_this_quarter(List<Node> nodes){
		ArrayList<Node> filtered = new ArrayList<>();
		for(Node n: nodes){
			if(check_if_inside(n) != Street_result.fail)
				filtered.add(n);
		}
		return filtered;
	}
	
	/**
	 * Vypoèítá obsah mnohoúhelníku daného touto mìstskou èástí.
	 */
	public void compute_area(){
		ArrayList<Point> points = get_points();
		double x = 0;
		double y = 0;
		
		double area = 0;     
		int j;
		int n = points.size()-1;
		for (int i=0; i<n; i++){
			j = (i+1)%n;
			area += points.get(i).getX() * points.get(j).getY();
			area -= points.get(j).getX() * points.get(i).getY();
			x+= points.get(i).getX();
			y+= points.get(i).getY();
		}
		
		this.center = new Point(x/n, y/n);
		this.area =  (Math.abs(area/2));
	}

	/**
	 * Zkontroluje, zda je novì vytvoøený uzel uvnitø ètvrti.
	 *
	 * @param newnode  Nový uzel
	 * @param quarter Ètvr ve které ulice stavíme (nebo null)
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	public Street_result check_if_inside(Node newnode){
		double angle = 12.456;
		boolean inside = false;
		for(Street s: streets){
			if(newnode.compute_distance_from_street(s) < 0.0001)
				return Street_result.not_altered;
		}
		while(angle<360){
			Street help_street = new Street(newnode, Node.make_new_node(angle, null, newnode, 1000*1000,null), null);
			int intersections = 0;
			for (Street s: streets) {
				if( Street.getIntersection(s, help_street)!=null ){
					intersections++;
				}
			}
			if(intersections%2 == 1)
				return Street_result.not_altered;
			//inside = inside && (intersections%2 == 1);
			angle+=50.1234567;
		}	
		if(inside)
			return Street_result.not_altered;
		return Street_result.fail;
	}
	public Street get_longest_street(){
		double max = 0;
		Street longest = null;
		for(Street s: streets){
			if(s.length > max){
				max = s.length;
				longest = s;
			}
		}
		return longest;
	}

}
