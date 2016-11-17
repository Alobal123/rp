package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import krabec.citysimulator.ui.City_window;


/**
 * This class manages the street network of the city. It is responsible for growth of new streets.
 * 
 */
public class Street_Network implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6510576669225628174L;
	/** Reprezentuje speciální typ køiovatky - slepou ulièku s jedinou ulicí.*/
	public static final Crossroad end_of_road;
	static {
		end_of_road = new Crossroad(1, null);
		end_of_road.angles = new ArrayList<>();
		end_of_road.angles.add(360.0);
	}
	
	int number = 0;
	/** Všechny pouitelné køiovatky. */
	public List<Crossroad> all_crossroads;
	
	/** Seznam všech ètvrtí ve mìstì. */
	List<Quarter> quarters = new ArrayList<>();
	/** Seznam všech uzlù v grafu ulic. */
	List<Node> nodes;
	
	static Random rnd = new Random();
	
	/** Seznam všech center rùstu ve mìstì. */
	List<Point> growthcenters;
	
	/** Seznam všech center mìsta. */
	List<Point> citycenters = new ArrayList<Point>();
	
	/** Seznam všech uzlù pøipravenıch k rùstu. */
	List<Node> to_grow_nodes;
	
	/** Aktuálnì zpracovávanı novì vytvoøenı uzel. Figuruje v metodì grow_street.*/
	Node current_new_node;
	
	/**Aktuálnì zpracovávaná novì vytvoøená ulice. Figuruje v metodì grow_street. */
	Street current_new_street;
	
	/** Seznam uzlù, které se nìjakım zpùsobem zmìnily. */
	List<Node> changed_nodes;
	Settings settings;
	
	/**
	 * Konstruktor 
	 *
	 */
	public Street_Network(){
		nodes = new ArrayList<>();
		growthcenters = new ArrayList<>();
		to_grow_nodes = new ArrayList<>();
		settings = new Settings();
	}
	public Street_Network(List<Crossroad> all_crossroads,Settings settings){
		
		nodes = new ArrayList<>();
		growthcenters = new ArrayList<>();
		to_grow_nodes = new ArrayList<>();
		this.all_crossroads = all_crossroads;
		
		Node node1 = (new Node(0, 0, Street_type.major));	
		nodes.add(node1);
		node1.crossroad = Street_Network.end_of_road;
		node1.angle = 270;
		
		Node node2 = new Node(-0.5, 0, Street_type.major);
		nodes.add(node2);
		node2.crossroad = Street_Network.end_of_road;
		node2.angle = 90;
		City_window.create_street(node1, node2,Street_type.major);
		for (Node n: nodes) {
			to_grow_nodes.add(n);
		}
		growthcenters.add(new Point(0,0));
		citycenters.add(new Point(0,0));
		this.settings = settings;
	}
	
	/**
	 * Zkusí nechat vyrùst i novıch hlavních ulic. Vybere uzly k rùstu a z nich se pokusí vytvoøit novou ulici.
	 * Poté nalezne novì vzniklé ètvrtì a vyplní je vedlejšími ulicemi. Najde takto vzniklé bloky. Vrací všechny zmìnìné uzly,
	 * pro které je tøeba pøepoèítat hodnotu doprav.
	 *
	 * @param number_of_streets Poèet uzlù k rùstu
	 * @return Seznam zmìnìnıch uzlù
	 */
	public List<Node> grow_major_streets(int number_of_streets){
		changed_nodes = new ArrayList<>();
		ArrayList<Node> chosen_nodes = choose_nodes_to_grow(to_grow_nodes,number_of_streets);
		
		for (Node node : chosen_nodes) {
			Street newstreet = grow_street(node,Street_type.major,null,to_grow_nodes);
			if(newstreet != null){
				newstreet.length = newstreet.get_length();
				ArrayList<Street> new_quarter = check_for_new_quarters(newstreet,true,null,newstreet.node2,false);
				ArrayList<Street> new_quarter2 = check_for_new_quarters(newstreet,true,null,newstreet.node1,false);
				if(new_quarter != null && new_quarter2 != null){
					if(new_quarter.size() < new_quarter2.size()){
						Quarter q = new Quarter(new_quarter,newstreet.node2);
						number++;
						q.number = number;
						quarters.add(q);
						grow_minor_streets(q);

					}
					else{
						Quarter q = new Quarter(new_quarter2,newstreet.node1);
						number++;
						q.number = number;
						quarters.add(q);
						grow_minor_streets(q);
					}
				}
			}
		}
		return changed_nodes;
	}
	/**
	 * Dostane ètvr a tu vyplní vedlejšími ulicemi. Také nalezne všechny bloky vytvoøené v této ètvrti.
	 *
	 * @param quarter Ètvr
	 */
	private void grow_minor_streets(Quarter quarter){
		ArrayList<Node> to_grow_nodes = new ArrayList<>();
		for (Street s: quarter.streets) {
			if(!to_grow_nodes.contains(s.node1))
				to_grow_nodes.add(s.node1);
			if(!to_grow_nodes.contains(s.node2))
				to_grow_nodes.add(s.node2);
		}
		boolean some_minor_street = false;
		make_seed_node(to_grow_nodes, quarter);
		while(!to_grow_nodes.isEmpty()){
			ArrayList<Node> chosen_nodes = choose_nodes_to_grow(to_grow_nodes,10);
			for (Node n: chosen_nodes){
				 Street s = grow_street(n,Street_type.minor, quarter, to_grow_nodes);
				 if(s!=null){
					 s.length = s.get_length();
					 some_minor_street = true;
				 }
			}
		}
		
		if(some_minor_street){
			search_for_blocks(quarter,nodes,false,true,settings);
		}
		else{
			search_for_blocks(quarter,nodes,true,true,settings);
			if(quarter.contained_city_parts.get(0).streets.size() > quarter.contained_city_parts.get(1).streets.size())
				quarter.contained_city_parts.remove(1);
			else
				quarter.contained_city_parts.remove(0);
		}
	}
	
	
	private void control_blocks(Quarter q) {
		ArrayList<City_part> to_remove = new ArrayList<>();
		for(City_part cp: q.contained_city_parts){
			for(City_part cp2 : q.contained_city_parts){
				if(cp != cp2){
					Node center = new Node(cp2.center.getX(), cp2.center.getY(), null);
					if(cp.check_if_inside(center) == Street_Result.not_altered)
						to_remove.add(cp2);
				}
			}
		}
		if(!to_remove.isEmpty())
			System.out.println("odstraneno");
		q.contained_city_parts.removeAll(to_remove);
		
	}
	/**
	 * Vybere ze seznamu uzlù nìkolik uzlù, podle toho jak jsou vzdálené od center rùstu.
	 *
	 * @param to_grow_nodes Seznam ze kterého se vybírá
	 * @param number_of_streets Poèet uzlù k vybrání
	 * @return Vybrané uzly
	 */
	private ArrayList<Node> choose_nodes_to_grow(List<Node> to_grow_nodes, int number_of_streets){  
		
		ArrayList<Node> chosen = new ArrayList<>();
		assert to_grow_nodes.size()>0:"There are no nodes to grow";
	    double [] distribution = new double [to_grow_nodes.size()]; 
	    
	    double distSum = 0;
		for (int i = 0; i < to_grow_nodes.size(); i++) {
			distribution[i] = Math.pow(Math.E,-1*settings.focus_constant*Point.get_smallest_distance(to_grow_nodes.get(i).point, growthcenters));
			if(to_grow_nodes.get(i).streets.size() == 2)
				distribution[i]+=0.5;
			distSum +=distribution[i];
		}
		for (int j = 0; j < number_of_streets; j++) {		
				double rand = Math.random();
				double ratio = 1.0f / distSum;
				double tempDist = 0;
				for (int i = 0; i < distribution.length; i++) {
					tempDist += distribution[i];
					if (rand / ratio <= tempDist) {
						chosen.add(to_grow_nodes.get(i));
						break;
					}
				}
		}
        return chosen;
	}

	/**
	 * Vytvoøí novou ulici z uzlu, pokud je to moné. Pokud není parametr quarter nulovı, pak rosotu ulice jen v rámci této ètvrti.
	 *
	 * @param oldnode Uzel ze kterého roste nová ulice.
	 * @param major Zda má bıt nová ulice hlavní
	 * @param quarter Ètvr ve které má nová ulice bıt
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @return Nová ulice
	 */
	 private Street grow_street(Node oldnode,Street_type major, Quarter quarter, List<Node> to_grow_nodes){
		 if(oldnode.crossroad.viable_crossroads.size() == 0){
				to_grow_nodes.remove(oldnode);
				return null;
		}
		
		boolean succes = true;
		int limit = 10;							//TODO nastav limit kolikrat se bude zkouset stavet cesta z uzlu nez se vyradi
		cyklus: for (int i = 0; i < limit; i++) {
			succes = true;
			int a = rnd.nextInt(oldnode.crossroad.viable_crossroads.size());
			Crossroad new_crossroad = oldnode.crossroad.viable_crossroads.get(a);
			Crossroad old_crossroad = oldnode.crossroad;
			double angle = (oldnode.angle + oldnode.crossroad.get_relative_angle(a)+360) % 360;
			double length;
			if(major == Street_type.major)
				length= rnd.nextDouble() * (settings.major_max_length - settings.major_min_length) + settings.major_min_length +settings.major_prolongation;
			else
				length = rnd.nextDouble() * (settings.minor_max_length - settings.minor_min_length) + settings.minor_min_length +settings.minor_prolongation; 
			
			current_new_node = Node.make_new_node(angle, major, oldnode,length);
			current_new_node.angle = angle;
			current_new_street = new Street( oldnode, current_new_node,major);
			current_new_node.streets.add(current_new_street);
			current_new_node.angle = current_new_node.compute_angle();
			oldnode.streets.add(current_new_street);
			oldnode.crossroad = new_crossroad;
			oldnode.angle = oldnode.compute_angle();
			nodes.add(current_new_node);
			to_grow_nodes.add(current_new_node);

			Street_Result result = Street_Result.not_altered;
			
			if(succes){
				result = check_for_crosses(current_new_street, current_new_node, oldnode,major,to_grow_nodes,quarter);
				if(result == Street_Result.not_altered){
					if(major == Street_type.major)
						length -= settings.major_prolongation;	
					else
						length -= settings.minor_prolongation;
					double dx = Math.sin(angle * Math.PI / 180) * length;
					double dy = Math.cos(angle * Math.PI / 180) * length;
					current_new_node.point = new Point(oldnode.point.getX() + dx, oldnode.point.getY() + dy);
				}
				else if (result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			
			if(succes && !(major == Street_type.major)){ //&& result != Street_Result.altered){
				result = quarter.check_if_inside(current_new_node);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			if(succes && major == Street_type.major){
				result = check_if_in_quarter(current_new_street);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			
			
			if(succes){
				result = check_for_close_streets(current_new_node,current_new_street,major);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			if(succes){
				result = check_for_close_node(current_new_street, current_new_node,oldnode,major,to_grow_nodes);
				if(result == Street_Result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			
		if(succes){
			break cyklus;
			}
		}
		
		if(!succes){
			to_grow_nodes.remove(oldnode);
			return null;
		}
		changed_nodes.add(current_new_street.node1);
		changed_nodes.add(current_new_street.node2);
		return current_new_street;
	}
	
	 
	/**
	 * Zkontroluje, zda novì vytvoøená ulice a novì vytvoøenı uzel, nejsou pøíliš blízko jiného uzlu, a zda se tyto uzly nedají slouèit v jeden,
	 * a provede všechny potøebné zmìny, pokud to lze.
	 *
	 * @param s Nová ulice
	 * @param newnode Novı uzel
	 * @param oldnode Starı uzel - ze kterého vyrostla nová ulice
	 * @param major Zda stavíme hlavní ulici
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	private Street_Result check_for_close_node(Street s, Node newnode, Node oldnode,Street_type major,List<Node> to_grow_nodes) {
		
		double constant;
		if(major==Street_type.major)
			 constant = settings.major_close_node_constant;
		else
			constant = settings.minor_close_node_constant;
		for (Node n: nodes) {
			
			if(s.node1 != n && s.node2!=n && Point.dist(n.point, newnode.point) < constant){
				Street newstreet = new Street(oldnode, n, major);
				
				oldnode.streets.remove(s);
				oldnode.streets.add(newstreet);
				n.streets.add(newstreet);
				
				Crossroad oldnode_cross = Crossroad.find(all_crossroads,oldnode);
				Crossroad n_cross = Crossroad.find(all_crossroads,n);
				if(oldnode_cross != null && n_cross != null){
					oldnode.crossroad = oldnode_cross;
					n.crossroad = n_cross;
					
					nodes.remove(newnode);
					to_grow_nodes.remove(newnode);
					current_new_node = n;
					current_new_street = newstreet;
					
					return Street_Result.altered;
				}
				else{
					oldnode.streets.add(s);
					oldnode.streets.remove(newstreet);
					n.streets.remove(newstreet);
					return Street_Result.fail;
				}
			}
		}
		return Street_Result.not_altered;
		
	}

	/**
	 *Kontroluje, zda se nová ulice nìkdì køíí s jinou ulicí a zda v místì køíení nelze vytvoøit novou køiovatku a uzel. 
	 *Pokud ano, provede všechny potøebné zmìny.
	 *
	 * @param newstreet Nová ulice
	 * @param newnode  Novı uzel
	 * @param oldnode Starı uzel - ze kterého vyrostla nová ulice
	 * @param major Zda stavíme hlavní ulici
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @param quarter Ètvr ve které ulice stavíme (nebo null)
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	
	private Street_Result check_for_crosses(Street newstreet, Node newnode, Node oldnode,Street_type major,List<Node> to_grow_nodes,Quarter quarter){
		Street intersecting = null;
		Point intersection = null;
		double min = Point.dist(oldnode.point, newnode.point);
		for (Node n: nodes) {
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
			Node trynode = new Node(intersection.getX(), intersection.getY(), intersecting.major,intersecting.built);
			
			Street newstreet1 = new Street(intersecting.node1, trynode, intersecting.major,intersecting.built);
			Street newstreet2 = new Street(intersecting.node2,trynode,intersecting.major,intersecting.built);
			Street newstreet3 = new Street(oldnode,trynode,major);
			ArrayList<Double> angles = new ArrayList<>();
			angles.add(180.0);
			double angle = Street.get_angle(newstreet1, newstreet3);
			if(angle>180)
				angle = angle -180;
			angles.add(angle);
			angles.add(180.0 - angle);
			Crossroad newcross = new Crossroad(3, angles);
			
			for (Crossroad c: all_crossroads) {
				if(c.equals(newcross)){
					trynode.streets.add(newstreet1);
					trynode.streets.add(newstreet2);
					trynode.streets.add(newstreet3);
					intersecting.node1.streets.add(newstreet1);
					intersecting.node2.streets.add(newstreet2);
					intersecting.node1.streets.remove(intersecting);
					intersecting.node2.streets.remove(intersecting);
					trynode.crossroad = c;
					oldnode.streets.remove(newstreet);
					oldnode.streets.add(newstreet3);
					nodes.add(trynode);
					to_grow_nodes.add(trynode);
					nodes.remove(newnode);
					to_grow_nodes.remove(newnode);
					trynode.angle = trynode.compute_angle();
					current_new_node = trynode;
					current_new_street = newstreet3;
					return Street_Result.altered;
				}
			}
			
			return Street_Result.fail;
		}
		
		return Street_Result.not_altered;
	}
	
	
	private Street_Result check_if_in_quarter(Street newstreet){
		Node middlenode = new Node((newstreet.node1.point.getX()+newstreet.node2.point.getX())/2, (newstreet.node1.point.getY()+newstreet.node2.point.getY())/2, null);
		for(Quarter q: quarters){
			if(q.check_if_inside(middlenode) == Street_Result.not_altered){
				return Street_Result.fail;
			}
		}
		return Street_Result.not_altered;
	}
	/**
	 * Zkontroluje, zda se novı uzel nenachází pøíliš blízko nìjaké ji existující ulici.
	 *
	 * @param newstreet Nová ulice
	 * @param newnode  Novı uzel
	 * @param major Zda stavíme hlavní ulici
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	private Street_Result check_for_close_streets(Node newnode,Street newstreet, Street_type major){
		double constant;
		if(major==Street_type.major)
			 constant = settings.major_close_street_constant;
		else
			constant = settings.minor_close_street_constant;
		
		
		for (Node n: nodes) {
			for(Street s: n.streets){
				if((s.node1 != newnode && s.node2 != newnode && newnode.distance(s) < constant) 
						|| (newstreet.node1 != n && newstreet.node2 != n && n.distance(newstreet) < constant)){
					return Street_Result.fail;
				}
			}
		
		}
		return Street_Result.not_altered;
	}
	
	/**
	 * V dané ètvrti nalezne všechny bloky, tj. stìny grafu tvoøeného hlavními ulicemi ohranièujícími ètvr a vedlejšími ulicemi uvnitø.
	 *
	 * @param quarter Ètvr
	 */
	public static void search_for_blocks(City_part quarter,List<Node> nodes,boolean whole,boolean blocks,Settings settings){
		HashSet<Street> to_search1 = new HashSet<>();
		HashSet<Street> to_search2 = new HashSet<>();
		
		for (Node n: nodes){
			for(Street s: n.streets){
				if(quarter.check_if_inside(s.node1)!=Street_Result.fail && quarter.check_if_inside(s.node2)!=Street_Result.fail){
					to_search1.add(s);
					to_search2.add(s);
				}
			}
		}
		
		while(!to_search1.isEmpty()){
			Iterator<Street> i = to_search1.iterator();
			Street s = i.next();
			find_blocks(s,quarter,to_search1,to_search2, true,whole,blocks,settings);
		}
		
		while(!to_search2.isEmpty()){
			Iterator<Street> i = to_search2.iterator();
			Street s = i.next();
			find_blocks(s,quarter,to_search1,to_search2, false,whole,blocks,settings);
		}
	}
	
	/**
	 * Hledá blok, kterı obsahuje ulici s ve ètvrti.
	 *
	 * @param s Ulice s
	 * @param quarter Ètvr
	 * @param to_search1 Mnoina ulic ze kterıch je ještì tøeba hledat bloky ve smìru node1 -> node2
	 * @param to_search2 Mnoina ulic ze kterıch je ještì tøeba hledat bloky ve smìru node2 -> node1
	 * @param from_1 Smìr hledání bloku
	 */
	public static ArrayList<Street> find_blocks(Street s ,City_part quarter,HashSet<Street> to_search1,HashSet<Street> to_search2, Boolean from_1,Boolean whole,Boolean blocks,Settings settings){
				Node first = s.node2;
				if(from_1){
					first = s.node1;
					to_search1.remove(s);
				}
				else{
					to_search2.remove(s);
				}
				
				ArrayList<Street> streets = check_for_new_quarters(s, true, quarter,first,!blocks);
				
				Node prevnode = null;
				if(streets != null){
						City_part block;
						if(blocks){
							block = new Block(streets,first,settings);
						}
						else{
							block = new Lot(streets,first);
						}
					if(!is_biggest(quarter, block) || whole){
						quarter.contained_city_parts.add(block);
						prevnode = null;
						for(Street s2 : streets){
							if(blocks && !s2.node1.blocks.contains(block))
								s2.node1.blocks.add((Block) block);
							if(blocks && !s2.node2.blocks.contains(block))
								s2.node2.blocks.add((Block) block);
							
							if(prevnode == null){
								prevnode = first;
								if(from_1)
									to_search1.remove(s2);
								else
									to_search2.remove(s2);
							}
							else{
								if(s2.other_node(prevnode) == s2.node1){
									
									to_search1.remove(s2);
								}
								else{
									to_search2.remove(s2);
								}
								prevnode = s2.other_node(prevnode);
							}
						}
					}
				}
				return streets;
	}
	
	
	/**
	 * Pokusí se najít mìstskou èást vzniklou pøidáním novì vytvoøené ulice. Vrací null pokud ádnou nenajde. 
	 * Pokud není parametr quarter null, hledá blok v této ètvrti. Jinak hledá pouze hlavní ulice.
	 * Vrací seznam ulic ohranièující nalezenou mìstskou èást.
	 *
	 * @param newstreet Nová ulice
	 * @param clockwise Zda se má hledat po smìru hodinovıch ruèièek
	 * @param quarter Ètvr
	 * @param startnode Uzel, ze kterého zaèínáme hledat
	 * @return Seznam ulic ohranièujících nalezenou èást mìsta
	 */
	public static ArrayList<Street> check_for_new_quarters(Street street,Boolean clockwise,City_part quarter,Node startnode,boolean simple){
		if(street == null || street.node1.streets.size() ==1 || street.node2.streets.size() ==1){
			return null;
		}
		if(startnode == null)
			startnode = street.node1;
		ArrayList<Street> rt = new ArrayList<>();
		HashMap<Street, Integer> visited = new HashMap<>();
		rt.add(street);
		visited.put(street, 1);
		Node n = startnode;
		Street s = get_least_angled(n,street, clockwise, quarter,simple);
		
		n = s.other_node(n);
		
		while(s != street){
			if(visited.containsKey(s)){
				if(visited.get(s) > 1){
					return null;
				}
				else
					visited.put(s, visited.get(s) +1);
			}
			else
				visited.put(s, 1);
			
			rt.add(s);
			Street s2 = get_least_angled(n,s,clockwise,quarter,simple);
			n = s2.other_node(n);
			s = s2;
		}
		
		if(rt.size()%2 == 0){
			for (int i = 0; i < rt.size()/2; i+=2) {
				if(rt.get(i) != rt.get(i+1)){
					return rt;
				}
			}
		}
		else{
			return rt;
		}
		return null;
	}

	/**
	

	/**
	 * Vrátí všechny zmìny zpùsobené rùstem nové ulice, pokud ulice nevyhovuje. 
	 *
	 * @param oldnode Starı uzel - ze kterého vyrostla nová ulice
	 * @param old_crossroad Pùvodní køiovatka starého uzlu
	 * @param major Zda stavíme hlavní ulici
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 */
	private void revert_changes(Node oldnode,Crossroad old_crossroad,Street_type major,List<Node> to_grow_nodes){
		
		current_new_node.streets.remove(current_new_street);
		nodes.remove(current_new_node);
		to_grow_nodes.remove(current_new_node);
		oldnode.streets.remove(current_new_street);
		oldnode.crossroad = old_crossroad;
		oldnode.angle = oldnode.compute_angle();
		
		if(current_new_node.streets.size() == 2 && Math.abs(180 - Street.get_angle(current_new_node.streets.get(0), current_new_node.streets.get(1)))<0.0001){
			Node node1 = current_new_node.streets.get(0).node1;
			if(node1 == current_new_node)
				node1 = current_new_node.streets.get(0).node2;
			Node node2 = current_new_node.streets.get(1).node1;
			if(node2 == current_new_node)
				node2 = current_new_node.streets.get(0).node2;
			node1.remove_street_to_node(current_new_node);
			node2.remove_street_to_node(current_new_node);
			
			Street_type type;
			if(node1.major==Street_type.major && node2.major==Street_type.major && current_new_node.major==Street_type.major )
				type = Street_type.major;
			else
				type = Street_type.minor;
			Street street = new Street(node1, node2, type,node1.built && node2.built);

			node1.streets.add(street);
			node2.streets.add(street);	
		}
	}

	
	/**
	 * Vytvoøí novı uzel uprostøed z jedné ulic ètvrti.
	 *
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @param quarter Ètvr
	 */
	private void make_seed_node(List<Node> to_grow_nodes,City_part quarter){
			Street oldstreet = quarter.streets.get(0);
			Point x = oldstreet.node1.point;
			Point y = oldstreet.node2.point;
			double newx = (x.getX() + y.getX())/2;
			double newy = (x.getY() + y.getY() )/2;
			Node new_node = new Node(newx,newy, Street_type.major);
			Street s1 = new Street(new_node, oldstreet.node1, Street_type.major);
			Street s2 = new Street(new_node, oldstreet.node2, Street_type.major);

			oldstreet.node1.streets.remove(oldstreet);
			oldstreet.node2.streets.remove(oldstreet);
			oldstreet.node1.streets.add(s1);
			oldstreet.node2.streets.add(s2);
			new_node.streets.add(s1);
			new_node.streets.add(s2);
			new_node.crossroad = Crossroad.find(all_crossroads, new_node);
			new_node.angle = new_node.compute_angle();
			to_grow_nodes.add(new_node);
			nodes.add(new_node);
				
			}
	
	/**
	 * Najde n nejbliších blokù danému bloku (Pomocí bfs).
	 *
	 * @param n Poèet blokù k nalezení
	 * @param block Blok ze kterého hledáme
	 * @return Seznam nejbliších blokù
	 */
	public ArrayList<Block> get_nearest_blocks(int n,Block block){
		ArrayList<Block> rt = new ArrayList<>();
		rt.add(block);
		ArrayList<Node> nodes = block.get_nodes();
		HashSet<Node> visited = new HashSet<>();
		LinkedList<Node> queue = new LinkedList<>();
		for(Node node: nodes){
			visited.add(node);
			queue.add(node);
		}
		while(rt.size() < n+1 && !queue.isEmpty()){
			Node node = queue.pop();
			for(Block b: node.blocks){
				if(!rt.contains(b))
					rt.add(b);
			}
			for(Street s: node.streets){
				if(!visited.contains(s.other_node(node))){
					queue.add(s.other_node(node));
					visited.add(s.other_node(node));
				}
			}
		}
		rt.remove(block);
		return rt;
		
	}
	
	/**
	 * Vrátí všechny bloky nacházející se v polomìru r. Bereme benou euklidovskou vzdálenost a mìøíme od støedu blokù.
	 *
	 * @param r Polomìr
	 * @param block Blok od kterého mìøíme
	 * @return Seznam blokù v polomìru r
	 */
	public ArrayList<Block> get_blocks_in_radius(double r, Block block){
		int n = (int)(4 * (r/settings.minor_min_length)*(r/settings.minor_min_length));
		ArrayList<Block> candidates = get_nearest_blocks(n, block);
		Iterator<Block> i = candidates.iterator();
		while(i.hasNext()){
			Block b = i.next();
			if(Point.dist(b.center, block.center)>r)
				i.remove();
		}
		return candidates;
		
	}
	
	/**
	 * Najde ulici která svírá nejmenší úhel s danou ulicí v daném uzlu. Mìøí se ve smìru hodinovıch ruèièek, podle parametru clockwise.
	 * Pokud je parametr quarter nulovı, poèítáme jenom s hlavními ulicemi.
	 *
	 * @param n Uzel 
	 * @param street Ulice
	 * @param clockwise Zda hledám po nebo proti smìru hodinovıch ruèièek
	 * @param quarter Ètvr ve které hledáme
	 * @return Ulice s nejmenším úhlem
	 */
	private static Street get_least_angled(Node n,Street street, Boolean clockwise, City_part quarter, boolean simple){
		 n.sort();
		 int index = n.streets.indexOf(street);
		 int count = n.streets.size();
		 
		 if(simple){
			 int summand = 1;
			 if(!clockwise)
				 summand = -1;
			 int counter = summand;
			 Street s = n.streets.get((index+count+counter) % count);
			 return s;
		 }
		 
		 if(quarter != null){
			 int summand = 1;
			 if(!clockwise)
				 summand = -1;
			 int counter = summand;
			 Street s = n.streets.get((index+count + counter) % count);
			 while(quarter.check_if_inside(s.node1) == Street_Result.fail || quarter.check_if_inside(s.node2) == Street_Result.fail){
				 counter += summand;
				 s = n.streets.get((index + count + counter) % count);
			 }
			 return n.streets.get((index + count + counter) % count);
		 }
		 else{
			 
			 int summand = 1;
			 if(!clockwise)
				 summand = -1;
			 int counter = summand;
			 while(!((n.streets.get((index+count + counter) % count)).major == Street_type.major)){
				 counter += summand;
			 }
			 return n.streets.get((index+count + counter) % count);
		 }
		 
		 
	 }
	
	/**
	 * Pøidá centrum mìsta do daného bodu.
	 *
	 * @param point the point
	 */
	public void add_city_center(Point point){
		this.citycenters.add(point);
	}

	private static boolean is_biggest (City_part bigger, City_part smaller){
		HashSet<Node> nodes = new HashSet<>();
		if(Math.abs(bigger.area - smaller.area)<0.001)
			return true;
			
		for(Street s: bigger.streets){
			if(bigger.streets.indexOf(s) == bigger.streets.lastIndexOf(s)){
				nodes.add(s.node1);
				nodes.add(s.node2);
			}
		}
		
		ArrayList<Node> borders = new ArrayList<>(nodes);
		/*if(bigger instanceof Block){
			((Block) bigger).remove_useless_nodes(borders);
		}*/
		return false;
		//return smaller.get_nodes().containsAll(borders);
	}
} 
