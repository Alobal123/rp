package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;



/**
 * Tato t��da reprezentuje cel� m�sto a ��d� celou simulaci.
 */
public class City implements Serializable{

	/** Seznam v�ech Lut, kter� se maj� ve m�st� vyskytovat. */
	ArrayList<Lut> luts = new ArrayList<>();
	
	/** Graf uchov�vaj�c� s� ulic a staraj�c� se o jejich r�st. */
	Street_Network network;
	
	/** Instance t��dy nd se star� o hled�n� a uchov�v�n� nejkrat��ch cest. */
	Node_Distance nd;
	
	/** Celkov� hodnota m�sta. */
	double value = 0;
	
	Random rnd = new Random();
	Settings settings;
	
	/**
	 * Konstruktor
	 *
	 * @param network the network
	 */
	public City (Street_Network network){
		this.network = network;
		this.nd = new Node_Distance(network);
		this.settings = network.settings;
	}
	
	
	/**
	 * Hlavn� metoda t��dy City. Provede r�stu ulic, p�epo��t� dopravu a pot� postav� nov� ulice a bloky.
	 * Nov�m blok� ur�� jejich nejv�hodn�j�� Lut a pot� je�t� se pokus� vylep�it hodnotu cel�ho m�sta 
	 */
	public void step(){
		List<Node> changed_nodes = network.grow_major_streets(10);  //TODO pocitat kolik se ma narust hlavnich ulic
		HashSet<Node> nodes_to_check = new HashSet<>();
		for (Node node : changed_nodes) {
			nodes_to_check.add(node);
			for(Street s: node.streets){
				nodes_to_check.add(s.other_node(node));
			}
		}
		int additional_nodes = (int)(network.nodes.size()*settings.traffic_resample_rate);
		for (int i = 0; i < additional_nodes; i++) {
			int index = rnd.nextInt(additional_nodes);
			nodes_to_check.add(network.nodes.get(index));
		}
		for(Node node: nodes_to_check){
			node.remove_trips(nd);
		}
		nd = new Node_Distance(network);
		for(Node node: nodes_to_check){
			int residents = (int) node.residents;
			for (int i = 0; i < residents; i++) {
				Trip trip = generate_trip(node);
				node.trips.add(trip);
				trip.add_traffic(nd);
			}
		}
		build();
		for(Block b: choose_blocks()){
			reevaluate(b,null);
		}
	}
		
		/**
		 * Vytvo�� nov� trip, tj. pro dan� za��tek vybere konec.
		 *
		 * @param node Uzel, ve kter�m trip za��n�.
		 * @return Nov� trip.
		 */
		private Trip generate_trip(Node node){
			return new Trip(node,choose_end_of_trip(node),1);
		}
		
		/**
		 * Vybere c�l tripu, na z�klad� po�tu obyvatel a vzd�lenosti od za��tku.
		 *
		 * @param node Uzel, ve kter�m trip za��n�.
		 * @return C�lov� uzel tripu.
		 */
		private Node choose_end_of_trip(Node node){
			double [] distribution = new double [network.nodes.size()];
			double distSum = 0;
			for (int i = 0; i < network.nodes.size(); i++) {
				Node possible_end = network.nodes.get(i);
				if(possible_end != node)
					distribution[i] = possible_end.residents * Math.pow(Math.E,-1*Point.dist(node.point, possible_end.point));
				distSum +=distribution[i];
			}
			double rand = Math.random();
			double ratio = 1.0f / distSum;
			double tempDist = 0;
			for (int i = 0; i < distribution.length; i++) {
				tempDist += distribution[i];
				if (rand / ratio <= tempDist) 
					return network.nodes.get(i);
			
			}
			return null;
		}
		
		/**
		 * Zkontroluje a postav� v�echny ulice, uzly a bloky, kter� maj� dostate�nou hladinu dopravy, aby byly postaveny.
		 *
		 *
		 */
		private void build(){
			for(Node node: network.nodes){
				for (Street s: node.streets){
					if(!s.built && s.traffic >= settings.build_cost){
						s.built = true;
						s.node1.built = true;
						s.node2.built = true;
					}
				}
			}
			for(Quarter quarter: network.quarters){
				for(City_part cp: quarter.contained_city_parts){
					Block block = (Block) cp;
					if(!block.built)
						if(block.check_if_built()){
							block.built = true;
							block.build_all();
							block.choose_lut(luts, network, nd);
						}
				}
			}
		}
		
		/**
		 * N�hodn� vybere bloky a vr�t� je. Po�et vr�cen�ch blok� z�vis� na celkov�m po�tu blok� a p��slu�n� konstant�.
		 *
		 * @return Mno�ina blok�.
		 */
		private HashSet<Block> choose_blocks(){
			ArrayList<Block> allblocks = new ArrayList<>();
			HashSet<Block> chosen = new HashSet<>();
			for (Quarter q: network.quarters) {
				for(City_part cp : q.contained_city_parts){
					allblocks.add((Block)cp);
				}
			}
			for (int i = 0; i < (int) (settings.lut_resample_rate * allblocks.size()); i++) {
				chosen.add(allblocks.get(rnd.nextInt(allblocks.size())));
			}
			return chosen;
			
			
		}
	
		/**
		 * Vrac� celkovou hodnotu m�sta.
		 *
		 * @return Hodnota m�sta.
		 */
		private double evaluate(){
			return (1-settings.global_weight)*local_value() + settings.global_weight*global_value();
		}
		
		/**
		 * Vrac� glob�ln� hodnotu. Tj. veli�ina vyjad�uj�c�, jak se aktu�ln� stav m�sta bl�� tomu ide�ln�mu.
		 *
		 * @return Glob�ln� hodnota.
		 */
		private double global_value(){
			double sum = 0;
			for(Lut lut : luts){
				sum -= ((percent(lut) - lut.wanted_percentage)/0.05) * ((percent(lut) - lut.wanted_percentage)/0.05);
				
			}
			return sum;
		}
		
		/**
		 * Pod�l plochy, kter� zab�r� lut z parametru.
		 *
		 * @param lut 
		 * @return Pod�l
		 */
		private double percent(Lut lut){
			double value_sum = 0;
			double area_sum = 0;
			for (Quarter q : network.quarters) {
				for(City_part cp: q.contained_city_parts){
					Block b = (Block) cp;
					if(b.lut != null){
						if(b.lut == lut)
							value_sum += b.area;
						area_sum += b.area;
					}
				}
			}
			if(area_sum == 0)
				return 0;
			return value_sum/area_sum;
		}
		
		/**
		 * Vrac� lok�ln� hodnotu, ta z�vis� na hodnot� jednotliv�ch blok�.
		 *
		 * @return Lok�ln� hodnota
		 */
		private double local_value(){
			double value_sum = 0;
			double area_sum = 0;
			for (Quarter q : network.quarters) {
				for(City_part cp: q.contained_city_parts){
					Block b = (Block) cp;
					if(b.lut != null){
						value_sum += b.area * b.value;
						area_sum += b.area;
					}
				}
			}
			if(area_sum == 0)
				return 0;
			return value_sum/area_sum;
		}
		
		public void reevaluate(Block b, Lut bestlut){
			double max =-1000;
			Lut prevlut = b.lut;
			if(bestlut == null){
				for(Lut lut: luts){
					b.lut = lut;
					b.value = lut.evaluate(b, network, nd)-settings.lut_resample_cost;
					double value = evaluate();
					if(value > max){
						max = value;
						bestlut = lut;
					}
				}
			}
			if(bestlut != prevlut){
				for (Node node : b.get_nodes_once()) {
					node.residents -= b.lut.residents;
				}
				
				b.lut = bestlut;
				/*for(Node n: b.lot_borders){
					Iterator<Street> i = n.streets.iterator();
						while (i.hasNext()) {
						   Street s = i.next(); 
						  if(s.major == Street_type.lot_border)
							  i.remove();
						}
				}
				Iterator<Node> i = b.lot_borders.iterator();
				while (i.hasNext()) {
				  Node n = i.next(); 
				  if(n.major == Street_type.lot_border)
					  i.remove();
				}*/
				
				b.lot_borders = new ArrayList<>();
				for(Node n: b.get_nodes_once()){
					b.lot_borders.add(n.copy_node(b));
				}
				
				ArrayList<Node> new_lot_borders = new ArrayList<>();
				for(Node n: b.lot_borders){
					Node newnode = new Node(n.point.x, n.point.y, n.major,true);
					new_lot_borders.add(newnode);
				}
				
				HashSet<Street> already_added = new HashSet<>();
				for(Node n: b.lot_borders){
					for(Street s: n.streets){
						if(!already_added.contains(s)){
							b.substitute_streets(s,new_lot_borders);
							already_added.add(s);
						}
					}
				}
				b.remove_useless_nodes(new_lot_borders);
				
				b.lot_borders = new_lot_borders;
				
				b.contained_city_parts = new ArrayList<>();

				b.divide_to_convex();

				for(City_part current_part: b.contained_city_parts){
					((Lot)current_part).choose_and_place (b.lut,settings);
				}
				for (Node node : b.get_nodes_once()) {
					node.residents += b.lut.residents;
				}
				b.value = max;

			}
			else{
				b.lut = prevlut;
			}
			
		}
		
	}
