package krabec.citysimulator;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Building_panel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Building building;
	ArrayList<Lut> luts;
	private JTextField x1;
	private JTextField y1;
	/**
	 * Create the panel.
	 */
	public Building_panel(Building building,ArrayList<Lut> luts) {
		this.luts = luts;
		this.building = building;
		setLayout(new GridLayout(2, 6, 0, 0));
		
		JTextField lblNewLabel = new JTextField(building.name);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(lblNewLabel);
		lblNewLabel.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  update();
			  }
			  public void removeUpdate(DocumentEvent e) {
				  update();
			  }
			  public void insertUpdate(DocumentEvent e) {
				  update();
			  }
			  private void update(){
				  try{
					 building.name = lblNewLabel.getText();
				  }
				  catch(NumberFormatException e){
					  
				  }
			  }
			   
		});
		
		JLabel lblNewLabel_2 = new JLabel("Front Edge");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblNewLabel_2);
		
		x1 = new JTextField();
		x1.setHorizontalAlignment(SwingConstants.CENTER);
		add(x1);
		x1.setColumns(10);
		x1.setText(Double.toString(building.get_front_length()));
		x1.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  update();
			  }
			  public void removeUpdate(DocumentEvent e) {
				  update();
			  }
			  public void insertUpdate(DocumentEvent e) {
				  update();
			  }
			  private void update(){
				  try{
					  double n = Double.parseDouble(x1.getText());
					  //building.points.get(0).x = 0;
					  building.place(new Point(0, 0));
					  building.points.get(1).y = n;
					  building.points.get(2).y = n;
					  building.place(new Point(0, 0));
					  System.out.println(building);
				  }
				  catch(NumberFormatException e){
					  
				  }
			  }
			   
		});
		
		JButton lblNewLabel_1 = new JButton("Luts");
		lblNewLabel_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Building_Lut_Window blw = new Building_Lut_Window(luts,building);
					blw.setVisible(true);
					blw.setAlwaysOnTop(true);
					blw.pack();
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_3 = new JLabel("Side Edge");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblNewLabel_3);
		
		y1 = new JTextField();
		y1.setHorizontalAlignment(SwingConstants.CENTER);
		add(y1);
		y1.setColumns(10);
		y1.setText(Double.toString(building.get_side_length()));
		y1.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  update();
			  }
			  public void removeUpdate(DocumentEvent e) {
				  update();
			  }
			  public void insertUpdate(DocumentEvent e) {
				  update();
			  }
			  private void update(){
				  try{
					  double n = Double.parseDouble(y1.getText());
					 // building.points.get(0).y = 0;
					  building.place(new Point(0, 0));
					  building.points.get(2).x = n;
					  building.points.get(3).x = n;
					  building.place(new Point(0, 0));
					  System.out.println(building);
				  }
				  catch(NumberFormatException e){
					  
				  }
			  }
			   
		});
	}

}
