package krabec.citysimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class Block_search {

	/**
	 * V dané ètvrti nalezne všechny bloky, tj. stìny grafu tvoøeného hlavními ulicemi ohranièujícími ètvr a vedlejšími ulicemi uvnitø.
	 *
	 * @param quarter Ètvr
	 */
	public static void search_for_blocks_inside_quarter(City_part quarter,List<Node> nodes,boolean whole,boolean blocks,Settings settings){
		LinkedHashSet<Street> to_search1 = new LinkedHashSet<Street>();
		LinkedHashSet<Street> to_search2 = new LinkedHashSet<Street>();
			
		for (Node n: nodes){
			for(Street s: n.streets){
				if(quarter.check_if_inside(s.node1)!=Street_result.fail && quarter.check_if_inside(s.node2)!=Street_result.fail){
					if(!to_search1.contains(s))
						to_search1.add(s);
					if(!to_search2.contains(s))
						to_search2.add(s);
				}
			}
		}
		
		while(!to_search1.isEmpty()){
			Iterator<Street> i = to_search1.iterator();
			Street s = i.next();
			find_blocks(s,quarter,to_search1,to_search2, true,whole,blocks,settings);
		}
		//if(quarter.contained_city_parts.size()>0 && quarter instanceof Quarter)
			//System.out.println(quarter.contained_city_parts.get(0).area);
		
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
	private static ArrayList<Street> find_blocks(Street s ,City_part quarter,LinkedHashSet<Street> to_search1,LinkedHashSet<Street> to_search2, Boolean from_1,Boolean whole,Boolean blocks,Settings settings){
				Node first = s.node2;
				Node second = s.node1;
				if(from_1){
					first = s.node1;
					second = s.node2;
					to_search1.remove(s);
				}
				else{
					to_search2.remove(s);
				}
				
				ArrayList<Street> streets = check_for_new_quarters(s, true, quarter,first,!blocks);
				ArrayList<Street> streets2 = new ArrayList<>();
				//if(blocks)
					streets2 = check_for_new_quarters(s, true, quarter,second,!blocks);
				
				Node prevnode = null;
				if(streets != null && streets2 !=null){
						City_part block;
						if(blocks){
							block = new Block(streets,first);
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
								if(s2.get_other_node(prevnode) == s2.node1){
									
									to_search1.remove(s2);
								}
								else{
									to_search2.remove(s2);
								}
								prevnode = s2.get_other_node(prevnode);
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
	static ArrayList<Street> check_for_new_quarters(Street street,Boolean clockwise,City_part quarter,Node startnode,boolean simple){
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
		
		n = s.get_other_node(n);
		
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
			n = s2.get_other_node(n);
			s = s2;
		}
		if(isDeformedCityPart(rt,visited,street)){
			return null;
		}	
		return rt;
	}
	private static boolean isDeformedCityPart(ArrayList<Street> streets, HashMap<Street, Integer> visited, Street street){
		boolean is_deformed = true;
		for(Street s: visited.keySet()){
			if(visited.get(s) <2 && s != street){
				return false;
			}
		}
		
		
		return is_deformed;
	}
	
	/**
	 * Zjistí zda jsou mìstské èásti bigger a smaller stejnì velké.
	 * @param bigger
	 * @param smaller
	 * @return
	 */
	private static boolean is_biggest (City_part bigger, City_part smaller){
		if(Math.abs(bigger.area - smaller.area)<0.001)
			return true;	
		return false;

	}
	/**
	 * Najde ulici která svírá nejmenší úhel s danou ulicí v daném uzlu. Mìøí se ve smìru hodinovıch ruèièek, podle parametru clockwise.
	 * Pokud je parametr quarter nulovı, poèítáme jenom s hlavními ulicemi.
	 *
	 * @param n Uzel 
	 * @param street Ulice
	 * @param clockwise Zda hledám po nebo proti smìru hodinovıch ruèièek
	 * @param quarter Ètvr ve které hledáme
	 * @param simple Pokud je true, hledáme mezi všemi ulicemi.
	 * @return Ulice s nejmenším úhlem
	 */
	private static Street get_least_angled(Node n,Street street, Boolean clockwise, City_part quarter, boolean simple){
		 
		 n.sort_streets_in_this_node();
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
			 while(quarter.check_if_inside(s.node1) == Street_result.fail ||
					 quarter.check_if_inside(s.node2) == Street_result.fail ||
					 quarter.check_if_inside(s.get_center()) == Street_result.fail){
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
}
