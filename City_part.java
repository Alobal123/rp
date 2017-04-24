package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * M�stsk� ��st - je n�jak� ��st m�sta ohrani�ena ulicemi, tedy st�na grafu tvo�en�ho ulicemi a k�i�ovatkami.
 * Jej�mi potomky jsou t��dy Quarter a Block. 
 * {@link Quarter} je ohrani�ena pouze hlavn�mi ulicemi, zat�mco {@link Block} i t�mi vedlej��mi.
 */
public abstract class City_part implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7374818903326872052L;
	/** Seznam ulic ohrani�uj�ch tuto m�stskou ��st. */
	List<Street> streets = new ArrayList<Street>();
	public List<City_part> contained_city_parts = new ArrayList<>();
	
	/**Prvn� uzel, tj. uzel ze kter�ho vych�z� prvn� ulice v atributu streets. */
	Node firstnode;
	
	/** Land_use_type t�to ��sti m�sta. */
	public Lut lut;
	
	/** Lok�ln� hodnota t�to ��sti m�sta. */
	double value;
	
	/** V�m�ra, neboli plocha, kterou tato ��st m�sta zab�r�. */
	double area;
	
	/** Ur�uje, zda je tato ��st m�sta postavena, nebo zat�m ne. */
	boolean built;
	
	/** T�i�t�, tedy bod zhruba ve st�edu t�to m�stsk� ��sti. */
	Point center;
	
	/**
	 * Vrac� mno�inu v�ech uzl�, kter� jsou na hranici t�to m�stsk� ��sti.
	 *
	 * @return Mno�ina uzl�
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
	 * Vrac� seznam uzl� na hranici t�to m�stsk� ��sti, tak jak jdou za sebou.
	 *
	 * @return Seznam uzl�
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
	 * Vrac� seznam bod�, kter� odpov�daj� uzl�, na hranici t�to m�stsk� ��sti.
	 *
	 * @return Seznam bod�
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
	 * Vrac� seznam uzl� le��c�ch uvnit� t�to �tvrti.
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
	 * Vypo��t� obsah mnoho�heln�ku dan�ho touto m�stskou ��st�.
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
	 * Zkontroluje, zda je nov� vytvo�en� uzel uvnit� �tvrti.
	 *
	 * @param newnode  Nov� uzel
	 * @param quarter �tvr� ve kter� ulice stav�me (nebo null)
	 * @return Zda je nov� ulice v po��dku a zda se zm�nila.
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
