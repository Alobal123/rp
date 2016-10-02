package krabec.citysimulator;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * Urèuje typy, podle kterých se urèuje hodnota jednotlivých blokù v závislosti na jejich využití.
 * 
 */
public enum Valuation_Types implements Serializable{
	
	/** Zvyšuje hodnotu, pokud jsou bloky stejného typu blízko u sebe.  */
	clustering,
	
	/** Zvyšuje hodnotu, pokud jsou bloky daného typu blízko u bloku, jehož hodnotu urèujeme */
	influence,
	
	/** Zvyšuje hodnotu, pokud je doprava na ulicích okolo vysoká. */
	traffic,
	
	/** Zvyšuje hodnotu, pokud je blok blízko centra mìsta. */
	citycenter,
	
	/** Konstantní hodnota */
	constant
	
	
}
