package krabec.citysimulator;

import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class City.
 */
public class City {

	/** The citycenters. */
	List<Point> citycenters;
	
	/** The growthcenters. */
	List<Point> growthcenters;
	
	/** The lots. */
	List<Lot> lots;
	
	/** The streetgrowth. */
	double streetgrowth;
	
	/** The avgprice. */
	double avgprice;
	
	/** The luts. */
	Map<String, Land_use_type> luts;
	
	List<Crossroad> crossroads;
	
	/** The luv. */
	double luv;
	
	/** The time. */
	int time;
	
	/** The timestep. */
	int timestep;
	
	
	//heightmap, street pattern,setback price 
	
	/** The streets. */
	Street_Network streets;
	
	
	/**
	 * Step.
	 */
	public void step(){
		streets.grow_major_streets();
		
	}
	
	
	
	
	
	
	
	
}
