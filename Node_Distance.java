package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;


public class Node_Distance implements Serializable{

	private static final long serialVersionUID = -3185718565136028397L;

	
	
	public Node_Distance(Street_Network network){
	}
	

	public ArrayList<Street> get_Path(Node start, Node goal, Street_Network network){
		
		class AComparator implements Comparator<Node>{
			HashMap<Node, Double> gscore;
			HashMap<Node, Double> fscore;
			public AComparator(HashMap<Node, Double> gscore,HashMap<Node, Double> fscore){
				this.gscore = gscore;
				this.fscore = fscore;
			}
			@Override
			public int compare(Node o1, Node o2) {
				return Double.compare(gscore.get(o1)+fscore.get(o1),gscore.get(o2)+fscore.get(o2));
			}
		}
		
		HashMap<Node, Node> previous = new HashMap<>();
		HashMap<Node, Double> gscore = new HashMap<>();
		HashMap<Node, Double> fscore = new HashMap<>();
		
		AComparator comparator = new AComparator(gscore, fscore);
		
		HashSet<Node> closed = new HashSet<>();

		gscore.put(start, 0.0);
		fscore.put(start, Point.dist(start.getPoint(), goal.getPoint()));
		
		PriorityQueue<Node> queue = new PriorityQueue<>(comparator);
		queue.add(start);
		big: while(!queue.isEmpty()){
			Node current = queue.poll();
			if(current == goal){
				break big;
			}
			closed.add(current);
			for(Street s: current.streets){
				if((s.major==Street_type.major && s.traffic < network.settings.major_street_capacity)||
					s.traffic < network.settings.minor_street_capacity){
					Node neighbour = s.get_other_node(current);
					double tentative_gScore = gscore.get(current) + s.length;
					if(closed.contains(neighbour))
						continue;
					if(gscore.get(neighbour) == null)
						gscore.put(neighbour, Double.MAX_VALUE);
					if(tentative_gScore < gscore.get(neighbour)){
						gscore.put(neighbour, tentative_gScore);
						fscore.put(neighbour, Point.dist(neighbour.getPoint(), goal.getPoint()));
						previous.put(neighbour, current);
	                    if (!queue.contains(neighbour)) {
	                        queue.add(neighbour);
	                    }
					}
				
				}
			}
			
		}
		return reconstruct_path(goal, previous);

	}
	
	private ArrayList<Street> reconstruct_path(Node goal, HashMap<Node, Node> previous) {
		ArrayList<Street> path =  new ArrayList<>();
		Node u = goal;
		while(previous.containsKey(u)){
			for(Street s: u.streets){
				if(s.get_other_node(u) == previous.get(u)){
					path.add(s);
					break;
				}
			}
			u = previous.get(u);
		}
		
		return path;
	}
		
}
