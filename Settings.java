package krabec.citysimulator;

import java.io.Serializable;

/**
 * The Class Settings.
 */
public class Settings implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4577519080726186924L;

	/** Urèuje, jak moc se mìsto bude rozrùstat daleko od centra rùstu.*/
	public Double focus_constant = 2.0;
	
	/** Maximální délka hlavní ulice. */
	public Double major_max_length = 70.0;
	
	/** Minimálná délka hlavní ulice. */
	public Double major_min_length = 50.0;
	
	/** Maximální délka vedlejší ulice */
	public Double minor_max_length = 50.0;
	
	/** Minimální délka vedlejší ulice */
	public Double minor_min_length = 40.0;
	
	/** Urèuje, jak blízko mùe bıt hlavní uzel nìjakému jinému uzlu. */
	public Double major_close_node_constant = 12.0;
	
	/** Urèuje, jak blízko mùe bıt hlavní uzel nìjaké ulici. */
	public Double major_close_street_constant = 12.0;
	
	/** Urèuje, jak blízko mùe bıt vedlejší uzel nìjakému jinému uzlu. */
	public Double minor_close_node_constant = 12.0;
	
	/** Urèuje, jak blízko mùe bıt vedlejší uzel nìjaké ulici.  */
	public Double minor_close_street_constant = 12.0;
	
	/** Urèuje, o kolik se hlavní ulice pokusí prodlouit a protnout se s nìjakou jinou ulicí a vytvoøit tak ètvr.*/
	public Double major_prolongation = major_min_length;
	
	/** Urèuje, o kolik se vedlejší ulice pokusí prodlouit a protnout se s nìjakou jinou ulicí a vytvoøit tak blok. */
	public Double minor_prolongation = minor_min_length;
	
	/** Urèuje kolik procent uzlù si bude v jednom kroku pøepoèítávat dopravu. */
	public Double traffic_resample_rate = 0.05;
	
	/** Urèuje kolik procent blokù si bude v jednom kroku pøepoèítávat lut.*/
	public Double lut_resample_rate = 0.2;
	
	/** Kolik stojí pøestavìt jeden blok na jinı lut.*/
	public Double lut_resample_cost = 0.1;
	
	/** Urèuje cenu postavení ulice, tj. jaká musí bıt doprava na této ulici, aby byla postavena. */
	public Double build_cost = 1.0;
	
	/** Maximální hodnota dopravy na hlavních ulicích */
	public Double major_street_capacity = 30.0;
	
	/** Maximální hodnota dopravy na vedlejších ulicích. */
	public Double minor_street_capacity = 10.0;
	
	/**Šíøka ulice. */
	public Double street_width = 4.0;

	public Double street_offset = 1.0;

	public Double global_weight = 0.1;
	
}
