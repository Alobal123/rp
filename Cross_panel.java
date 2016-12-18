package krabec.citysimulator.ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import krabec.citysimulator.Crossroad;
import java.awt.FlowLayout;
import javax.swing.JLabel;


public class Cross_panel extends JPanel{

	private static final long serialVersionUID = 1L;
	Crossroad crossroad;
	public JTextField textfield;
	Cross_window parent;
	
	@Override
	public  Dimension getPreferredSize(){
		return new Dimension(565, 40);
	}
	
	/**
	 * Create the panel.
	 * @param crossroad 
	 */
	public Cross_panel(Crossroad crossroad, Cross_window parent) {

		
		this.crossroad = crossroad;
		this.parent = parent;
		Cross_panel thispanel = this;
		setLayout(new BorderLayout(0, 0));
		
		JTextField textfield = new JTextField(crossroad.toString());
		add(textfield, BorderLayout.CENTER);
		this.textfield = textfield;
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton remove_button = new JButton("X");
		panel.add(remove_button);
		
		JLabel lblNewLabel_1 = new JLabel("Crossroad : ");
		add(lblNewLabel_1, BorderLayout.WEST);
		remove_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.panel.remove(thispanel);	
				parent.panel.updateUI();
			}
		});
		
		
		

	}

}
