package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Node_Distance implements Serializable{

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
		ArrayList<Street> path =  new ArrayList<>();
		Node u = n2;
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
	
	

}
