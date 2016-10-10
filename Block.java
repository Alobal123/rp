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
 * Tøída Block reprezentuje jeden mìstský blok. Tj. pozemky ohranièené cestami.
 * Od tøídy City_Part dìdí všechny potøebné atributy.
 */
public class Block extends City_part implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8080606993668064943L;
	
	/** Obsahuje všechny uzly, které tvoøí hranic pozemkù v tomto bloku.*/
	List<Node> lot_borders = new ArrayList<>();
	
	/** Aktuální parametry simulace. */
	Settings settings;
	
	/**
	 * Konstruktor.Zkopíruje si všechny uzly na hranicích bloku a pøidá je do atributu lot_borders.
	 *
	 * @param streets - ulice ohranièující blok
	 * @param firstnode - jde o první uzel, ze kterého vychází první ulice v seznamu ulic v prvním parametru
	 * @param settings - aktuální parametry
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
	 * SLouží ke kopírování uzlù. Nalezne uzly ve stejné poloze jako krajní uzly ulice s v seznamu nodes.
	 *
	 * @param s ulice
	 * @param nodes seznam uzlù
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
	 * Nalezne v seznamu nodes uzel, který je dostateènì blízko uzlu node a vrátí ho.
	 *
	 * @param node hledaný uzel
	 * @param nodes seznam uzlù
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
	 * Zkontroluje, zda je tento blok pøipraven k postavení.
	 * A to nastane tehdy, pokud je postaven dostateèný poèet ulic, které ho ohranièují.
	 *
	 * @return Pokud má být postaven, vrací true, jinak false.
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
	 * Postaví všechny ulice a uzly v seznamu ulic ohranièující tento blok.
	 */
	public void build_all(){
		
		for (Street street : streets) {
			street.built = true;
			street.node1.built =true;
			street.node2.built =true;
		}
	}
	
	/**
	 * Vybere ze všech nabídnutých Lut ten, který je nejvýhodnìjší pro tento blok, pøiøadí ho a zvýší poèty obyvatel v uzlech pøíslušným zpùsobem.
	 *
	 * @param luts Všechny Lut ve mìstì.
	 * @param network Graf ulic
	 * @param nd Aktuální vzdálenosti uzlù a nejkratší cesty mezi nimi.
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
	 * Vybere a vrátí Lut s nejvìtší hodnotou pro tento pozemek.
	 *
	 * @param luts Všechny Lut ve mìstì.
	 * @param network Graf ulic
	 * @param nd Aktuální vzdálenosti uzlù a nejkratší cesty mezi nimi.
	 * @return Lut s nejvìtší hodnotou pro tento blok.
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
	 * Upraví hranice pozemkù tohoto bloku tak, aby pozemky byly konvexní. Poté rozdìlí pozemky tak, aby nebyly pøíliš velké.
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
	 * V bloku najde všechny pozemky a uloží je v atributu contained_city_parts.
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
	 * Dìlí pozemky v tomto bloku tak dlouho, dokud žádný z nich nemá vìtší obsah než minarea.
	 *
	 * @param minarea Minimální velikost pozemku
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
	 * Najde a vrátí pozemek vìtší než minarea.
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
