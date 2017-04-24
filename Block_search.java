package krabec.citysimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class Block_search {

	/**
	 * V dan� �tvrti nalezne v�echny bloky, tj. st�ny grafu tvo�en�ho hlavn�mi ulicemi ohrani�uj�c�mi �tvr� a vedlej��mi ulicemi uvnit�.
	 *
	 * @param quarter �tvr�
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
	 * Hled� blok, kter� obsahuje ulici s ve �tvrti.
	 *
	 * @param s Ulice s
	 * @param quarter �tvr�
	 * @param to_search1 Mno�ina ulic ze kter�ch je je�t� t�eba hledat bloky ve sm�ru node1 -> node2
	 * @param to_search2 Mno�ina ulic ze kter�ch je je�t� t�eba hledat bloky ve sm�ru node2 -> node1
	 * @param from_1 Sm�r hled�n� bloku
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
	 * Pokus� se naj�t m�stskou ��st vzniklou p�id�n�m nov� vytvo�en� ulice. Vrac� null pokud ��dnou nenajde. 
	 * Pokud nen� parametr quarter null, hled� blok v t�to �tvrti. Jinak hled� pouze hlavn� ulice.
	 * Vrac� seznam ulic ohrani�uj�c� nalezenou m�stskou ��st.
	 *
	 * @param newstreet Nov� ulice
	 * @param clockwise Zda se m� hledat po sm�ru hodinov�ch ru�i�ek
	 * @param quarter �tvr�
	 * @param startnode Uzel, ze kter�ho za��n�me hledat
	 * @return Seznam ulic ohrani�uj�c�ch nalezenou ��st m�sta
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
	 * Zjist� zda jsou m�stsk� ��sti bigger a smaller stejn� velk�.
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
	 * Najde ulici kter� sv�r� nejmen�� �hel s danou ulic� v dan�m uzlu. M��� se ve sm�ru hodinov�ch ru�i�ek, podle parametru clockwise.
	 * Pokud je parametr quarter nulov�, po��t�me jenom s hlavn�mi ulicemi.
	 *
	 * @param n Uzel 
	 * @param street Ulice
	 * @param clockwise Zda hled�m po nebo proti sm�ru hodinov�ch ru�i�ek
	 * @param quarter �tvr� ve kter� hled�me
	 * @param simple Pokud je true, hled�me mezi v�emi ulicemi.
	 * @return Ulice s nejmen��m �hlem
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
