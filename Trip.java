package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Reprezentuje cestu nìjakých obyvatel mìsta z jednoho místa na jiné.
 */
public class Trip implements Serializable{

	/**Startovní uzel tripu*/
	Node start;
	
	/** Cílový uzel tripu */
	Node end;
	
	/** Nejkratší cesta ze startu do cíle */
	ArrayList<Street> path;
	
	/** Poèet obyvatel na tripu */
	double volume;
	
	/**
	 * Konstruktor
	 *
	 * @param start the start
	 * @param end the end
	 * @param volume the volume
	 */
	public Trip (Node start,Node end, double volume){
		this.start = start;
		this.end = end;
		this.volume = volume;
	}
	
	/**
	 * Pøiète hodnotu volume k dopravì, ke všem ulicím mezi startem a cílem a zapamatuje si tuto nejkratší cestu.
	 *
	 * @param nd the nd
	 */
	public void add_traffic(Node_Distance nd){
		this.path =  nd.get_path_dijkstra(start, end);
		for (Street street : path) {
			street.traffic+=volume;
		}
	}
	
	/**
	 * Odeète hodnotu volume od všech ulic mezi startem a cílem.
	 *
	 * @param nd the nd
	 */
	public void remove_traffic(Node_Distance nd){
		for (Street street : path) {
			street.traffic-=volume;
		}
	}
}
