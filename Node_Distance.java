package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Node_Distance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3185718565136028397L;
	Street_Network network;
	double[][] dist;
	Integer[][] next;
	
	
	public Node_Distance(Street_Network network){
		this.network = network;
		//floyd_warshall();
	}
	
	public void floyd_warshall(){
		System.out.println("Zacinam");
		List<Node> nodes = network.nodes;
		int n = nodes.size();
		double [][] rt = new double [n][n];
		Integer[][] next = new Integer [n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(i!=j)
					rt[i][j] = Double.MAX_VALUE;
				else 
					rt[i][j] = 0;
			}
		}
		for (int i = 0; i < n; i++) {
			Node node = nodes.get(i);
			Iterator<Street> iterator = node.streets.iterator();
			while(iterator.hasNext()){
				Street s = iterator.next();
				Node node2 = s.other_node(node);
				int j = nodes.indexOf(node2);
				if(j==-1){
					System.out.println("Chyba chycena");
					iterator.remove();
					//node2.crossroad.find(all_crossroads, node2);
				}
				else{	
					rt[i][j] = s.length;
					next[i][j] = j;
				}
			}
		}
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j< n; j++) {
					if (rt[i][j] > rt[i][k] + rt[k][j] ){
						rt[i][j] = rt[i][k] + rt[k][j];
						next[i][j] = next [i][k];
					}
				}
			}
		}
		dist = rt;
		this.next = next;
		System.out.println("Konec");
	}
	
	public ArrayList<Street> get_path(int u, int v){
		ArrayList<Street> path = new ArrayList<>();
		if(next[u][v] == null)
			return null;
		while(u != v){
			Node node1 = network.nodes.get(u);
			Node node2 = network.nodes.get(next[u][v]);
			for(Street s: node1.streets){
				if(s.other_node(node1) == node2){
					path.add(s);
					break;
				}
			}
			u = next[u][v];
		
		}
		return path;
	}
	public ArrayList<Street> get_path(Node n1, Node n2){
		int u = network.nodes.indexOf(n1);
		int v = network.nodes.indexOf(n2);
		return get_path(u, v);
	}
	public ArrayList<Node> get_path_nodes(int u, int v){
		ArrayList<Node> path = new ArrayList<>();
		if(next[u][v] == null)
			return null;
		path.add(network.nodes.get(u));
		while(u != v){
			path.add(network.nodes.get(u));
			u = next[u][v];
		
		}
		return path;
	}
	public ArrayList<Node> get_path_nodes(Node n1, Node n2){
		int u = network.nodes.indexOf(n1);
		int v = network.nodes.indexOf(n2);
		return get_path_nodes(u, v);
	}
	
	
	
	
	public ArrayList<Street> get_Path_Astar(Node start, Node goal){
		
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
		for (Node node : network.nodes) {
			gscore.put(node, Double.MAX_VALUE);
			fscore.put(node, Double.MAX_VALUE);
		}
		gscore.put(start, 0.0);
		fscore.put(start, Point.dist(start.point, goal.point));
		
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
					Node neighbour = s.other_node(current);
					double tentative_gScore = gscore.get(current) + s.length;
					if(closed.contains(neighbour))
						continue;
					if(tentative_gScore < gscore.get(neighbour)){
						gscore.put(neighbour, tentative_gScore);
						fscore.put(neighbour, Point.dist(neighbour.point, goal.point));
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
				if(s.other_node(u) == previous.get(u)){
					path.add(s);
					break;
				}
			}
			u = previous.get(u);
		}
		
		return path;
	}

	public ArrayList<Street> get_path_dijkstra(Node n1, Node n2){
		
		HashMap<Node, Node> previous = new HashMap<>();
		for (Node node : network.nodes) {
			node.distance = Double.MAX_VALUE;
		}
		n1.distance = 0;
		PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				return Double.compare(o1.distance,o2.distance);
			}
			
		});
		queue.addAll(network.nodes);
		
		big: while(!queue.isEmpty()){
			Node node1 = queue.poll();
			for(Street s: node1.streets){
				if((s.major==Street_type.major && s.traffic < network.settings.major_street_capacity)||
						s.traffic < network.settings.minor_street_capacity){
				Node node2 = s.other_node(node1);
				double alt = node1.distance + s.length;
				if(alt < node2.distance){
					node2.distance = alt;
					previous.put(node2, node1);
					if(node2 == n2)
						break big;
					queue.remove(node2);
					queue.add(node2);
					
				}	
			}
			}
		}
		
		return reconstruct_path(n2, previous);
		
	}
	
	

}
