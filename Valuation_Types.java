package krabec.citysimulator;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * Ur�uje typy, podle kter�ch se ur�uje hodnota jednotliv�ch blok� v z�vislosti na jejich vyu�it�.
 * 
 */
public enum Valuation_Types implements Serializable{
	
	/** Zvy�uje hodnotu, pokud jsou bloky stejn�ho typu bl�zko u sebe.  */
	clustering,
	
	/** Zvy�uje hodnotu, pokud jsou bloky dan�ho typu bl�zko u bloku, jeho� hodnotu ur�ujeme */
	influence,
	
	/** Zvy�uje hodnotu, pokud je doprava na ulic�ch okolo vysok�. */
	traffic,
	
	/** Zvy�uje hodnotu, pokud je blok bl�zko centra m�sta. */
	citycenter,
	
	/** Konstantn� hodnota */
	constant
	
	
}
