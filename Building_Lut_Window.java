package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import krabec.citysimulator.Building;
import krabec.citysimulator.Lut;

import java.awt.GridLayout;

public class Building_Lut_Window extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6768450950957361766L;

	private final JPanel contentPanel = new JPanel();

	ArrayList<Lut> luts;
	Building building;
	/**
	 * Create the dialog.
	 */
	public Building_Lut_Window(ArrayList<Lut> luts, Building building) {
		this.setTitle("Building Use");
		Building_Lut_Window thiswindow = this;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 1, 0, 10));
		for(Lut lut: luts){
			panel.add(new Building_Lut_panel(lut, building));
		}
		
		
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
