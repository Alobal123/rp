package krabec.citysimulator;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import krabec.citysimulator.ui.City_window;

public class Tests {

	public static void main(String[] args) {
		System.out.println(Street.are_parallel(new Street(new Node(0, 0, null, null), new Node(1, 0, null, null), null),
				(new Street(new Node(0, 0, null, null), new Node(-2, 0, null, null), null))));
		long sum = 0;
		for (int i = 5000; i < 6000; i++) {
			long time1 = System.nanoTime();
			City city = krabec.citysimulator.ui.City_window.create_city();
			
			//City city2 = krabec.citysimulator.ui.City_window.create_city();
		
			//city.network.settings.lut_resample_rate=0.0;
			city.rnd.rnd.setSeed(i);
			//city2.network.settings.lut_resample_rate=0.0;
			int stepnumber =0;
			try{
				//System.out.println(seed);
				for (int j = 0; j < 100; j++) {
					city.step();
					stepnumber = j;
					/*city2.step();
					if(!are_the_same(city, city2)){
						city.rnd.writer.close();
						city2.rnd.writer.close();
						System.out.println(city.rnd.counter);
						break;
					}*/
					
				}
				//show_window(city);
				//show_window(city2);
				long time2 = System.nanoTime();
				sum +=(time2-time1);
				System.out.println("Quarters of city " +i+" " + city.network.quarters.size() + "in time "+((time2-time1)/1000/1000/100));
				//System.out.println("Quarters of city2 " +i+" " + city2.network.quarters.size() + "in time "+((time2-time1)/1000/1000/100));
			}			
			
			catch(Exception e){
				System.out.println(e + " in step " + stepnumber);
				System.out.println(e.getStackTrace());
				//throw e;
			}
			//show_window(city);
		}
		
		//System.out.println(sum/10.0/1000/1000/100);
			
		}
		
		

	
	private static boolean are_the_same(City c1, City c2){
		if(c1.network.nodes.size() != c2.network.nodes.size())
			return false;
		for (int i = 0; i < c1.network.nodes.size(); i++) {
			if(!c1.network.nodes.get(i).equals(c2.network.nodes.get(i))){
				return false;
			}
		}
		return true;
	}
	
	
	public static boolean vejde_se(double size_of_building, double size_of_square) {
		City city = create_city(size_of_building);
		
		city.network.settings.major_close_node_constant = 3.0;
		city.network.settings.major_close_street_constant = 3.0;
		city.network.settings.major_min_length = 20.0;
		city.network.settings.major_max_length = 40.0;
		city.network.settings.build_cost = 0.0;
		
		city.network.settings.street_width = 2.0;
		city.network.settings.street_offset= 0.0;
		
		create_rectangle(size_of_square+2, (size_of_square)+2, city.network);
	try{
		while(city.network.quarters.size() < 1){
			city.step();
		}
	}
	catch(Exception e){
		
	}
		show_window(city);
		return print_city_blocks(city.network.quarters);
		
		

	}
	
	private static boolean print_city_blocks(List<Quarter> quarters){
		for(Quarter q: quarters){
			System.out.println("Quarter: ");
			for(City_part cp : q.contained_city_parts){
				Block b = (Block) cp;
				System.out.println("	Block: " + b.lut);
				for(City_part cp2 : b.contained_city_parts){
					Lot l = (Lot) cp2;
					System.out.println("                  Lot: "+ l.building +"  area = "+ l.area);
					if(l.building != null)
						return true;
				}
			}
		}
		return false;
	
	}
	
	private static City create_city(double building_size){
		
		ArrayList<Crossroad> all_crossroads = new ArrayList<>();
		create_crossroad(all_crossroads,2, 180.0, 180.0);
		create_crossroad(all_crossroads,2, 90, 270);
		create_crossroad(all_crossroads,3, 90,90,180.0);	
		for (Crossroad c: all_crossroads) {
			c.get_viable_crossroads(all_crossroads);
		}
		
		Street_Network network = new Street_Network(all_crossroads,new Settings());
		
		City city = new City(network);
		Building b = new Building(building_size*2,building_size*2,"B");
		Building b2 = new Building(building_size,building_size,"B2");
		
		Lut lut =  new Lut("Lut", 4, 1,Color.red);
		lut.add_val(new Valuation((float)0.4, Valuation_Types.citycenter,Mapping.linear_down,0,250));
		
		Lut lut2 =  new Lut("Lut2", 4, 1,Color.orange);
		lut.add_val(new Valuation((float)0.4, Valuation_Types.constant,Mapping.constant,0,0));
		
		lut2.getBuildings().add(b2);
		lut.getBuildings().add(b);
		city.luts.add(lut);
		city.luts.add(lut2);
		return city;
		
		
	}
	private static void show_window(City city){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					City_window frame = new City_window();
					frame.change_city(city);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void create_rectangle(double a, double b, Street_Network network){
		network.nodes = new ArrayList<>();
		Node n1 = new Node(0, 0, Street_type.major,null);
		Node n2 = new Node(a, 0, Street_type.major,null);
		Node n3 = new Node(0, 2*b, Street_type.major,null);
		Node n4 = new Node(a, b, Street_type.major,null);
		network.nodes.add(n1);
		network.nodes.add(n2);
		network.nodes.add(n3);
		network.nodes.add(n4);
		City_window.create_street(n1, n2, Street_type.major);
		City_window.create_street(n1, n3, Street_type.major);
		City_window.create_street(n2, n4, Street_type.major);
		for(Node n : network.nodes){
			n.crossroad = Crossroad.find(network.all_crossroads, n);
			//System.out.println(n.crossroad);
			n.angle = n.compute_angle();
			//System.out.println(n.angle);
		}
		network.to_grow_nodes = new ArrayList<>();
		network.to_grow_nodes.add(n4);
		
	}

	
	private static void create_crossroad(List<Crossroad> all_crossroads,int n, double... angles){
		Crossroad c = new Crossroad(angles);
		all_crossroads.addAll(c.get_all_rotations());
	}
}
