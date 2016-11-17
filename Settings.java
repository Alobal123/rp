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

	/** Ur�uje, jak moc se m�sto bude rozr�stat daleko od centra r�stu.*/
	public Double focus_constant = 2.0;
	
	/** Maxim�ln� d�lka hlavn� ulice. */
	public Double major_max_length = 0.6;
	
	/** Minim�ln� d�lka hlavn� ulice. */
	public Double major_min_length = 0.4;
	
	/** Maxim�ln� d�lka vedlej�� ulice */
	public Double minor_max_length = 0.3;
	
	/** Minim�ln� d�lka vedlej�� ulice */
	public Double minor_min_length = 0.2;
	
	/** Ur�uje, jak bl�zko m��e b�t hlavn� uzel n�jak�mu jin�mu uzlu. */
	public Double major_close_node_constant = 0.03;
	
	/** Ur�uje, jak bl�zko m��e b�t hlavn� uzel n�jak� ulici. */
	public Double major_close_street_constant = 0.1;
	
	/** Ur�uje, jak bl�zko m��e b�t vedlej�� uzel n�jak�mu jin�mu uzlu. */
	public Double minor_close_node_constant = 0.07;
	
	/** Ur�uje, jak bl�zko m��e b�t vedlej�� uzel n�jak� ulici.  */
	public Double minor_close_street_constant = 0.07;
	
	/** Ur�uje, o kolik se hlavn� ulice pokus� prodlou�it a protnout se s n�jakou jinou ulic� a vytvo�it tak �tvr�.*/
	public Double major_prolongation = 0.5;
	
	/** Ur�uje, o kolik se vedlej�� ulice pokus� prodlou�it a protnout se s n�jakou jinou ulic� a vytvo�it tak blok. */
	public Double minor_prolongation = 0.2;
	
	/** Ur�uje kolik procent uzl� si bude v jednom kroku p�epo��t�vat dopravu. */
	public Double traffic_resample_rate = 0.05;
	
	/** Ur�uje kolik procent blok� si bude v jednom kroku p�epo��t�vat lut.*/
	public Double lut_resample_rate = 0.2;
	
	/** Kolik stoj� p�estav�t jeden blok na jin� lut.*/
	public Double lut_resample_cost = 0.1;
	
	/** Ur�uje cenu postaven� ulice, tj. jak� mus� b�t doprava na t�to ulici, aby byla postavena. */
	public Double build_cost = 3.0;
	
	/** Maxim�ln� hodnota dopravy na hlavn�ch ulic�ch */
	public Double major_street_capacity = 50.0;
	
	/** Maxim�ln� hodnota dopravy na vedlej��ch ulic�ch. */
	public Double minor_street_capacity = 20.0;
	
	/**���ka ulice. */
	public Double street_width = 0.015;

	public Double global_weight = 0.5;
	
}
