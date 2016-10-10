/*
 * 
 */
package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


/**
 * T��da Block reprezentuje jeden m�stsk� blok. Tj. pozemky ohrani�en� cestami.
 * Od t��dy City_Part d�d� v�echny pot�ebn� atributy.
 */
public class Block extends City_part implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8080606993668064943L;
	
	/** Obsahuje v�echny uzly, kter� tvo�� hranic pozemk� v tomto bloku.*/
	List<Node> lot_borders = new ArrayList<>();
	
	/** Aktu�ln� parametry simulace. */
	Settings settings;
	
	/**
	 * Konstruktor.Zkop�ruje si v�echny uzly na hranic�ch bloku a p�id� je do atributu lot_borders.
	 *
	 * @param streets - ulice ohrani�uj�c� blok
	 * @param firstnode - jde o prvn� uzel, ze kter�ho vych�z� prvn� ulice v seznamu ulic v prvn�m parametru
	 * @param settings - aktu�ln� parametry
	 */
	public Block(List<Street> streets,Node firstnode,Settings settings){
		this.streets = streets;
		this.firstnode = firstnode;
		this.settings = settings;
		for(Node n: this.get_nodes_once()){
			lot_borders.add(n.copy_node(this));
		}
		
		ArrayList<Node> new_lot_borders = new ArrayList<>();
		for(Node n: lot_borders){
			Node newnode = new Node(n.point.x, n.point.y, n.major,true);
			new_lot_borders.add(newnode);
		}
		
		HashSet<Street> already_added = new HashSet<>();
		for(Node n: lot_borders){
			for(Street s: n.streets){
				if(!already_added.contains(s)){
					substitute_streets(s,new_lot_borders);
					already_added.add(s);
				}
			}
		}
		remove_useless_nodes( new_lot_borders);
		lot_borders = new_lot_borders;
		find_area();
	}
	
	public void remove_useless_nodes(ArrayList<Node> borders){
		Iterator<Node> i = borders.iterator();
		while(i.hasNext()){
			Node n = i.next();
			if(n.streets.size() == 2){
				Street s0 = n.streets.get(0);
				Street s1 = n.streets.get(1);
				
				if( Math.abs(Street.get_oriented_angle(s0,s1 ,n)-180.0) <0.0001){
					Node n0 = s0.other_node(n);
					Node n1 = s1.other_node(n);
					n0.streets.remove(s0);
					n1.streets.remove(s1);
	
					Street s = new Street(n0, n1, Street_type.minor);
					n0.streets.add(s);
					n1.streets.add(s);
					i.remove();
					
				}
			}
			
		}
	}
	
	/**
	 * SLou�� ke kop�rov�n� uzl�. Nalezne uzly ve stejn� poloze jako krajn� uzly ulice s v seznamu nodes.
	 *
	 * @param s ulice
	 * @param nodes seznam uzl�
	 */
	public void substitute_streets(Street s, ArrayList<Node> nodes){
		Node node1 = findnode(s.node1,nodes);
		Node node2 = findnode(s.node2,nodes);
		if(node1 != null && node2 != null){
			Street newstreet = new Street(node1, node2, s.major,true);
			node1.streets.add(newstreet);
			node2.streets.add(newstreet);
		}
		
	}
	
	/**
	 * Nalezne v seznamu nodes uzel, kter� je dostate�n� bl�zko uzlu node a vr�t� ho.
	 *
	 * @param node hledan� uzel
	 * @param nodes seznam uzl�
	 * @return the node
	 */
	public Node findnode(Node node,ArrayList<Node> nodes){
		for(Node n: nodes){
			if(Point.dist(n.point, node.point)<0.0000001)
				return n;
		}
		return null;
		
	}
	
	/**
	 * Zkontroluje, zda je tento blok p�ipraven k postaven�.
	 * A to nastane tehdy, pokud je postaven dostate�n� po�et ulic, kter� ho ohrani�uj�.
	 *
	 * @return Pokud m� b�t postaven, vrac� true, jinak false.
	 */
	public Boolean check_if_built(){
		int sum = 0;
		for (Street street : streets) {
			if(street.built)
				sum++;
		}
		if(sum + 2 > streets.size()/2)
			return true;
		return false;
	}
	
	/**
	 * Postav� v�echny ulice a uzly v seznamu ulic ohrani�uj�c� tento blok.
	 */
	public void build_all(){
		
		for (Street street : streets) {
			street.built = true;
			street.node1.built =true;
			street.node2.built =true;
		}
	}
	
	/**
	 * Vybere ze v�ech nab�dnut�ch Lut ten, kter� je nejv�hodn�j�� pro tento blok, p�i�ad� ho a zv��� po�ty obyvatel v uzlech p��slu�n�m zp�sobem.
	 *
	 * @param luts V�echny Lut ve m�st�.
	 * @param network Graf ulic
	 * @param nd Aktu�ln� vzd�lenosti uzl� a nejkrat�� cesty mezi nimi.
	 */
	public void choose_lut(List<Lut> luts,Street_Network network,Node_Distance nd){
		HashSet<Node> nodes = get_nodes_once();
		if(lut != null){
			for (Node node : nodes) {
				node.residents-= lut.residents;
			}
		}
		Lut best_lut = find_best_lut(luts, network, nd);
		this.lut = best_lut;
		divide_to_convex();
		for(City_part current_part: contained_city_parts){
			((Lot)current_part).choose_and_place (lut,settings);
			
		}
		for (Node node : nodes) {
			node.residents += this.lut.residents;
		}
		
	}

	/**
	 * Vybere a vr�t� Lut s nejv�t�� hodnotou pro tento pozemek.
	 *
	 * @param luts V�echny Lut ve m�st�.
	 * @param network Graf ulic
	 * @param nd Aktu�ln� vzd�lenosti uzl� a nejkrat�� cesty mezi nimi.
	 * @return Lut s nejv�t�� hodnotou pro tento blok.
	 */
	private Lut find_best_lut(List<Lut> luts,Street_Network network,Node_Distance nd){
		double max = -100;
		Lut bestlut = null;
		for(Lut lut: luts){
			double value = lut.evaluate(this, network, nd);
			if(value > max){
				max = value;
				bestlut = lut;
			}
		}
		this.value = max;
		//System.out.println(bestlut);
		return bestlut;
	}
	
	/**
	 * Uprav� hranice pozemk� tohoto bloku tak, aby pozemky byly konvexn�. Pot� rozd�l� pozemky tak, aby nebyly p��li� velk�.
	 */
	public void divide_to_convex(){	
		
		
		ArrayList<Node> new_borders = new  ArrayList<>();
		new_borders.addAll(lot_borders);
		Triangulation.triangulate_block(new_borders);
		lot_borders = new_borders;
		remove_crossings();
		remove_outsiders();
		remove_diagonals();
		find_lots();
		
		divide_until(lut.minimal_lot_area);
		control();
		
	}
	
	private void remove_crossings(){
		class cross{
			Street s1;
			Street s2;
			public cross(Street s1,Street s2){
				this.s1 = s1;
				this.s2 = s2;
			}
			@Override
			public boolean equals(Object o){
				cross c = (cross)o;
				if(s1 == c.s1 && s2 == c.s2)
					return true;
				if(s1 == c.s2 && s2 == c.s1)
					return true;	
				return false;
			}
		}
		
		HashSet<cross> crosses = new HashSet<>();
		for(Node n: lot_borders){
			for(Node n2:lot_borders){
				if(n != n2){
					for(Street s : n.streets){
						for(Street s2 : n2.streets){
							if(s.node1 != s2.node1  && s.node2 != s2.node1  && s.node1 != s2.node2  && s.node2 != s2.node2  && Street.getIntersection(s, s2) != null)
								crosses.add(new cross(s,s2));
						}
					}
				}
			}
		}
		for(cross c: crosses){
			if(c.s1.major != Street_type.lot_border){
				c.s1.node1.streets.remove(c.s1);
				c.s1.node2.streets.remove(c.s1);
			}
			else{
				c.s2.node1.streets.remove(c.s2);
				c.s2.node2.streets.remove(c.s2);
			}
		}
	}

	private void remove_outsiders(){
		HashSet<Street> to_remove = new HashSet<>();
		for(Node n: lot_borders){
			for(Street s: n.streets){
				Node middle = new Node((s.node1.point.x + s.node2.point.x)/2, (s.node1.point.y + s.node2.point.y)/2, null);
				if(check_if_inside(middle) == Street_Result.fail)
					to_remove.add(s);
			}
		}
		for(Street s: to_remove){
			s.node1.streets.remove(s);
			s.node2.streets.remove(s);
		}
	}
	
	private void remove_diagonals(){
		HashSet<Node> corners = new HashSet<>();
		for(Node n: lot_borders){
				if(n.major != Street_type.lot_border){
					corners.add(n);
				}		
		}
		HashSet<Node> to_try = new HashSet<>(corners);
		while(to_try.size() > 0){
			Iterator<Node> i = to_try.iterator();
			remove_corner(i.next(), corners,to_try);
		}
	}
	
	private void remove_corner(Node n, HashSet<Node> corners, HashSet<Node> to_try){
		Street s1 = null;
		Street s2 = null;
		for(Street s : n.streets){
			if(corners.contains(s.node1) && corners.contains(s.node2)){
				if(s1 == null)
					s1 =s;
				else
					s2 =s;
			}
		}
		
		if(s1 != null && s2 != null){
			Node n1 = s1.other_node(n);
			Node n2 = s2.other_node(n);
			if(Triangulation.street_exists(n1, n2) != null){
				Node n3 = find_triangle(n1, n2,n);
				Street to_remove = Triangulation.street_exists(n1, n2);
				if(n3 != null && to_remove.major == Street_type.lot_border){
					
					to_remove.node1.streets.remove(to_remove);
					to_remove.node2.streets.remove(to_remove);
					corners.add(n);
					corners.add(n1);
					corners.add(n2);
					corners.add(n3);
					
				}
	
			}
		}
		to_try.remove(n);
		
		
	}
	private Node find_triangle(Node n1, Node n2, Node n){
		for(Street s1: n1.streets){
			Node newnode = s1.other_node(n1);
			for(Street s2: n2.streets){
				if(s2.other_node(n2) == newnode && newnode != n)
					return newnode;
			}
		}
		
		return null;
	}
	
	private void control() {
		ArrayList<City_part> to_remove = new ArrayList<>();
		for(City_part cp: contained_city_parts){
					Node center = new Node(cp.center.x, cp.center.y, null);
					if(check_if_inside(center) == Street_Result.fail)
						to_remove.add(cp);
		}
		if(!to_remove.isEmpty())
		contained_city_parts.removeAll(to_remove);
		
	}


	
	/**
	 * V bloku najde v�echny pozemky a ulo�� je v atributu contained_city_parts.
	 */
	public void find_lots(){
		contained_city_parts = new ArrayList<>();
		Street_Network.search_for_blocks(this, lot_borders,false,false,null);
		if(contained_city_parts.isEmpty()){
			Street_Network.search_for_blocks(this,lot_borders,true,false,null);
			if(contained_city_parts.get(0).streets.size() > contained_city_parts.get(1).streets.size())
				contained_city_parts.remove(1);
			else
				contained_city_parts.remove(0);
		}

		
	}
	
	/**
	 * D�l� pozemky v tomto bloku tak dlouho, dokud ��dn� z nich nem� v�t�� obsah ne� minarea.
	 *
	 * @param minarea Minim�ln� velikost pozemku
	 */
	private void divide_until(double minarea){
		Lot lot = find_big_lot(minarea);
		int i =0;
		while(i<50 && lot != null){
			lot.divide(this);
			find_lots();
			lot = find_big_lot(minarea);
			i++;
		}
	}
	
	/**
	 * Najde a vr�t� pozemek v�t�� ne� minarea.
	 *
	 * @param minarea the minarea
	 * @return the lot
	 */
	private Lot find_big_lot(double minarea){
		Lot lot = null;
		for(City_part cp : contained_city_parts){
			if(cp.area > minarea){
				lot = (Lot)cp;
				break;
			}
		}
		return lot;
	}
}
