package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Reprezentuje cestu n�jak�ch obyvatel m�sta z jednoho m�sta na jin�.
 */
public class Trip implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2612659907294785389L;

	/**Startovn� uzel tripu*/
	Node start;
	
	/** C�lov� uzel tripu */
	Node end;
	
	/** Nejkrat�� cesta ze startu do c�le */
	ArrayList<Street> path;
	
	/** Po�et obyvatel na tripu */
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
	 * P�i�te hodnotu volume k doprav�, ke v�em ulic�m mezi startem a c�lem a zapamatuje si tuto nejkrat�� cestu.
	 *
	 * @param nd the nd
	 */
	public void add_traffic(Node_Distance nd, Street_Network network){
		this.path = nd.get_Path(start, end,network);
		for (Street street : path) {
			street.traffic+=volume;
		}
	}
	
	/**
	 * Ode�te hodnotu volume od v�ech ulic mezi startem a c�lem.
	 *
	 * @param nd the nd
	 */
	public void remove_traffic(Node_Distance nd){
		for (Street street : path) {
			street.traffic-=volume;
		}
	}
}
