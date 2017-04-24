package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Tato t��da reprezentuje cel� m�sto a ��d� celou simulaci.
 */
public class City implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6617663931603831692L;

	/** Seznam v�ech Lut, kter� se maj� ve m�st� vyskytovat. */
	public ArrayList<Lut> luts = new ArrayList<>();
	
	/** Graf uchov�vaj�c� s� ulic a staraj�c� se o jejich r�st. */
	public Street_Network network;
	
	/** Instance t��dy nd se star� o hled�n� a uchov�v�n� nejkrat��ch cest. */
	Node_Distance nd;

	public Random_Wrapped rnd = new Random_Wrapped(2837);//2837
	
	/**
	 * Konstruktor
	 *
	 * @param network the network
	 */
	public City (Street_Network network){
		this.network = network;
		this.network.rnd = rnd;
		this.nd = new Node_Distance(network);
	}
	
	
	/**
	 * Hlavn� metoda t��dy City. Provede r�stu ulic, p�epo��t� dopravu a pot� postav� nov� ulice a bloky.
	 * Nov�m blok� ur�� jejich nejv�hodn�j�� Lut a pot� je�t� se pokus� vylep�it hodnotu cel�ho m�sta 
	 */
	public void step(){
		//R�st ulic
		List<Node> changed_nodes = network.grow_major_streets(10);
		//Po��t�n� dopravy
		LinkedHashSet<Node> nodes_to_check = new LinkedHashSet<Node>();
		for (Node node : changed_nodes) {
			nodes_to_check.add(node);
			for(Street s: node.streets){
				nodes_to_check.add(s.get_other_node(node));
			}
		}
		int additional_nodes = (int)(network.nodes.size()*network.settings.traffic_resample_rate);
		for (int i = 0; i < additional_nodes; i++) {
			int index = rnd.nextInt(additional_nodes,"choose_additional_nodes_to_change_traffic");
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
				trip.add_traffic(nd,network);
			}
		}
		//Stav�n� m�sta
		build_planned_city();
		//P�epo��t�n� lutu pro n�hodn� vybran� bloky
		for(Block b: choose_random_blocks()){
			change_lut_in_block(b,null);
			if(count_buildings(b) == 0){
				fill_with_most_buildings(b);
			}
		}
	}
		
		/**
		 * Vytvo�� nov� trip, tj. pro dan� za��tek vybere konec.
		 *
		 * @param start_node Uzel, ve kter�m trip za��n�.
		 * @return Nov� trip.
		 */
		private Trip generate_trip(Node start_node){
			return new Trip(start_node,choose_end_of_trip(start_node),1);
		}
		
		/**
		 * Vybere c�l tripu, na z�klad� po�tu obyvatel a vzd�lenosti od za��tku.
		 *
		 * @param start_node Uzel, ve kter�m trip za��n�.
		 * @return C�lov� uzel tripu.
		 */
		private Node choose_end_of_trip(Node start_node){
			return network.nodes.get(rnd.nextInt(network.nodes.size(),"choose_end_of_trip"));
		/*	double [] distribution = new double [network.nodes.size()];
			double distSum = 0;
			for (int i = 0; i < network.nodes.size(); i++) {
				Node possible_end = network.nodes.get(i);
				if(possible_end != start_node)
					distribution[i] = possible_end.residents * Math.pow(Math.E,-1*Point.dist(start_node.getPoint(), possible_end.getPoint()));
				distSum +=distribution[i];
			}
			System.out.println("distsum = " + distSum);
			double rand = rnd.nextDouble("choose_end_of_trip");
			//double rand = Math.random();
			double ratio = 1.0 / distSum;
			double tempDist = 0;
			for (int i = 0; i < distribution.length; i++) {
				tempDist += distribution[i];
				if (rand / ratio <= tempDist) 
					return network.nodes.get(i);
			
			}
			return null;*/
		}
		
		/**
		 * Zkontroluje a postav� v�echny ulice, uzly a bloky, kter� maj� dostate�nou hladinu dopravy, aby byly postaveny.
		 *
		 *
		 */
		private void build_planned_city(){
			for(Node node: network.nodes){
				for (Street s: node.streets){
					if(!s.built && s.traffic >= network.settings.build_cost){
						
						s.built = true;
						s.node1.setBuilt(true);
						s.node2.setBuilt(true);
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
							change_lut_in_block(block, block.find_best_lut(luts, network, nd, network.settings));
							if(count_buildings(block) == 0){
								fill_with_most_buildings(block);
							}
								
							
						}
				}
			}
		}
		
		private void fill_with_most_buildings(Block block){
			int max = -1;
			Lut best_lut = null;
			for(Lut lut : luts){
				change_lut_in_block(block, lut);
				int buildings_count = count_buildings(block);
				if(buildings_count > max){
					max = buildings_count;
					best_lut = lut;
				}
			}
			change_lut_in_block(block, best_lut);
		}
		
		private int count_buildings(Block block){
			int sum = 0;
			for(City_part cp: block.contained_city_parts){
				Lot lot = (Lot) cp;
				if(lot.building != null)
					sum++;
			}
			return sum;
				
		}
		
		/**
		 * N�hodn� vybere bloky a vr�t� je. Po�et vr�cen�ch blok� z�vis� na celkov�m po�tu blok� a p��slu�n� konstant�.
		 *
		 * @return Mno�ina blok�.
		 */
		private LinkedHashSet<Block> choose_random_blocks(){
			ArrayList<Block> allblocks = new ArrayList<>();
			LinkedHashSet<Block> chosen = new LinkedHashSet<>();
			for (Quarter q: network.quarters) {
				for(City_part cp : q.contained_city_parts){
					allblocks.add((Block)cp);
				}
			}
			for (int i = 0; i < (int) (network.settings.lut_resample_rate * allblocks.size()); i++) {
				chosen.add(allblocks.get(rnd.nextInt(allblocks.size(),"choose_blocks_to_change_lut")));
			}
			return chosen;
			
			
		}
	
		/**
		 * Vrac� celkovou hodnotu m�sta.
		 *
		 * @return Hodnota m�sta.
		 */
		private double evaluate(){
			return (1-network.settings.global_weight)*get_local_value() + network.settings.global_weight*get_global_value();
		}
		
		/**
		 * Vrac� glob�ln� hodnotu. Tj. veli�ina vyjad�uj�c�, jak se aktu�ln� stav m�sta bl�� tomu ide�ln�mu.
		 *
		 * @return Glob�ln� hodnota.
		 */
		private double get_global_value(){
			double sum = 0;
			for(Lut lut : luts){
				sum -= ((get_ratio_of_lut(lut) - lut.wanted_percentage)/0.05) * ((get_ratio_of_lut(lut) - lut.wanted_percentage)/0.05);
				
			}
			return sum;
		}
		
		/**
		 * Pod�l plochy, kter� zab�r� lut z parametru.
		 *
		 * @param lut 
		 * @return Pod�l
		 */
		private double get_ratio_of_lut(Lut lut){
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
		private double get_local_value(){
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
		
		public void change_lut_in_block(Block b, Lut bestlut){
			double max =-1000;
			Lut prevlut = b.lut;
			if(bestlut == null){
				for(Lut lut: luts){
					b.lut = lut;
					b.value = lut.evaluate(b, network, nd,network.settings)- network.settings.lut_resample_cost;
					double value = evaluate();
					if(value > max){
						max = value;
						bestlut = lut;
					}
				}
			}
			if(bestlut != prevlut){

				
				b.lot_borders = new ArrayList<>();
				for(Node n: b.get_nodes_once()){
					b.lot_borders.add(n.copy_node_with_streets_from_block(b));
				}
				
				ArrayList<Node> new_lot_borders = new ArrayList<>();
				
				for(Node n: b.lot_borders){
					Node newnode = new Node(n.getPoint().getX(), n.getPoint().getY(), n.major,true,null);
					new_lot_borders.add(newnode);
				}
				
				ArrayList<Street> already_added = new ArrayList<>();
				for(Street s: b.streets){
						if(!already_added.contains(s)){
							b.substitute_streets(s,new_lot_borders);
							already_added.add(s);
						}
					}
				
				b.remove_useless_nodes(new_lot_borders);
				b.lot_borders = new_lot_borders;
				b.contained_city_parts = new ArrayList<>();
				b.choose_lut(luts, bestlut, network, nd,network.settings);

			}
			else{
				b.lut = prevlut;
			}
			
		}

		
	}
