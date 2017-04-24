package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	/** Všechny použitelné køižovatky. */
	public List<Crossroad> all_crossroads;
	
	/** Seznam všech ètvrtí ve mìstì. */
	public List<Quarter> quarters = new ArrayList<>();
	/** Seznam všech uzlù v grafu ulic. */
	public List<Node> nodes;

	public Random_Wrapped rnd;
	
	Crossroad end_of_road;
	
	/** Seznam všech center rùstu ve mìstì. */
	List<Point> growthcenters  = new ArrayList<Point>();
	
	/** Seznam všech center mìsta. */
	List<Point> citycenters = new ArrayList<Point>();
	
	/** Seznam všech uzlù pøipravených k rùstu. */
	List<Node> to_grow_nodes;
	
	/** Aktuálnì zpracovávaný novì vytvoøený uzel. Figuruje v metodì grow_street.*/
	private Node current_new_node;
	
	/**Aktuálnì zpracovávaná novì vytvoøená ulice. Figuruje v metodì grow_street. */
	private Street current_new_street;
	
	/** Seznam uzlù, které se nìjakým zpùsobem zmìnily. */
	private List<Node> changed_nodes;
	public Settings settings;
	
	/**
	 * Konstruktor 
	 *
	 */
	public Street_Network(List<Crossroad> all_crossroads,Settings settings){
		
		end_of_road = Crossroad.get_new_end_of_Road();
		all_crossroads.add(end_of_road);
		for (Crossroad c: all_crossroads) {
			c.get_viable_crossroads(all_crossroads);
		}
		 
		nodes = new ArrayList<>();
		growthcenters = new ArrayList<>();
		to_grow_nodes = new ArrayList<>();
		this.all_crossroads = all_crossroads;

		Node node1 = (new Node(0, 0, Street_type.major,end_of_road));	
		nodes.add(node1);
		node1.angle = 270;
		
		Node node2 = new Node(-50, 0, Street_type.major,end_of_road);
		nodes.add(node2);
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
	 * Zkusí nechat vyrùst i nových hlavních ulic. Vybere uzly k rùstu a z nich se pokusí vytvoøit novou ulici.
	 * Poté nalezne novì vzniklé ètvrtì a vyplní je vedlejšími ulicemi. Najde takto vzniklé bloky. Vrací všechny zmìnìné uzly,
	 * pro které je tøeba pøepoèítat hodnotu doprav.
	 *
	 * @param number_of_streets Poèet uzlù k rùstu
	 * @return Seznam zmìnìných uzlù
	 */
	public List<Node> grow_major_streets(int number_of_streets){
		changed_nodes = new ArrayList<>();
		ArrayList<Node> chosen_nodes = choose_nodes_to_grow(to_grow_nodes,number_of_streets);
		
		for (Node node : chosen_nodes) {
			Street newstreet = grow_street(node,Street_type.major,null,to_grow_nodes);
			if(newstreet != null){
				newstreet.length = newstreet.compute_length();
				ArrayList<Street> new_quarter = Block_search.check_for_new_quarters(newstreet,true,null,newstreet.node2,false);
				ArrayList<Street> new_quarter2 = Block_search.check_for_new_quarters(newstreet,true,null,newstreet.node1,false);		
				
				if(new_quarter != null && new_quarter2 != null){
					if(new_quarter.size() < new_quarter2.size()){
						Quarter q = new Quarter(new_quarter,newstreet.node2);
						quarters.add(q);
						grow_minor_streets(q);
					}
					else{
						Quarter q = new Quarter(new_quarter2,newstreet.node1);
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
		Street old_street = quarter.get_longest_street();
		Node seed_node = make_seed_node(to_grow_nodes, old_street);

		while(!to_grow_nodes.isEmpty()){
			ArrayList<Node> chosen_nodes = choose_nodes_to_grow(to_grow_nodes,10);
			for (Node n: chosen_nodes){
				 Street s = grow_street(n,Street_type.minor, quarter, to_grow_nodes);
				 if(s!=null){
					 s.length = s.compute_length();
					 some_minor_street = true;
				 }
			}
		}
		
		if(!some_minor_street){
			//remove_node_with_180_180_crossroad(seed_node, old_street);
			//nodes.remove(seed_node);
			//to_grow_nodes.remove(seed_node);
		}
		
		if(some_minor_street){
			Block_search.search_for_blocks_inside_quarter(quarter,quarter.filter_nodes_outside_this_quarter(nodes),false,true,settings);
		}
		
		
		if(!some_minor_street|| quarter.contained_city_parts.size()==0){

			Block_search.search_for_blocks_inside_quarter(quarter,quarter.filter_nodes_outside_this_quarter(nodes),true,true,settings);
			if(quarter.contained_city_parts.get(0).streets.size() > quarter.contained_city_parts.get(1).streets.size())
				quarter.contained_city_parts.remove(1);
			else
				quarter.contained_city_parts.remove(0);

		}
		if(quarter.contained_city_parts.size()>1)
			repair(quarter);
		//control(quarter);
	}
	private void control(Quarter q) {
		ArrayList<City_part> to_remove = new ArrayList<>();
		for(City_part cp: q.contained_city_parts){
				for(Node n: cp.get_nodes_once())
					if(q.check_if_inside(n) == Street_result.fail){
						to_remove.add(cp);
						break;
					}
		}
		if(!to_remove.isEmpty())
		q.contained_city_parts.removeAll(to_remove);
		
	}
	
	private void repair( Quarter quarter){

				Iterator<City_part> i = quarter.contained_city_parts.iterator();
				while (i.hasNext()) {
				   City_part block = i.next();
				   if(block.area-quarter.area > 0.0001)
					   i.remove();
				}
	
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
	    double [] distribution = new double [to_grow_nodes.size()]; 
	    
	    double distSum = 0;
		for (int i = 0; i < to_grow_nodes.size(); i++) {
			distribution[i] = Math.pow(Math.E,-1*settings.focus_constant*Point.get_smallest_distance(to_grow_nodes.get(i).getPoint(), growthcenters));
			if(to_grow_nodes.get(i).streets.size() == 2)
				distribution[i]+=0.5;
			distSum +=distribution[i];
		}
		for (int j = 0; j < number_of_streets; j++) {		
				double rand = rnd.nextDouble("choose_nodes_to_grow , we have " + quarters.size());
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
	 * Vytvoøí novou ulici z uzlu, pokud je to možné. Pokud není parametr quarter nulový, pak rosotu ulice jen v rámci této ètvrti.
	 *
	 * @param oldnode Uzel ze kterého roste nová ulice.
	 * @param major Zda má být nová ulice hlavní
	 * @param quarter Ètvr ve které má nová ulice být
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @return Nová ulice
	 */
	 private Street grow_street(Node oldnode,Street_type major, Quarter quarter, List<Node> to_grow_nodes){
		 if(oldnode.crossroad.viable_crossroads.size() == 0){
				to_grow_nodes.remove(oldnode);
				return null;
		}
		
		boolean succes = true;
		int limit = 5;							
		cyklus: for (int i = 0; i < limit; i++) {
			succes = true;
			int a = rnd.nextInt(oldnode.crossroad.viable_crossroads.size(),"choose_which_crossroad");
			Crossroad new_crossroad = oldnode.crossroad.viable_crossroads.get(a);
			Crossroad old_crossroad = oldnode.crossroad;
			double angle = (oldnode.angle + oldnode.crossroad.get_relative_angle(a)+360) % 360;
			double length;
			if(major == Street_type.major)
				length= rnd.nextDouble("choose_major_length") * (settings.major_max_length - settings.major_min_length) + settings.major_min_length +settings.major_prolongation;
			else
				length = rnd.nextDouble("choose_major_length") * (settings.minor_max_length - settings.minor_min_length) + settings.minor_min_length +settings.minor_prolongation; 
			
			current_new_node = Node.make_new_node(angle, major, oldnode,length,end_of_road);
			current_new_node.angle = angle;
			current_new_street = new Street( oldnode, current_new_node,major);
			current_new_node.streets.add(current_new_street);
			current_new_node.angle = current_new_node.compute_angle();
			oldnode.streets.add(current_new_street);
			oldnode.crossroad = new_crossroad;
			oldnode.angle = oldnode.compute_angle();
			
			nodes.add(current_new_node);
			to_grow_nodes.add(current_new_node);

			Street_result result = Street_result.not_altered;
			
			if(succes){
				result = check_for_crosses(current_new_street, current_new_node, oldnode,major,to_grow_nodes,quarter);
				if(result == Street_result.not_altered){
					if(major == Street_type.major)
						length -= settings.major_prolongation;	
					else
						length -= settings.minor_prolongation;
					double dx = Math.sin(angle * Math.PI / 180) * length;
					double dy = Math.cos(angle * Math.PI / 180) * length;
					current_new_node.setPoint(new Point(oldnode.getPoint().getX() + dx, oldnode.getPoint().getY() + dy));
				}
				else if (result == Street_result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			
			if(succes && !(major == Street_type.major)){
				result = quarter.check_if_inside(current_new_node);
				if(result == Street_result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			if(succes && major == Street_type.major){
				result = check_if_in_quarter(current_new_street);
				if(result == Street_result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			if(succes){
				result = check_for_close_streets(current_new_node,current_new_street,major);
				if(result == Street_result.fail){
					revert_changes(oldnode, old_crossroad,major,to_grow_nodes);
					succes = false;
				}
			}
			if(succes){
				result = check_for_close_node(current_new_street, current_new_node,oldnode,major,to_grow_nodes);
				if(result == Street_result.fail){
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
	 * Zkontroluje, zda novì vytvoøená ulice a novì vytvoøený uzel, nejsou pøíliš blízko jiného uzlu, a zda se tyto uzly nedají slouèit v jeden,
	 * a provede všechny potøebné zmìny, pokud to lze.
	 *
	 * @param s Nová ulice
	 * @param newnode Nový uzel
	 * @param oldnode Starý uzel - ze kterého vyrostla nová ulice
	 * @param major Zda stavíme hlavní ulici
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	private Street_result check_for_close_node(Street s, Node newnode, Node oldnode,Street_type major,List<Node> to_grow_nodes) {
		
		double constant;
		if(major==Street_type.major)
			 constant = settings.major_close_node_constant;
		else
			constant = settings.minor_close_node_constant;
		for (Node n: nodes) {
			
			if(s.node1 != n && s.node2!=n && Point.dist(n.getPoint(), newnode.getPoint()) < constant){
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
					
					return Street_result.altered;
				}
				else{
					oldnode.streets.add(s);
					oldnode.streets.remove(newstreet);
					n.streets.remove(newstreet);
					return Street_result.fail;
				}
			}
		}
		return Street_result.not_altered;
		
	}

	/**
	 *Kontroluje, zda se nová ulice nìkdì køíží s jinou ulicí a zda v místì køížení nelze vytvoøit novou køižovatku a uzel. 
	 *Pokud ano, provede všechny potøebné zmìny.
	 *
	 * @param newstreet Nová ulice
	 * @param newnode  Nový uzel
	 * @param oldnode Starý uzel - ze kterého vyrostla nová ulice
	 * @param major Zda stavíme hlavní ulici
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @param quarter Ètvr ve které ulice stavíme (nebo null)
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	
	private Street_result check_for_crosses(Street newstreet, Node newnode, Node oldnode,Street_type major,List<Node> to_grow_nodes,Quarter quarter){
		Street intersecting = null;
		Point intersection = null;
		double min = Point.dist(oldnode.getPoint(), newnode.getPoint());
		for (Node n: nodes) {
			for(Street street: n.streets){
				if(!street.equals(newstreet)){
					Point intersect = null;
					intersect  = Street.getIntersection(street, newstreet);
					
					if(intersect != null && Point.dist(oldnode.getPoint(), intersect) < min
							&& Point.dist(intersect, street.node1.getPoint()) > 0.00001 && Point.dist(intersect, street.node2.getPoint()) > 0.00001){
						intersecting = street;
						intersection = intersect;
						min = Point.dist(oldnode.getPoint(), intersect);
					}
				}
			}
		}
		
		if(intersecting != null){
			Node trynode = new Node(intersection.getX(), intersection.getY(), intersecting.major,intersecting.built,end_of_road);
			
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
					return Street_result.altered;
				}
			}
			
			return Street_result.fail;
		}
		
		return Street_result.not_altered;
	}
	
	
	private Street_result check_if_in_quarter(Street newstreet){
		Node middlenode = new Node((newstreet.node1.getPoint().getX()+newstreet.node2.getPoint().getX())/2, (newstreet.node1.getPoint().getY()+newstreet.node2.getPoint().getY())/2, null,end_of_road);
		for(Quarter q: quarters){
			if(q.check_if_inside(middlenode) == Street_result.not_altered){
				return Street_result.fail;
			}
		}
		return Street_result.not_altered;
	}
	/**
	 * Zkontroluje, zda se nový uzel nenachází pøíliš blízko nìjaké již existující ulici.
	 *
	 * @param newstreet Nová ulice
	 * @param newnode  Nový uzel
	 * @param major Zda stavíme hlavní ulici
	 * @return Zda je nová ulice v poøádku a zda se zmìnila.
	 */
	private Street_result check_for_close_streets(Node newnode,Street newstreet, Street_type major){
		double constant;
		if(major==Street_type.major)
			 constant = settings.major_close_street_constant;
		else
			constant = settings.minor_close_street_constant;
		
		
		for (Node n: nodes) {
			for(Street s: n.streets){
				if((s.node1 != newnode && s.node2 != newnode && newnode.compute_distance_from_street(s) < constant) 
						|| (newstreet.node1 != n && newstreet.node2 != n && n.compute_distance_from_street(newstreet) < constant)){
					return Street_result.fail;
				}
			}
		
		}
		return Street_result.not_altered;
	}
	


	/**
	

	/**
	 * Vrátí všechny zmìny zpùsobené rùstem nové ulice, pokud ulice nevyhovuje. 
	 *
	 * @param oldnode Starý uzel - ze kterého vyrostla nová ulice
	 * @param old_crossroad Pùvodní køižovatka starého uzlu
	 * @param major Zda stavíme hlavní ulici
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 */
	private void revert_changes(Node oldnode,Crossroad old_crossroad,Street_type major,List<Node> to_grow_nodes){

		current_new_node.streets.remove(current_new_street);
		oldnode.streets.remove(current_new_street);
		oldnode.crossroad = old_crossroad;
		oldnode.angle = oldnode.compute_angle();
		nodes.remove(current_new_node);
		to_grow_nodes.remove(current_new_node);
		if(current_new_node.streets.size() == 2 && Math.abs(180 - Street.get_angle(current_new_node.streets.get(0), current_new_node.streets.get(1)))<0.0001){
			remove_node_with_180_180_crossroad(current_new_node,null);
		}

	}
	
	
	private void remove_node_with_180_180_crossroad(Node n,Street old_street){

		Node node1 = n.streets.get(0).get_other_node(n);
		Node node2 = n.streets.get(1).get_other_node(n);
		node1.remove_street_to_node(n);
		node2.remove_street_to_node(n);
		
		Street_type type;
		if(node1.major==Street_type.major && node2.major==Street_type.major && current_new_node.major==Street_type.major )
			type = Street_type.major;
		else
			type = Street_type.minor;
		Street street;
		if(old_street==null)
			street = new Street(node1, node2, type,node1.isBuilt() && node2.isBuilt());
		else
			street = old_street;

		node1.streets.add(street);
		node2.streets.add(street);	

	}

	
	/**
	 * Vytvoøí nový uzel uprostøed z jedné ulic ètvrti.
	 *
	 * @param to_grow_nodes Uzly, které mohou dále rùst
	 * @param quarter Ètvr
	 */
	private Node make_seed_node(List<Node> to_grow_nodes, Street oldstreet){
			Point x = oldstreet.node1.getPoint();
			Point y = oldstreet.node2.getPoint();
			double newx = (x.getX()*0.51 + y.getX()*0.49 );
			double newy = (x.getY()*0.51 + y.getY()*0.49 );
			Node new_node = new Node(newx,newy, Street_type.major,end_of_road);
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
			
			return new_node;
			}
	
	/**
	 * Najde n nejbližších blokù danému bloku (Pomocí bfs).
	 *
	 * @param n Poèet blokù k nalezení
	 * @param block Blok ze kterého hledáme
	 * @return Seznam nejbližších blokù
	 */
	ArrayList<Block> get_nearest_blocks(int n,Block block){
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
				if(!visited.contains(s.get_other_node(node))){
					queue.add(s.get_other_node(node));
					visited.add(s.get_other_node(node));
				}
			}
		}
		rt.remove(block);
		return rt;
		
	}
	
	/**
	 * Vrátí všechny bloky nacházející se v polomìru r. Bereme bežnou euklidovskou vzdálenost a mìøíme od støedu blokù.
	 *
	 * @param radius Polomìr
	 * @param block Blok od kterého mìøíme
	 * @return Seznam blokù v polomìru r
	 */
	ArrayList<Block> get_blocks_in_radius(double radius, Block block){
		int n = (int)(4 * (radius/settings.minor_min_length)*(radius/settings.minor_min_length));
		ArrayList<Block> candidates = get_nearest_blocks(n, block);
		Iterator<Block> i = candidates.iterator();
		while(i.hasNext()){
			Block b = i.next();
			if(Point.dist(b.center, block.center)>radius)
				i.remove();
		}
		return candidates;
		
	}
	

	

} 
