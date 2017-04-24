package krabec.citysimulator;

import java.io.Serializable;

/**
 * Udává výsledek kontrol ulic.
 */
enum Street_result implements Serializable{
	
	/** Ulice neprošla testem, je tøeba ji smazat a zkusit narùst jinou ulici. */
	fail,
	
	/** Ulice prošla testem, ale zmìnila se. */
	altered,
	
	/**Ulice prošla testem a zùstala v pùvodním stavu*/
	not_altered
	
}
