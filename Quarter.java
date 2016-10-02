package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Quarter.
 */
public class Quarter extends City_part implements Serializable{
	
	/** The blocks. */
	List<Block> blocks = new ArrayList<>();
	int number;
	/**
	 * Instantiates a new quarter.
	 *
	 * @param main_streets the main streets
	 * @param firstnode the firstnode
	 */
	public Quarter(List<Street> main_streets, Node firstnode){
		this.streets = main_streets;
		this.firstnode = firstnode;
		find_area();
	}
	

		
	
	
}
