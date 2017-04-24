package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class Quarter.
 */
public class Quarter extends City_part implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5973932903980349268L;
	/**
	 * Instantiates a new quarter.
	 *
	 * @param main_streets the main streets
	 * @param firstnode the firstnode
	 */
	public Quarter(List<Street> main_streets, Node firstnode){
		this.streets = main_streets;
		this.firstnode = firstnode;
		compute_area();
	}
	

		
	
	
}
