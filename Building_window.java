package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import krabec.citysimulator.Building;
import krabec.citysimulator.Lut;

import java.awt.GridLayout;

public class Building_window extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8514822025514752082L;
	private JPanel contentPane;
	ArrayList<Lut> luts;

	/**
	 * Create the frame.
	 */	
	public Building_window(ArrayList<Lut> luts) {
		this.setTitle("Buildings");
		this.setResizable(false);
		Building_window thiswindow = this;
		setBounds(200, 100, 640, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1,BorderLayout.CENTER);
		
		JPanel panel = new Panelscrollable();
		panel.setLayout(new GridLayout(0, 1, 0, 10));
		
		HashSet<Building> buildings =  new HashSet<>();
		for(Lut lut: luts){
			buildings.addAll(lut.getBuildings());
		}
		for(Building b: buildings){
			panel.add(new Building_panel(b, luts));
		}
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrollPane.setHorizontalScrollBarPolicy(
				   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPane.setViewportView(panel);
		{
			scrollPane.setViewportView(panel);
		}
		
		
		JButton new_lut_button = new JButton("New");
		panel_1.add(new_lut_button,BorderLayout.SOUTH);
		new_lut_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.add(new Building_panel(new Building(),luts));
				panel.updateUI();
			}
		});
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						for(Lut lut: luts){
							lut.find_min_area();
						}
						thiswindow.dispose();
					}
				});
				getRootPane().setDefaultButton(okButton);
			}

		}
		
		
	}

}
