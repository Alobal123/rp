package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import krabec.citysimulator.Building;
import krabec.citysimulator.Lut;

import java.awt.GridLayout;

public class Building_Lut_Window extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6768450950957361766L;


	ArrayList<Lut> luts;
	Building building;
	/**
	 * Create the dialog.
	 */
	public Building_Lut_Window(ArrayList<Lut> luts, Building building) {
		this.setTitle("Building Use");
		//this.setResizable(false);
		Building_Lut_Window thiswindow = this;
		setBounds(150, 100, 300, 600);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel panel = new Panelscrollable();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		panel.setLayout(new GridLayout(0, 1, 0, 10));
		for(Lut lut: luts){
			panel.add(new Building_Lut_panel(lut, building));
		}
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setVerticalScrollBarPolicy(
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrollPane.setHorizontalScrollBarPolicy(
				   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPane.setViewportView(panel);
		
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.RIGHT);
			fl_buttonPane.setAlignOnBaseline(true);
			buttonPane.setLayout(fl_buttonPane);
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						thiswindow.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		
	}

}
