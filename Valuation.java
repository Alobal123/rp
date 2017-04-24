package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Tøída urèující zpùsob ohodnocení bloku.
 */
public class Valuation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7654011403473672612L;

	/** Váha s jakou se zapoèítává tato hodnota do celkové hodnoty pozemku. */
	private float weight;
	
	/** Typ ohodnocení */
	private Valuation_Types type;
	
	/** S blízkostí jakého lutu se poèítá pøi ohodnocovaní tohoto bloku*/
	private Lut influencing_lut;
	
	/** Jak se mapuje hodnota do intervalu [0,1] */
	private Mapping mapping;
	
	/** Jaká je být minimální hodnota v mapování. */
	private double min = 0;
	
	/** Jaká je být maximální hodnota v mapování. */
	private double max = 1;
	
	/**
	 * Konstruktor
	 *
	 * @param weight the weight
	 * @param type the type
	 * @param mapping the mapping
	 * @param min the min
	 * @param max the max
	 */
	public Valuation(float weight, Valuation_Types type,Mapping mapping,double min, double max){
		this.setWeight(weight);
		this.setType(type);
		this.setMapping(mapping);
		this.setMin(min);
		this.setMax(max);
	}
	
	/**
	 * Konstruktor
	 *
	 * @param weight the weight
	 * @param type the type
	 * @param influencing_lut the influencing lut
	 * @param mapping the mapping
	 * @param min the min
	 * @param max the max
	 */
	public Valuation(float weight, Valuation_Types type,Lut influencing_lut,Mapping mapping,double min, double max){
		this.setWeight(weight);
		this.setType(type);
		this.setInfluencing_lut(influencing_lut);
		this.setMapping(mapping);
		this.setMin(min);
		this.setMax(max);
	}
	
	/**
	 * Pro daný blok nám dá jeho hodnotu.
	 *
	 * @param network Graf ulic
	 * @param block Blok jehož hodnotu urèujeme
	 * @param nd the nd
	 * @return Hodnota
	 */
	public double get_value(Street_Network network, Block block, Node_Distance nd,Settings settings){
		return Mapping.map(get_non_mapped_value(network, block, nd, settings), getMin(), getMax(), getMapping());
	}
	
	/**
	 * Vrátí hodnotu tohoto bloku, ale ne v intervalu [0,1]. 
	 *
	 * @param network Graf ulic
	 * @param block Blok jehož hodnotu urèujeme
	 * @param nd the nd
	 * @return Hodnota
	 */
	private double get_non_mapped_value(Street_Network network, Block block, Node_Distance nd,Settings settings){
		switch (getType()){
		case clustering:
			ArrayList<Block> blocks = network.get_nearest_blocks(50, block);
			int same = 0;
			int different = 0;
			for(Block b: blocks){
					if(b.built && b.lut == getInfluencing_lut())
						same++;
					else
						different++;
				
			}
			if(same + different == 0)
				return 0;
			return (double)same/(same+different);
			
		case influence:
			double radius = settings.minor_max_length*3;
			double sum = 0;
			double influencing = 0;
			blocks = network.get_blocks_in_radius(radius, block);
			for(Block b: blocks){
				if(b.built){
					double dist = Point.dist(b.center, block.center)/radius;
					if(b.lut == getInfluencing_lut())
						influencing += dist;
					sum+=dist;
				}
			}
			if(sum == 0)
				return 0;
			
			return influencing/sum;
			
		case traffic:
			sum = 0;
			for (Street s : block.streets) {
				sum += s.traffic;
			}
			return sum/block.streets.size();
			
		case citycenter:
			return Point.get_smallest_distance(block.center, network.citycenters);
		case constant:
			return getMin();
		
		default:
			break;
		}
		
		
		return 0;
	}

	public Lut getInfluencing_lut() {
		return influencing_lut;
	}

	public void setInfluencing_lut(Lut influencing_lut) {
		this.influencing_lut = influencing_lut;
	}

	public Valuation_Types getType() {
		return type;
	}

	public void setType(Valuation_Types type) {
		this.type = type;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	
	
	
}
