/*
 * 
 */
package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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
		//System.out.println(new_lot_borders.size());
		for(Node n: lot_borders){
			for(Street s: n.streets){
				if(!already_added.contains(s)){
					substitute_streets(s,new_lot_borders);
					already_added.add(s);
				}
			}
		}
		lot_borders = new_lot_borders;
		find_area();
	}
	
	/**
	 * SLouží ke kopírování uzlù. Nalezne uzly ve stejné poloze jako krajní uzly ulice s v seznamu nodes.
	 *
	 * @param s ulice
	 * @param nodes seznam uzlù
	 */
	private void substitute_streets(Street s, ArrayList<Node> nodes){
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
	private Node findnode(Node node,ArrayList<Node> nodes){
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
		double[] angles = {0,90,180,270};
		for(Node n: lot_borders){
			//if(n.streets.size() == 1){
			for(double d: angles){
				Node newnode = Node.make_new_node(d, Street_type.lot_border, n, 1000);
				newnode.built = true;
				Street newstreet = new Street(n, newnode, Street_type.lot_border);
				boolean is_street = false;
				for(Street s: n.streets){
					if(Street.get_angle(s, newstreet) <0.1){
						is_street = true;
						break;
					}
				}
				if(!is_street)
					find_crossing(newstreet, n, newnode, new_borders);
			}
		//	}
		}
		lot_borders = new_borders;
		find_lots();
		divide_until(lut.minimal_lot_area);
;
		
	}
	
	/**
	 * Najde, zda a kde se køíží ulice newstreet s ulicemi obsaženými v uzlech v seznamu list.
	 * V místì køížení vytvoøí nový uzel a ulici v nìm ukonèí. 
	 * 
	 *
	 * @param newstreet the newstreet
	 * @param oldnode the oldnode
	 * @param newnode the newnode
	 * @param list the list
	 */
	private void find_crossing(Street newstreet,Node oldnode,Node newnode,ArrayList<Node> list){
		Street intersecting = null;
		Point intersection = null;
		double min = Point.dist(oldnode.point, newnode.point);
		for (Node n: list) {
			for(Street street: n.streets){
				if(!street.equals(newstreet)){
					Point intersect = null;
					intersect  = Street.getIntersection(street, newstreet);
					if(intersect != null && Point.dist(oldnode.point, intersect) < min
							&& Point.dist(intersect, street.node1.point) > 0.00001 && Point.dist(intersect, street.node2.point) > 0.00001){
						intersecting = street;
						intersection = intersect;
						min = Point.dist(oldnode.point, intersect);
					}
				}	
			}
		}
		if(intersecting != null){
			Node trynode = new Node(intersection.x, intersection.y, intersecting.major,intersecting.built);
			Street newstreet1 = new Street(intersecting.node1, trynode, intersecting.major,intersecting.built);
			Street newstreet2 = new Street(intersecting.node2,trynode,intersecting.major,intersecting.built);
			Street newstreet3 = new Street(oldnode,trynode,Street_type.lot_border,true);
			ArrayList<Double> angles = new ArrayList<>();
			angles.add(180.0);
			double angle = Street.get_angle(newstreet1, newstreet3);
			if(angle>180)
				angle = angle -180;
			angles.add(angle);
			angles.add(180.0 - angle);
			Crossroad newcross = new Crossroad(3, angles);
			
			trynode.streets.add(newstreet1);
			trynode.streets.add(newstreet2);
			trynode.streets.add(newstreet3);	
			intersecting.node1.streets.add(newstreet1);
			intersecting.node2.streets.add(newstreet2);
			intersecting.node1.streets.remove(intersecting);
			intersecting.node2.streets.remove(intersecting);
			trynode.crossroad = newcross;
			
			oldnode.streets.add(newstreet3);
			trynode.angle = trynode.compute_angle();
			list.add(trynode);
		}
		
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
	//	if(lot == null)
	//		System.out.println("null");
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
