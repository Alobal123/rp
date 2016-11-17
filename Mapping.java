package krabec.citysimulator;

import java.io.Serializable;

/**
 * Mapuje hodnotu do intervalu [0,1]. 
 */
public enum Mapping implements Serializable{

	/** Pokud je hodnota alespo� min, pak je to 1, jinak 0. */
	treshold_up,
	
	/** Pokud je hodnota alespo� min, pak je to 0, jinak 1. */
	treshold_down,
	
	/** Line�rn� namapuje hodnoty z intervalu [min,max] do intervalu [0,1], tak, �e p��mka kles�.*/
	linear_down,
	
	/** Line�rn� namapuje hodnoty z intervalu [min,max] do intervalu [0,1], tak, �e p��mka stoup�.*/
	linear_up,
	
	/**Vrac� konstant� hodnotu min, nez�visle na hodnot�. */
	constant;
	
	
	/**
	 *Mapuje hodnotu do intervalu [0,1] v z�vislosti na typu.
	 *
	 * @param value Hodnota
	 * @param min Minimum
	 * @param max Maximum
	 * @param mapping Typ mapov�n�
	 * @return Namapovan� hodnota
	 */
	public static double map(double value,double min,double max,Mapping mapping){
		switch(mapping){
		case linear_down:
			if(value <= min)
				return 1;
			if(value >= max)
				return 0;
			double a = 1/(min - max);
			double b = max/(max-min);
			return a*value+b;
			
		case linear_up:
			if(value <= min)
				return 0;
			if(value >= max)
				return 1;
			a = 1/(max - min);
			b = min/(min -max);
			return a*value+b;	
			
		case treshold_down:
			if(value<min)
				return 1;
			return 0;
			
		case treshold_up:
			if(value < min)
				return 0;
			return 1;
		case constant:
			if(value<0)
				return 0;
			if(value > 1)
				return 1;
			return value;

		default:
			break;
		
		}
		return 0;
		
	}
}
