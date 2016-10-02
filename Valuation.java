package krabec.citysimulator;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * T��da ur�uj�c� zp�sob ohodnocen� bloku.
 */
public class Valuation implements Serializable{

	/** V�ha s jakou se zapo��t�v� tato hodnota do celkov� hodnoty pozemku. */
	float weight;
	
	/** Typ ohodnocen� */
	Valuation_Types type;
	
	/** S bl�zkost� jak�ho lutu se po��t� p�i ohodnocovan� tohoto bloku*/
	Lut influencing_lut;
	
	/** Jak se mapuje hodnota do intervalu [0,1] */
	Mapping mapping;
	
	/** Jak� je b�t minim�ln� hodnota v mapov�n�. */
	double min = 0;
	
	/** Jak� je b�t maxim�ln� hodnota v mapov�n�. */
	double max = 1;
	
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
		this.weight = weight;
		this.type = type;
		this.mapping = mapping;
		this.min = min;
		this.max = max;
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
		this.weight = weight;
		this.type = type;
		this.influencing_lut = influencing_lut;
		this.mapping = mapping;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Pro dan� blok n�m d� jeho hodnotu.
	 *
	 * @param network Graf ulic
	 * @param block Blok jeho� hodnotu ur�ujeme
	 * @param nd the nd
	 * @return Hodnota
	 */
	public double get_value(Street_Network network, Block block, Node_Distance nd){
		return Mapping.map(get_non_mapped_value(network, block, nd), min, max, mapping);
	}
	
	/**
	 * Vr�t� hodnotu tohoto bloku, ale ne v intervalu [0,1]. 
	 *
	 * @param network Graf ulic
	 * @param block Blok jeho� hodnotu ur�ujeme
	 * @param nd the nd
	 * @return Hodnota
	 */
	private double get_non_mapped_value(Street_Network network, Block block, Node_Distance nd){
		switch (type){
		case clustering:
			ArrayList<Block> blocks = network.get_nearest_blocks(12, block);
			int same = 0;
			int different = 0;
			for(Block b: blocks){
					if(b.built && b.lut == influencing_lut)
						same++;
					else
						different++;
				
			}
			//System.out.println("Same " + same + " Differenr " + different);
			if(same + different == 0)
				return 0;
			return (double)same/(same+different);
			
		case influence:
			double radius = 2;
			double sum = 0;
			double influencing = 0;
			blocks = network.get_blocks_in_radius(radius, block);
			for(Block b: blocks){
				if(b.built){
					double dist = Point.dist(b.center, block.center)*b.area/radius;
					if(b.lut == influencing_lut)
						influencing += dist;
					sum+=dist;
				}
			}
			if(sum == 0)
				return 0;
			//System.out.println(influencing/sum);
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
			return min;
		
		default:
			break;
		}
		
		
		return 0;
	}
	
	
	
	
}
