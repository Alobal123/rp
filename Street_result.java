package krabec.citysimulator;

import java.io.Serializable;

/**
 * Ud�v� v�sledek kontrol ulic.
 */
enum Street_result implements Serializable{
	
	/** Ulice nepro�la testem, je t�eba ji smazat a zkusit nar�st jinou ulici. */
	fail,
	
	/** Ulice pro�la testem, ale zm�nila se. */
	altered,
	
	/**Ulice pro�la testem a z�stala v p�vodn�m stavu*/
	not_altered
	
}
