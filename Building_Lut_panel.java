package krabec.citysimulator;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;


public class Building_Lut_panel extends JPanel {

	/**
	 * Create the panel.
	 */
	Lut lut;
	
	/** The building. */
	Building building;
	
	@Override
	public  Dimension getPreferredSize(){
		return new Dimension(220, 50);
	}
	
	/**
	 * Instantiates a new building lut panel.
	 *
	 * @param lut the lut
	 * @param building the building
	 */
	public Building_Lut_panel(Lut lut, Building building) {
		this.lut = lut;
		this.building = building;
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setText(lut.name);
		add(lblNewLabel);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxNewCheckBox.isSelected()){
					lut.buildings.add(building);
					lut.find_min_area();	
				}
				else{
					lut.buildings.remove(building);
					lut.find_min_area();
				}
			}
		});
		chckbxNewCheckBox.setSelected(lut.buildings.contains(building));
		add(chckbxNewCheckBox, BorderLayout.EAST);
	}
	

}
