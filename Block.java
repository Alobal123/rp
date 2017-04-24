/*
 * 
 */
package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
	

	/**
	 * Konstruktor.Zkopíruje si všechny uzly na hranicích bloku a pøidá je do atributu lot_borders.
	 *
	 * @param streets - ulice ohranièující blok
	 * @param firstnode - jde o první uzel, ze kterého vychází první ulice v seznamu ulic v prvním parametru
	 * @param settings - aktuální parametry
	 */
	public Block(List<Street> streets,Node firstnode){
		this.streets = streets;
		this.firstnode = firstnode;
		for(Node n: this.get_nodes_once()){
			lot_borders.add(n.copy_node_with_streets_from_block(this));
		}

		compute_area();
	}
	/**
	 * Odstraní všechny uzly, které obsahují v tomto bloku jen dvì ulice a tvoøí køižovatku (180,180).
	 * @param Seznam uzlù v tomto bloku.
	 */
	void remove_useless_nodes(ArrayList<Node> borders){
		Iterator<Node> i = borders.iterator();
		while(i.hasNext()){
			Node n = i.next();
			if(n.streets.size() == 0)
			{
				i.remove();
			}
			if(n.streets.size() == 2){
				Street s0 = n.streets.get(0);
				Street s1 = n.streets.get(1);
				
				if( Math.abs(Street.get_oriented_angle(s0,s1 ,n)-180.0) <0.0001){
					Node n0 = s0.get_other_node(n);
					Node n1 = s1.get_other_node(n);
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
	 * Slouží ke kopírování uzlù. Nalezne uzly ve stejné poloze jako krajní uzly ulice s v seznamu nodes.
	 *
	 * @param s ulice
	 * @param nodes seznam uzlù
	 */
	void substitute_streets(Street s, ArrayList<Node> nodes){
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
			if(Point.dist(n.getPoint(), node.getPoint())<0.00001)
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
	public boolean check_if_built(){
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
			street.node1.setBuilt(true);
			street.node2.setBuilt(true);
		}
	}
	
	/**
	 * Vybere ze všech nabídnutých Lut ten, který je nejvýhodnìjší pro tento blok, pøiøadí ho a zvýší poèty obyvatel v uzlech pøíslušným zpùsobem.
	 *
	 * @param luts Všechny Lut ve mìstì.
	 * @param network Graf ulic
	 * @param nd Aktuální vzdálenosti uzlù a nejkratší cesty mezi nimi.
	 */
	 void choose_lut(List<Lut> luts,Lut chosen_lut, Street_Network network,Node_Distance nd,Settings settings){
		LinkedHashSet<Node> nodes = get_nodes_once();
		if(lut != null){
			for (Node node : nodes) {
				node.residents-= lut.residents;
			}
		}
		if(chosen_lut == null)
			chosen_lut = find_best_lut(luts, network, nd,settings);
		this.lut = chosen_lut;
		divide_and_place_buildings(settings,network.rnd);
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
	public Lut find_best_lut(List<Lut> luts,Street_Network network,Node_Distance nd,Settings settings){
		double max = -100;
		Lut bestlut = null;
		for(Lut lut: luts){
			double value = lut.evaluate(this, network, nd,settings);
			if(value > max){
				max = value;
				bestlut = lut;
			}
		}
		this.value = max;
		return bestlut;
	}
	
	/**
	 * Upraví hranice pozemkù tohoto bloku tak, aby pozemky byly konvexní. Poté rozdìlí pozemky tak, aby nebyly pøíliš velké.
	 */
	 void divide_and_place_buildings(Settings settings,Random_Wrapped rnd){	
		
		divide_until(settings);
		//control();
		
	}
	
	private void control() {
		ArrayList<City_part> to_remove = new ArrayList<>();
		for(City_part cp: contained_city_parts){
					Node center = new Node(cp.center.getX(), cp.center.getY(), null,null);
					if(check_if_inside(center) == Street_result.fail)
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
		Block_search.search_for_blocks_inside_quarter(this,lot_borders,false,false,null);
		if(contained_city_parts.isEmpty()){
			Block_search.search_for_blocks_inside_quarter(this,lot_borders,true,false,null);
			if(contained_city_parts.size() == 2){
				if(contained_city_parts.get(0).streets.size() > contained_city_parts.get(1).streets.size())
					contained_city_parts.remove(1);
				else
					contained_city_parts.remove(0);
			}
		}

		
	}
	
	/**
	 * Dìlí pozemky v tomto bloku tak dlouho, dokud žádný z nich nemá vìtší obsah než minarea.
	 *
	 * @param minarea Minimální velikost pozemku
	 */
	private void divide_until(Settings settings){
		
		ArrayList<Lot> expanded= new ArrayList<>();
		find_lots();
		while(divide(expanded,settings)){
			find_lots();
		}
		find_lots();
		for(City_part cp: contained_city_parts){
			Lot l = (Lot)cp;
			if(l.building == null){
				l.choose_and_place_building(lut, settings);
			}
		}
	}
	
	public boolean divide(ArrayList<Lot> expanded,Settings settings){
		for (int i = 0; i < contained_city_parts.size(); i++) {
			if(!expanded.contains(contained_city_parts.get(i))){
				
				Lot lot = (Lot)contained_city_parts.get(i);
				Street newstreet = lot.divide(this,true);
				
				if(newstreet != null){
					Lot lot1 =  new Lot(Block_search.check_for_new_quarters(newstreet, true, null, newstreet.node1, true),newstreet.node1);
					Lot lot2 =  new Lot(Block_search.check_for_new_quarters(newstreet, false, null, newstreet.node1, true),newstreet.node1);
					boolean lot1_has_building = lot1.choose_and_place_building(lut, settings);
					boolean lot2_has_building = lot2.choose_and_place_building(lut, settings);
	
					if(!lot1_has_building || !lot2_has_building){
						lot.undivide(newstreet, this,Street_type.lot_border);
					
					}
				}
				expanded.add(lot);
				return true;
				

			}
			
		}
		return false;
	}
	
}
