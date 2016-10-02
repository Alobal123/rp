package krabec.citysimulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;

public class Building_window extends JDialog {

	private JPanel contentPane;
	ArrayList<Lut> luts;

	/**
	 * Create the frame.
	 */	
	public Building_window(ArrayList<Lut> luts) {
		this.setTitle("Buildings");
		Building_window thiswindow = this;
		setBounds(100, 100, 766, 482);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 1, 0, 10));
		
		HashSet<Building> buildings =  new HashSet<>();
		for(Lut lut: luts){
			buildings.addAll(lut.buildings);
		}
		for(Building b: buildings){
			panel.add(new Building_panel(b, luts));
		}
		
		JButton new_lut_button = new JButton("New");
		panel.add(new_lut_button);
		new_lut_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(new_lut_button);
				panel.add(new Building_panel(new Building(),luts));
				panel.add(new_lut_button);
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
						thiswindow.dispose();
					}
				});
				getRootPane().setDefaultButton(okButton);
			}

		}
		
		
	}

}
