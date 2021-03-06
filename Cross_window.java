package krabec.citysimulator.ui;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.GridLayout;
import java.awt.JobAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import krabec.citysimulator.Crossroad;
import krabec.citysimulator.Street_Network;


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
		this.setResizable(false);
		this.setTitle("Crossroads");
		Collections.sort(all_crossroads);
		Cross_window thiswindow = this;
		this.citywindow = citywindow;
		this.crossroads = (ArrayList<Crossroad>) all_crossroads;
		setBounds(200, 100, 600, 600);
		getContentPane().setLayout(new BorderLayout());

		panel = new Panelscrollable();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		for(Crossroad crossroad: remove_duplicates(crossroads)){
			Cross_panel cpanel = new Cross_panel(crossroad,this);
			panel.add(cpanel);
		}
		
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
		

		
		JButton new_cross_button = new JButton("New");
		panel_1.add(new_cross_button,BorderLayout.SOUTH);
		new_cross_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.add(new Cross_panel(new Crossroad(0, new ArrayList<>()), thiswindow));
				panel.updateUI();
			}
		});
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrollPane.setHorizontalScrollBarPolicy(
				   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		panel_1.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(panel);

	}
	
	
	private void create_crossroads(ArrayList<Crossroad> all_crossroads) {
		boolean succes = true;
		for(Component c: panel.getComponents()){
			if(c instanceof Cross_panel){
				succes = succes && create_crossroad(all_crossroads, (Cross_panel)c);
			}
		}
		if(!succes){
			JOptionPane.showMessageDialog(this, "Not all crossroads could be created.");
		}
	}
	
	private boolean create_crossroad(List<Crossroad> all_crossroads,Cross_panel panel){
		Crossroad c = Crossroad.Read_crossroad(panel.textfield.getText());
		boolean rt = true;
		if(control(c))
			all_crossroads.addAll(c.get_all_rotations());
		else
			rt = false;
		return rt;
			
	}
	
	private boolean control(Crossroad crossroad){
		if(crossroad == null)
			return false;
		double sum =0;
		if(crossroad.getNumber_of_roads() ==0|| crossroad.angles.size()==0)
			return false;
		if(crossroad.getNumber_of_roads() != crossroad.angles.size())
			return false;
		for (int i = 0; i < crossroad.getNumber_of_roads()-1; i++) {
			sum += crossroad.angles.get(i);
		}
		double last = crossroad.angles.get(crossroad.getNumber_of_roads()-1);
		if(sum + last  == 360)
			return true;
		else{
			crossroad.angles.set(crossroad.getNumber_of_roads()-1, 360-sum);
			return true;
		}
	}
	private ArrayList<Crossroad> remove_duplicates (ArrayList<Crossroad> all_crossroads){
		ArrayList<Crossroad> without_duplicates = new ArrayList<>();
		for(Crossroad c: all_crossroads){
			boolean contains = false;
			for(Crossroad c2 : without_duplicates){
				if(c2.get_all_rotations().contains(c))
					contains = true;
			}
			if(!contains)
				without_duplicates.add(c);
		}
		return without_duplicates;
		
	}


}
