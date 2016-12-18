package krabec.citysimulator.ui;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import krabec.citysimulator.Lut;

public class Lut_window extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Lut> luts;
	City_window city_window;
	JPanel panel;

	public Lut_window(ArrayList<Lut> luts,City_window city_window) {
		setResizable(false);
		this.setTitle("Land Use Types");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				city_window.setEnabled(true);
				
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.luts = luts;
		this.city_window = city_window;
		Lut_window thiswindow = this;
		city_window.setEnabled(false);
		setBounds(200, 100, 525, 525);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new Panelscrollable();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		for(Lut lut:luts){
			Lut_panel lpanel = new Lut_panel(lut,thiswindow);
			panel.add(lpanel);
		}
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JButton new_lut_button = new JButton("New");
		panel_1.add(new_lut_button, BorderLayout.SOUTH);
		new_lut_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.add(new Lut_panel(new Lut("New Lut",1,0,Color.black,city_window.city.getSettings()),thiswindow));
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
						city_window.city.luts = load_luts();
						city_window.setEnabled(true);
						thiswindow.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			
			JScrollPane scrollPane = new JScrollPane();
			panel_1.add(scrollPane, BorderLayout.CENTER);
			scrollPane.setVerticalScrollBarPolicy(
					   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
			scrollPane.setHorizontalScrollBarPolicy(
					   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
			scrollPane.setViewportView(panel);

			{
				JPanel panel_2 = new JPanel();
				scrollPane.setColumnHeaderView(panel_2);
				panel_2.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblNewLabel = new JLabel("Color");
					panel_2.add(lblNewLabel, BorderLayout.WEST);
				}
				{
					JLabel lblNewLabel_1 = new JLabel("Name");
					lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
					panel_2.add(lblNewLabel_1, BorderLayout.CENTER);
				}
				{
					JPanel panel_3 = new JPanel();
					panel_2.add(panel_3, BorderLayout.EAST);
					{
						JLabel lblNewLabel_2 = new JLabel("Wanted Percentage");
						panel_3.add(lblNewLabel_2);
					}
				}
			}
		}
		
	}
	public ArrayList<Lut> load_luts(){
		ArrayList<Lut> new_luts = new ArrayList<>();
		for(Component c: panel.getComponents()){
			if(c instanceof Lut_panel){
				new_luts.add(((Lut_panel)c).lut);
			}
		}
		return new_luts;
		
	}

}
