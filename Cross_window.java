package krabec.citysimulator;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class Cross_window extends JDialog {
	
	private static final long serialVersionUID = 1L;
	ArrayList<Crossroad> crossroads;
	City_window citywindow;
	JPanel panel;
	/**
	 * Create the dialog.
	 * @param citywindow 
	 * @param all_crossroads 
	 */
	public Cross_window(List<Crossroad> all_crossroads, City_window citywindow) {
		this.setTitle("Crossroads");
		Cross_window thiswindow = this;
		this.citywindow = citywindow;
		this.crossroads = (ArrayList<Crossroad>) all_crossroads;
		setBounds(100, 100, 624, 528);
		getContentPane().setLayout(new BorderLayout());
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		getContentPane().add(panel);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNumberOfRoads = new JLabel("   Number of roads");
	
		lblNumberOfRoads.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblNumberOfRoads, BorderLayout.WEST);
		
		JLabel lblAngles = new JLabel("Angles");
		lblAngles.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblAngles, BorderLayout.CENTER);
		for(Crossroad crossroad: remove_duplicates(crossroads)){
			Cross_panel cpanel = new Cross_panel(crossroad,this);
			panel.add(cpanel);
		}
		
		
		JButton new_cross_button = new JButton("New");
		panel.add(new_cross_button);
		new_cross_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(new_cross_button);
				panel.add(new Cross_panel(new Crossroad(0, new ArrayList<>()), thiswindow));
				panel.add(new_cross_button);
				panel.updateUI();
			}
		});
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<Crossroad> all_crossroads = new ArrayList<>();
						create_crossroads(all_crossroads);
						all_crossroads.add(Street_Network.end_of_road);
						Street_Network.end_of_road.viable_crossroads = new ArrayList<>();
						for (Crossroad c: all_crossroads) {
							c.get_viable_crossroads(all_crossroads);
						}
						citywindow.city.network.all_crossroads = all_crossroads;
						thiswindow.dispose();	
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						thiswindow.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	
	private void create_crossroads(ArrayList<Crossroad> all_crossroads) {
		for(Component c: panel.getComponents()){
			if(c instanceof Cross_panel){
				create_crossroad(all_crossroads, (Cross_panel)c);
			}
		}
	}
	
	private void create_crossroad(List<Crossroad> all_crossroads,Cross_panel panel){
		Crossroad c = new Crossroad(Integer.parseInt(panel.fieldn.getText()), new ArrayList<>());
		for (JTextField jtf: panel.fields) {
			double angle = 0;
			try{
				angle = Double.parseDouble(jtf.getText());
				c.angles.add(angle);
			}
			catch(NumberFormatException e){	
			}
			
		}
		if(control(c))
			all_crossroads.addAll(c.get_all_rotations());
	}
	
	private boolean control(Crossroad crossroad){
		double sum =0;
		if(crossroad.number_of_roads ==0|| crossroad.angles.size()==0)
			return false;
		if(crossroad.number_of_roads != crossroad.angles.size())
			return false;
		for (int i = 0; i < crossroad.number_of_roads-1; i++) {
			sum += crossroad.angles.get(i);
		}
		double last = crossroad.angles.get(crossroad.number_of_roads-1);
		if(sum + last  == 360)
			return true;
		else{
			crossroad.angles.set(crossroad.number_of_roads-1, 360-sum);
			return true;
		}
	}
	private ArrayList<Crossroad> remove_duplicates (ArrayList<Crossroad> all_crossroads){
		ArrayList<Crossroad> without_duplicates = new ArrayList<>();
		for(Crossroad c: all_crossroads){
			if(!without_duplicates.contains(c))
				without_duplicates.add(c);
		}
		return without_duplicates;
		
	}


}
